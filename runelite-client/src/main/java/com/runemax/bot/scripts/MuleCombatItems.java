package com.runemax.bot.scripts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import com.runemax.bot.api.commons.Rand;
import com.runemax.bot.api.commons.Sleep;
import com.runemax.bot.api.entities.actors.players.Players;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.api.itemcontainer.inventory.Inventory;
import com.runemax.bot.api.itemcontainer.trade.Trade;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;
import com.runemax.bot.api.tabs.Tab;
import com.runemax.bot.api.tabs.Tabs;
import com.runemax.bot.api.worlds.Worlds;
import com.runemax.bot.api.wrappers.entity.actor.player.Player;
import com.runemax.bot.scripts.muling.*;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static com.runemax.bot.api.commons.Rand.gaussian;

/**
 * Provide combat supplies (scims, runes, staffs) to accounts that request them over rabbitMQ
 */
@ScriptMeta("CombatMuler")
@Slf4j
public class MuleCombatItems extends BotScript {
    RestockRequest currentRequest = null;
    boolean acknowledged = false;
    Channel channel;
    Timer timer;
    int tradesAttempted = 0;

    @Override
    protected void loop() {
        if (Inventory.count("Mind rune") < 10 * 1000 || Inventory.first("Staff of air").isEmpty()) {
            log.info("Out of items, stopping");
            this.stopLooping();
            return;
        }
        /**if (Trade.getView().equals(Trade.View.CLOSED) && seenSecondTradeScreen) {
         // We have already seen the second trade screen and we are not in a trade, we probably completed our trade already
         log.info("We have seen second trade screen already and we are not in a trade, expiring everything");
         expireRequest();
         return;
         }*/
        if (acknowledged == true) {
            /** Our offer was acknowledged, we should trade guy */
            log.info("Acknowledged is true");
            String theirHandle = currentRequest.getHandle();
            Player recipient = Players.closest(theirHandle);
            if (!recipient.isPresent()) {
                log.info("Recipient " + currentRequest.getHandle() + " is not here yet");
                //log.info("Our request was at " + currentRequest.getTime() + " and the current time is " + System.currentTimeMillis());
                long difference = (System.currentTimeMillis() - currentRequest.getTime());
                log.info("Seconds since request: " + difference / 1000);
                Sleep.sleep(gaussian(50, 15 * 1000, 5 * 1000, 2500));
                return;
            } else if (Trade.getView().equals(Trade.View.CLOSED)) {
                log.info("Recipient is here!");
                if (recipient.getWorldLocation().distanceTo(Players.getLocal().getWorldLocation()) > 1) {
                    log.info("Waiting for recipient to walk to us");
                    Sleep.sleep(gaussian(50, 15 * 1000, 5 * 1000, 2500));
                    return;
                } else {
                    if (tradesAttempted > 5) {
                        log.info("We tried to trade over 5 times and they didn't trade us back, expiring request");
                        expireRequest();
                        return;
                    }
                    tradesAttempted++;
                    log.info("Trading recipient, attempt " + tradesAttempted);
                    recipient.interact("Trade");
                    Sleep.until(() -> Trade.getView().equals(Trade.View.FIRST), Rand.nextInt(5 * 1000, 10 * 1000));
                    Sleep.sleep(gaussian(50, 15 * 1000, 5 * 1000, 2500));
                    return;
                }
            } else if (Trade.getView().equals(Trade.View.FIRST)) {
                /** Offer stuff */
                for (ItemRequest itemRequest : currentRequest.getItemRequestList()) {
                    int itemId = itemRequest.getItemId();
                    int quantity = itemRequest.getQuantity();
                    if (Trade.getOfferedQuantity(itemId) < quantity) {
                        log.info("Offering " + itemId + " x " + quantity);
                        Trade.offer(itemId, quantity - Trade.getOfferedQuantity(itemId));
                        Sleep.sleep(gaussian(50, 15 * 1000, 2000, 1500));
                        return;
                    }
                }
            }
            log.info("Accepting trade");
            Trade.accept();
        } else if(currentRequest != null) {
                long difference = (System.currentTimeMillis() - currentRequest.getTime());
                log.info("Waiting for an acknowledgement from " + currentRequest.getHandle() + " for " + difference / 1000 + " seconds");
                if (difference > 20 * 1000) {
                    log.info("Waited 20 seconds and never got an acknowledgement, expiring");
                    expireRequest();
                }
        }
        Sleep.sleep(2 * 1000, 5 * 1000);
    }

