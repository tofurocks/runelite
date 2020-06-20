package com.runemax.bot.api;

import com.runemax.bot.api.interact.Interact;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;
import com.runemax.bot.api.script.ScriptThread;
import com.runemax.bot.scripts.*;
import com.runemax.bot.scripts.Automaton;
import com.runemax.bot.scripts.DraynorFishing;
import com.runemax.bot.scripts.Tutorial;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.RuneLite;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class BotManager {
    public static final File SCRIPTS_DIR = new File(RuneLite.RUNELITE_DIR, "scripts");

    private volatile ScriptThread scriptThread = null;

    public BotManager() {
        RuneLite.getInjector().createChildInjector(new BotModule());
        Interact.init();
        SCRIPTS_DIR.mkdir();
    }

    public void startScript(Class<? extends BotScript> scriptClass) {
        if (scriptThread != null && scriptThread.isAlive()) {
            log.warn("script running, trying to stop");
            stopScript();
            return;
        }

        BotScript script;
        try {
            script = scriptClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException("script had wrong params or something", e);
        }

        scriptThread = new ScriptThread(script);
        scriptThread.start();
        log.info("started script" + scriptClass.getName());
    }

    public boolean stopScript() {
        if (scriptThread == null) {
            return true;
        }

        if (scriptThread.isAlive()) {
            scriptThread.getScript().stopLooping();
            return false;
        }else {
            log.info("successfully stopped script");
            scriptThread = null;
            return true;
        }
    }

    public static List<Class<? extends BotScript>> loadScripts() {
        List<Class<? extends BotScript>> out = new ArrayList<>();
        out.add(Tutorial.class);
        out.add(DraynorFishing.class);
        out.add(WalkToGrandExchange.class);
        out.add(JustLogin.class);
        out.add(Fighter.class);
        out.add(MuleCombatItems.class);
        out.add(Automaton.class);
        out.add(Construction.class);

        out.addAll(loadScriptsFromDir(SCRIPTS_DIR));
        return out;
    }

    private static List<Class<? extends BotScript>> loadScriptsFromDir(File dir) {//copypastaed from rspeer
        List<Class<? extends BotScript>> out = new ArrayList<>();

        File[] files = dir.listFiles(pathname -> !pathname.isDirectory() && pathname.getName().endsWith(".jar"));
        if (files == null) {
            return out;
        }

        for (File file : files) {
            out.addAll(loadScriptFromFile(file));
        }

        return out;
    }

    @SuppressWarnings("unchecked")
    private static List<Class<? extends BotScript>> loadScriptFromFile(File file) {//copypastaed from rspeer
        List<Class<? extends BotScript>> out = new ArrayList<>();
        try (
                JarFile jar = new JarFile(file);
                URLClassLoader ucl = new URLClassLoader(new URL[]{file.toURI().toURL()})
        ) {
            Enumeration<JarEntry> elems = jar.entries();
            while (elems.hasMoreElements()) {
                JarEntry entry = elems.nextElement();
                if (entry.getName().endsWith(".class")) {
                    String name = entry.getName();
                    name = name.substring(0, name.length() - ".class".length());
                    name = name.replace('/', '.');
                    Class<?> clazz = ucl.loadClass(name);

                    if (validateClassIsScript(clazz)) {
                        out.add((Class<? extends BotScript>) clazz);
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            log.error("some exception loading scripts", e);
        }
        return out;
    }

    private static boolean validateClassIsScript(Class<?> clazz) {
        if (!BotScript.class.isAssignableFrom(clazz) || Modifier.isAbstract(clazz.getModifiers())) {//shit dont work
            return false;
        }

        if (clazz.getAnnotation(ScriptMeta.class) == null) {
            log.warn("Class {} is a script, but has no scriptmeta", clazz);
            return false;
        }

        return true;
    }
}
