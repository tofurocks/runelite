package com.runemax.bot.scripts.combat.muling;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.itemcontainer.equipment.Equipment;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.itemcontainer.trade.Trade;
import com.runemax.bot.api.script.Task;
import com.runemax.bot.api.skill.Skills;
import com.runemax.bot.api.tabs.Tab;
import com.runemax.bot.api.tabs.Tabs;
import com.runemax.bot.api.worlds.Worlds;
import com.runemax.bot.api.wrappers.entity.actor.player.Player;
import com.runemax.bot.scripts.Store;
import com.runemax.bot.scripts.combat.CombatStore;
import com.runemax.bot.scripts.muling.ItemRequest;
import com.runemax.bot.scripts.muling.RestockAcknowledgement;
import com.runemax.bot.scripts.muling.RestockOffer;
import com.runemax.bot.scripts.muling.RestockRequest;
import com.runemax.bot.tasks.subtasks.WalkTask;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.Skill;

import java.io.IOException;

import static com.runemax.bot.Constants.GRAND_EXCHANGE;
import static com.runemax.bot.Constants.POINT_GRAND_EXCHANGE;
import static com.runemax.bot.api.commons.Rand.gaussian;
import static com.runemax.bot.scripts.combat.muling.GetMagicSupplies.RELOAD_STATE.REQUEST_NOT_SENT;
import static com.runemax.bot.scripts.combat.muling.GetMagicSupplies.RELOAD_STATE.REQUEST_SENT;
import static net.runelite.api.ItemID.MIND_RUNE;
import static net.runelite.api.ItemID.STAFF_OF_AIR;

@Slf4j
public class GetMagicSupplies extends Task {
    RELOAD_STATE state = REQUEST_NOT_SENT;
    Channel channel;
    RestockOffer offer = null;
    WalkTask walkToGrandExchange = new WalkTask("walk to GE", POINT_GRAND_EXCHANGE);
    boolean seenSecondTradeScreen = false; //Have we seen the second trade screen yet
    int strike = 0;
    int tradeAttempt = 0;

