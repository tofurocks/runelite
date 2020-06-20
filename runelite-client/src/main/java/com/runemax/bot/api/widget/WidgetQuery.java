package com.runemax.bot.api.widget;

import com.runemax.bot.api.wrappers.widget.Widget;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.widgets.WidgetInfo;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

@Slf4j
public class WidgetQuery {
    private final int groupId;
    private Predicate<? super Widget> childPredicate = null;
    private Predicate<? super Widget> grandChildPredicate = null;

    private int childId = -1;
    private int grandChildIndex = -1;

    public WidgetQuery(int groupId, Predicate<? super Widget> childPredicate){
        this.groupId = groupId;
        if(childPredicate == null) throw new IllegalArgumentException();
        this.childPredicate = childPredicate;
    }

    public WidgetQuery(int groupId, int childId){
        this.groupId = groupId;
        this.childId = childId;
    }

    public WidgetQuery withGrandChild(Predicate<? super Widget> grandChildPredicate){
        this.grandChildPredicate = grandChildPredicate;
        return this;
    }

    public WidgetQuery withGrandChild(int grandChildIndex){
        this.grandChildIndex = grandChildIndex;
        return this;
    }

    @Nonnull
    public Widget get(){
        if(childId == -1){
            Widget[] group = Widgets.getGroup(groupId);
            for (Widget widget : group) {
                if(childPredicate.test(widget)){
                    childId = WidgetInfo.TO_CHILD(widget.getId());
                    break;
                }
            }

            if(childId == -1) return new Widget(null);
        }

        Widget child = Widgets.get(groupId, childId);
        if(grandChildPredicate == null){
            return child;
        }

        if(grandChildIndex == -1){
            if(child.isEmpty()){
                return new Widget(null);
            }

            Widget[] grandChildren = child.getChildren();
            for (int i = 0; i < grandChildren.length; i++) {
                if(grandChildPredicate.test(grandChildren[i])){
                    grandChildIndex = i;
                    break;
                }
            }

            if(grandChildIndex == -1) return new Widget(null);
        }

        return child.getChild(grandChildIndex);
    }
}
