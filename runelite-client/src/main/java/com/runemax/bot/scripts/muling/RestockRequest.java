package com.runemax.bot.scripts.muling;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class RestockRequest {
    @Getter
    @Setter
    String handle;
    @Getter
    @Setter
    List<ItemRequest> itemRequestList = new ArrayList<>();
    @Getter
    @Setter
    long time;

    public RestockRequest(){
        time = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        String s;
        s = "Handle: " + handle;
        for(ItemRequest itemRequest : itemRequestList){
            s += " id: " + itemRequest.itemId + " x " + itemRequest.quantity;
        }
        return s;
    }
}
