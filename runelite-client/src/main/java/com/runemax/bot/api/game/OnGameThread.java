package com.runemax.bot.api.game;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.inject.Inject;

import net.runelite.client.callback.ClientThread;

public class OnGameThread {
    public static final int TIMEOUT = 500;

    @Inject
    private static ClientThread clientThread;

    public static <T> Future<T> invoke(Callable<T> callable){
        FutureTask<T> out = new FutureTask<>(callable);
        if(Client.isClientThread()){
            out.run();
        }else {
            clientThread.invokeLater(out);
        }
        return out;
    }

    public static <T> T invokeAndWait(Callable<T> callable){
        if(Client.isClientThread()){
            try {
                return callable.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        FutureTask<T> futureTask = new FutureTask<>(callable);
        clientThread.invokeLater(futureTask);
        try {
            return futureTask.get(TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
