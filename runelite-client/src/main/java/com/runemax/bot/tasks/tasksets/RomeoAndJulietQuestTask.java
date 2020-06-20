package com.runemax.bot.tasks.tasksets;

import com.runemax.bot.api.coords.RectangularArea;
import com.runemax.bot.api.game.Client;
import com.runemax.bot.tasks.framework.TaskSet;
import com.runemax.bot.tasks.subtasks.TalkNPCTask;
import com.runemax.bot.tasks.subtasks.WalkTask;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Quest;
import net.runelite.api.QuestState;
import net.runelite.api.coords.WorldPoint;

@Slf4j
public class RomeoAndJulietQuestTask extends TaskSet {

    public static final RectangularArea VARROCK_SQUARE = new RectangularArea(3208, 3424, 3218, 3422, 0);
    public static final RectangularArea JULIET_DOWNSTAIRS_AREA = new RectangularArea(3159, 3433, 3163, 3436, 0);
    public static final String ROMEO = "Romeo";
    public static final String JULIET = "Juliet";
    public static final String STAIRCASE = "Staircase";

    public RomeoAndJulietQuestTask(String taskDescription) {
        super(taskDescription);
    }

    @Override
    public void ascertainTasks() {
        final int stage = Quest.ROMEO__JULIET.getQuestProgress(Client.getInstance());
        final QuestState state = Quest.ROMEO__JULIET.getState(Client.getInstance());
        switch (state) {
            case IN_PROGRESS:
                this.add(new WalkTask("walk to Juliet", new WorldPoint(3158, 3427, 1)));
//                if (stage == 0 && !Inventory.first("Message").isPresent()) {
//
//                }
//                else if (stage == 0 && Inventory.first("Message").isPresent()) {
////                    this.add(new WalkTask("Walk to downstairs of Juliet's mansion", JULIET_DOWNSTAIRS_AREA));
////                    this.add(new InteractTask("Climb up stairs", STAIRCASE, "Climb-up"));
////                    this.add(new TalkNPCTask("Talk to Juliet", JULIET, new String[]{
////                        "shut up bitch"
////                    }));
//                }
                break;

            case NOT_STARTED:
                // Start the quest with Romeo
                this.add(new TalkNPCTask("Talk to Romeo", ROMEO, VARROCK_SQUARE, new String[]{
                    "help to find her", "let her know", "ok, thanks"
                }));
                break;

            case FINISHED:
                // do nothing
                break;
        }
    }

}