    public GetMagicSupplies() {
        channel = Store.getChannel();
        try {
            createOfferListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean activate() {
        if (Skills.getLevel(Skill.ATTACK) < 30 || Skills.getLevel(Skill.DEFENCE) < 30){
            return false; //Not done training melee yet
        }
        if (Inventory.first("Mind rune").isPresent()
                && (Inventory.first("Staff of air").isPresent() || (Equipment.isOccupied(EquipmentInventorySlot.WEAPON) && Equipment.inSlot(EquipmentInventorySlot.WEAPON).getName().equals("Staff of air")))) {
            //We already have a staff of air and mind runes, we don't need to get supplies
            return false;
        }
        /** If we are missing a staff of air, we need to get magic supplies */
        if (Inventory.first("Staff of air").isEmpty() && !Equipment.inSlot(EquipmentInventorySlot.WEAPON).getName().equals("Staff of air")) {
            log.info("We have no staff of air, we need to get magic supplies");
            return true;
        }
        return false;
    }

    @Override
    public int execute() {
        log.info("State is " + state.toString());
        if(Worlds.getCurrentWorldId() != 470){
            log.info("Hopping to world 470 to trade mule");
            Worlds.switchTo(Worlds.get(470));
            return Rand.nextInt(5*1000, 10*1000);
        }
        if (Trade.getView().equals(Trade.View.CLOSED) && seenSecondTradeScreen) {
            /** We have already seen the second trade screen and we are not in a trade, we probably completed our trade already */
            log.info("We have seen second trade screen already and we are not in a trade, setting stuff needed to false");
            CombatStore.setStuffNeeded(false);
            return 0;
        }
        switch (state) {
            case REQUEST_NOT_SENT:
                requestStuff();
                state = REQUEST_SENT;
                break;
            case REQUEST_SENT:
                if (strike > 5) {
                    log.info("We never received an offer after 6 strikes, setting state back to REQUEST_NOT_SENT");
                    state = REQUEST_NOT_SENT;
                    strike = 0;
                    return 0;
                }
                strike++;
                log.info("Waiting to receive an offer, strike " + strike);
                Sleep.sleep(5000);
                break;
            case OFFER_RECEIVED:
                acknowledgeOffer();
                break;
            case ACKNOWLEDGEMENT_SENT:
                log.info("Acknowledgement sent, going to trade mule");
                tradeMule();
                break;
        }
        return 0;
    }

    private void tradeMule() {
        Player mule = Players.closest(offer.getHandle());
        if (!mule.isPresent()) {
            log.info("Walking to grand exchange to trade mule");
            walkToGrandExchange.execute(null);
            Sleep.sleep(gaussian(50, 15 * 1000, 1500, 1000));
            return;
        }
        if (Trade.getView() == Trade.View.CLOSED) {
            if(tradeAttempt > 5){
                log.info("We tried to trade the mule over 5 times and they never traded us back, expiring everthing");
                strike = 0;
                tradeAttempt = 0;
                state = REQUEST_NOT_SENT;
                offer = null;
                return;
            }
            tradeAttempt++;
            log.info("Trading mule, attempt " + tradeAttempt);
            mule.interact("Trade");
            Sleep.until(() -> Trade.getView() == Trade.View.FIRST, Rand.nextInt(10 * 1000, 20 * 1000));
            return;
        } else if (Trade.getView() == Trade.View.FIRST) {
            log.info("We are in the first trade screen, accepting");
            Sleep.sleep(5000);
            Trade.accept();
            return;
        } else if (Trade.getView() == Trade.View.SECOND) {
            log.info("We are in the second trade screen, setting seen second trade screen to true and accepting");
            Sleep.sleep(5000);
            Trade.accept();
            return;
        }
    }

    private void createOfferListener() throws IOException {
        log.info("Creating offer listener");
        channel.queueDeclare(Players.getLocal().getName(), false, false, false, null);
        String ourHandle = Players.getLocal().getName();

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            String message = new String(delivery.getBody(), "UTF-8");
            offer = new ObjectMapper().readValue(message, RestockOffer.class);
            System.out.println("Received restock offer: " + offer.toString() + " setting state to OFFER_RECEIVED");
            state = RELOAD_STATE.OFFER_RECEIVED;
        };
        channel.basicConsume(ourHandle, true, deliverCallback, consumerTag -> {
        });
    }

    private void requestStuff() {
        try {
            System.out.println("Sending restock request to requests exchange and clicking a random tab");
            int choice = Rand.nextInt(0, 4);
            if (choice == 0) {
                Tabs.open(Tab.INVENTORY);
            } else if (choice == 1) {
                Tabs.open(Tab.QUESTS);
            } else if (choice == 2) {
                Tabs.open(Tab.EQUIPMENT);
            } else if (choice == 3) {
                Tabs.open(Tab.PRAYER);
            }

            RestockRequest request = new RestockRequest();
            request.setHandle(Players.getLocal().getName());
            if (Inventory.first("Staff of air").isEmpty() && !Equipment.inSlot(EquipmentInventorySlot.WEAPON).getName().equals("Staff of air")) {
                log.info("We have no staff of air, adding to request");
                request.getItemRequestList().add(new ItemRequest(STAFF_OF_AIR, 1));
            }
            if (Inventory.count("Mind rune") < 100) {
                log.info("We need mind runes, adding to request");
                request.getItemRequestList().add(new ItemRequest(MIND_RUNE, 10 * 1000));
            }
            byte[] message = new ObjectMapper().writeValueAsBytes(request);
            channel.basicPublish("requests", "", null, message);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void acknowledgeOffer() {
        try {
            System.out.println("Acknowledging restock offer");
            String theirHandle = offer.getHandle();
            RestockAcknowledgement acknowledgement = new RestockAcknowledgement();
            acknowledgement.setHandle(Players.getLocal().getName());
            Channel channel = Store.getChannel();
            byte[] message = new ObjectMapper().writeValueAsBytes(acknowledgement);
            channel.queueDeclare(theirHandle, false, false, false, null);
            channel.basicPublish("", theirHandle, null, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        log.info("Setting state to ACKNOWLEDGEMENT_SENT");
        state = RELOAD_STATE.ACKNOWLEDGEMENT_SENT;
    }

    enum RELOAD_STATE {
        REQUEST_NOT_SENT,
        REQUEST_SENT,
        OFFER_RECEIVED,
        ACKNOWLEDGEMENT_SENT
    }
}
