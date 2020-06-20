package com.runemax.bot.api.account;

import lombok.Getter;
import lombok.Setter;
import com.runemax.bot.api.varps.Varps;

import java.util.function.Supplier;

public class Account {
    public enum BankPinStatus {
        UNSET,
        SET,
        PENDING_REMOVAL,
        UNKNOWN,
        ;
    }

    @Getter
    @Setter
    private static String setUsername = null;

    @Getter
    @Setter
    private static BankPinStatus bankPinStatus = BankPinStatus.UNKNOWN;

    @Setter
    private static Supplier<Credentials> credentialsSupplier = null;

    public static void setCredentials(Credentials credentials) {
        setCredentialsSupplier(() -> credentials);
    }

    public static Credentials getCredentials() {
        return credentialsSupplier == null ? null : credentialsSupplier.get();
    }

    public static int getMembership() {
        return Varps.get(1780);
    }
}
