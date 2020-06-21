package net.runelite.client.plugins.quest;

import net.runelite.api.Quest;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;

import javax.inject.Singleton;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

@Singleton
class QuestPanelItem extends JPanel {

    private final JLabel state = new JLabel();
    private final JLabel progress = new JLabel();

    QuestPanelItem(final Quest q) {
        setBorder(new EmptyBorder(3, 3, 3, 3));
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        GroupLayout layout = new GroupLayout(this);
        this.setLayout(layout);

//        JLabel image = new JLabel();
//        b.getIcon().addTo(image);
        JLabel name = new JLabel(q.getName());
        state.setFont(FontManager.getRunescapeSmallFont());
        progress.setFont(FontManager.getRunescapeSmallFont());

        layout.setVerticalGroup(layout.createParallelGroup()
            .addGroup(layout.createSequentialGroup()
                .addComponent(name)
                .addComponent(state)
                .addComponent(progress)
            )
        );

        layout.setHorizontalGroup(layout.createSequentialGroup()
            .addGroup(layout.createParallelGroup()
                .addComponent(name)
                .addComponent(state)
                .addComponent(progress)
            )
        );
    }

    void setState(String location) {
        this.state.setText(location);
    }

    void setProgress(String progress) {
        this.progress.setText(progress);
    }

}
