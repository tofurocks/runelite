package com.runemax.bot.api.account;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nullable;

@RequiredArgsConstructor
public class Credentials {
    @Getter
    private final String user;
    @Getter
    private final String password;

    @Nullable
    @Getter
    private String auth = null;

    public Credentials setAuth(String auth) {
        this.auth = auth;
        return this;
    }

    public static Credentials parse(String colonDelimitedString){
        String[] split = colonDelimitedString.split(":");
        Credentials credentials = new Credentials(split[0], split[1]);
        if(split.length > 2){
            credentials.setAuth(split[2]);
        }

        return credentials;
    }
}