    @Override
    public void onStart(String launchArg) {
        super.onStart(launchArg);
        /** Set up rabbitMQ listeners */
        log.info("Setting up rabbitMQ listeners");
        channel = Store.getChannel();
        try {
            setupRequestListener();
            setupAcknowledgementListener();
            setupDeliveryConfirmationListener();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Create scheduled task to switch tabs to we don't get logged out
        timer = new Timer("Timer");
        TimerTask clickTab = new TimerTask() {
            @Override
            public void run() {
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
            }
        };
        long delay = 1000L * 60L * Rand.nextInt(1, 2);
        long period = delay;
        timer.scheduleAtFixedRate(clickTab, delay, period);
    }

    private void setupDeliveryConfirmationListener() throws IOException {
        channel.queueDeclare(Players.getLocal().getName() + "delivery", false, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            if (Client.getGameState() != GameState.LOGGED_IN) {
                System.out.println("Not logged in, ignoring delivery confirmation");
                return;
            }
            String message = new String(delivery.getBody(), "UTF-8");
            DeliveryAcknowledgement confirmation = new ObjectMapper().readValue(message, DeliveryAcknowledgement.class);
            System.out.println("Received delivery confirmation from " + confirmation.getHandle());
            if (currentRequest == null) {
                System.out.println("We have no request set!");
            } else if (!currentRequest.getHandle().equals(confirmation.getHandle())) {
                System.out.println("Confirmation received did not match current request's handle!");
            } else {
                System.out.print("Received delivery confirmation from " + confirmation.getHandle() + ", expiring everything");
                expireRequest();
            }
        };

        channel.basicConsume(Players.getLocal().getName() + "delivery", true, deliverCallback, consumerTag -> {
        });
    }

    private void setupAcknowledgementListener() throws IOException {
        channel.queueDeclare(Players.getLocal().getName(), false, false, false, null);

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            if (Client.getGameState() != GameState.LOGGED_IN) {
                System.out.println("Not logged in, ignoring acknowledgement");
                return;
            }
            String message = new String(delivery.getBody(), "UTF-8");
            RestockAcknowledgement acknowledgement = new ObjectMapper().readValue(message, RestockAcknowledgement.class);
            System.out.println("Received restock acknowledgement from " + acknowledgement.getHandle());
            if (currentRequest == null) {
                System.out.println("We have no request set!");
            } else if (!currentRequest.getHandle().equals(acknowledgement.getHandle())) {
                System.out.println("Acknowledgement received did not match current request's handle!");
            } else {
                acknowledged = true;
                System.out.print("set acknowledged to true");
            }
        };

        channel.basicConsume(Players.getLocal().getName(), true, deliverCallback, consumerTag -> {
        });
    }

    private void setupRequestListener() throws IOException {
        channel.exchangeDeclare("requests", "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, "requests", "");

        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            if (Client.getGameState() != GameState.LOGGED_IN) {
                System.out.println("Not logged in, ignoring restock request");
                return;
            }
            String message = new String(delivery.getBody(), "UTF-8");
            RestockRequest request = new ObjectMapper().readValue(message, RestockRequest.class);
            System.out.println("Received restock request: " + request.toString());
            if (currentRequest != null) {
                if (request.getHandle().equals(currentRequest.getHandle())) {
                    log.info("Received restock request from current request guy, setting acknowledged to true");
                    acknowledged = true;
                    return;
                }
                System.out.println("We are already handling a request from " + currentRequest.getHandle() + " currently");
                long timeSinceRequest = System.currentTimeMillis() - currentRequest.getTime();
                log.info("currentRequest time: " + currentRequest.getTime());
                log.info("Time since last request: " + timeSinceRequest);
                if (timeSinceRequest > 180 * 1000) {
                    System.out.println("It has been over a minute since we got our current request, we can replace it with new request");
                    currentRequest = request;
                    processRequest(request);
                }
            } else {
                currentRequest = request;
                processRequest(request);
            }
        };
        channel.basicConsume(queueName, true, deliverCallback, consumerTag -> {
        });
    }

    private void processRequest(RestockRequest request) {
        log.info("Received a restock request: " + request.toString());
        if (!weCanFulfill(request)) {
            log.info("We cannot fulfill the request");
            return;
        }
        log.info("We can fulfill the request!");
        String theirHandle = request.getHandle();
        int ourWorld = Worlds.getCurrentWorldId();
        sendOffer(theirHandle, ourWorld);
        //TODO: handle offer not being seen sometimes? should this be handled in receiver?
    }

    private boolean weCanFulfill(RestockRequest request) {
        for (ItemRequest itemRequest : request.getItemRequestList()) {
            if (Inventory.count(itemRequest.getItemId()) < itemRequest.getQuantity()) {
                log.info("We don't have enough " + itemRequest.getItemId() + " to fulfill request");
                return false;
            }
        }
        return true;
    }

    private void sendOffer(String theirHandle, int world) {
        try {
            System.out.println("Sending restock offer");
            RestockOffer offer = new RestockOffer();
            offer.setHandle(Players.getLocal().getName());
            offer.setWorld(world);
            byte[] message = new ObjectMapper().writeValueAsBytes(offer);
            channel.queueDeclare(theirHandle, false, false, false, null);
            channel.basicPublish("", theirHandle, null, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void expireRequest() {
        log.info("Expiring request");
        currentRequest = null; //TODO: find a better solution
        acknowledged = false;
        tradesAttempted = 0;
    }
}
