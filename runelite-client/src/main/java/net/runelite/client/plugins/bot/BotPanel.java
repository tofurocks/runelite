package net.runelite.client.plugins.bot;

import com.runemax.bot.api.BotManager;
import com.runemax.bot.api.script.BotScript;
import com.runemax.bot.api.script.ScriptMeta;
import net.runelite.client.ui.PluginPanel;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class BotPanel extends PluginPanel {
    private final JList<ScriptListEntry> scriptList = new JList<>();
    private final JFrame logFrame;

    public BotPanel(BotManager botManager){
        super(false);
        setLayout(new BorderLayout());

        refreshScriptList();

        //north
        Panel northPanel = new Panel(new GridLayout(1, 0));

        //startButton
        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> botManager.startScript(scriptList.getSelectedValue().getScriptClass()));
        northPanel.add(startButton);

        //stopButton
        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(e -> botManager.stopScript());
        northPanel.add(stopButton);

        //refreshButton
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> refreshScriptList());
        northPanel.add(refreshButton);


        //logButton
        JButton logButton = new JButton("Logger");
        logButton.addActionListener(e -> openLogger());
        northPanel.add(logButton);


        add(northPanel, BorderLayout.NORTH);

        //center
        Panel centerPanel = new Panel(new GridLayout(0, 1));

        //scriptScrollPane
        JScrollPane scriptScrollPane = new JScrollPane(scriptList);
        scriptScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        centerPanel.add(scriptScrollPane);

        add(centerPanel, BorderLayout.CENTER);

        logFrame = new JFrame();

        JTextArea textArea = new TrimmingJTextArea();
        textArea.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        logFrame.getContentPane().add(scrollPane);
        logFrame.pack();
        logFrame.setSize(1000, 500);

        System.setOut(new PrintStreamInterceptor(System.out, textArea));
        System.setErr(new PrintStreamInterceptor(System.err, textArea));
    }

    private void openLogger(){
        if(!logFrame.isVisible()){
            logFrame.setLocationRelativeTo(this);
            logFrame.setVisible(true);
        }
    }

    private void refreshScriptList(){
        Vector<ScriptListEntry> scriptListEntries = new Vector<>();
        for (Class<? extends BotScript> scriptClass : BotManager.loadScripts()) {
            scriptListEntries.add(new ScriptListEntry(scriptClass));
        }

        scriptList.setListData(scriptListEntries);
    }

    private static final class ScriptListEntry {
        private final Class<? extends BotScript> scriptClass;
        private final ScriptMeta descriptor;

        private ScriptListEntry(Class<? extends BotScript> scriptClass, ScriptMeta descriptor) {
            this.scriptClass = scriptClass;
            this.descriptor = descriptor;
        }

        private ScriptListEntry(Class<? extends BotScript> scriptClass) {
            this(scriptClass, scriptClass.getAnnotationsByType(ScriptMeta.class)[0]);
        }

        @Override
        public String toString() {
            return descriptor.value();
        }

        public Class<? extends BotScript> getScriptClass() {
            return scriptClass;
        }
    }
}
