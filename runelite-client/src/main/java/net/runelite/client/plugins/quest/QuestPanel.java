package net.runelite.client.plugins.quest;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Quest;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;

import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.util.HashMap;

@Slf4j
@Singleton
class QuestPanel extends PluginPanel {

    private IconTextField searchBar;
    private JPanel questsPane = new JPanel(new GridBagLayout());
    private JButton refresh;
    private HashMap<Quest, QuestPanelItem> panelItems = new HashMap<>();
    private final Client client;

    @Inject
    public QuestPanel(Client client) {
        super();
        this.client = client;
    }

    public void init() {

        setLayout(new BorderLayout(0, 5));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        refresh = new JButton("Click to load quests");
        refresh.addActionListener(ev -> reloadData());

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        // search bar
        JPanel topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new BorderLayout(0, BORDER_OFFSET));

        searchBar = new IconTextField();
        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { filter(); }

            @Override
            public void removeUpdate(DocumentEvent e) { filter(); }

            @Override
            public void changedUpdate(DocumentEvent e) { filter(); }
        });

        topPanel.add(searchBar, BorderLayout.NORTH);
        topPanel.add(refresh, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        // Add quests pane
        add(questsPane, BorderLayout.CENTER);

    }

    public void filter() {
        SwingUtilities.invokeLater(() ->
        {
            String search = searchBar != null ? searchBar.getText().toLowerCase().trim() : "";

            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1;
            c.gridx = 0;
            c.gridy = 0;

            questsPane.removeAll();
            for (Quest quest : panelItems.keySet()) {
                if (search.length() > 0 && !quest.name().toLowerCase().trim().contains(search)) continue;
                QuestPanelItem qpa = panelItems.get(quest);
                questsPane.add(qpa, c);
                c.gridy++;
            }

            this.revalidate();
            this.repaint();
//            log.info("Filtered Quest Panel");
        });
    }

    public void reloadData() {
        SwingUtilities.invokeLater(() ->
        {
            refresh.setText("Refresh");
            panelItems.clear();
            questsPane.removeAll();
            for (Quest quest : Quest.values()) {
                QuestPanelItem panel = new QuestPanelItem(quest);
                if (client.getGameState() == GameState.LOGGED_IN) {
                    panel.setState("State: " + quest.getState(this.client).name());
                    panel.setProgress("Stage: " + quest.getQuestProgress(this.client));
                } else {
                    panel.setState("State: ?");
                    panel.setProgress("Stage: ?");
                }
                panelItems.put(quest, panel);
            }
//            log.info("Retreived quest info");
            this.filter();
        });
    }

}
