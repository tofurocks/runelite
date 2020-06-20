package com.runemax.bot.scripts.muling;

import lombok.Getter;
import lombok.Setter;

public class ItemRequest {
    @Getter
    @Setter
    int itemId;
    @Getter
    @Setter
    int quantity;

    public ItemRequest(){}

    public ItemRequest(int itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
