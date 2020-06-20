package net.runelite.client.plugins.bot;

import javax.swing.*;
import javax.swing.text.Document;

public class TrimmingJTextArea extends JTextArea {
    public TrimmingJTextArea() {
        super();
    }

    public TrimmingJTextArea(String text) {
        super(text);
    }

    public TrimmingJTextArea(int rows, int columns) {
        super(rows, columns);
    }

    public TrimmingJTextArea(String text, int rows, int columns) {
        super(text, rows, columns);
    }

    public TrimmingJTextArea(Document doc) {
        super(doc);
    }

    public TrimmingJTextArea(Document doc, String text, int rows, int columns) {
        super(doc, text, rows, columns);
    }

    @Override
    public void append(String str) {
        String text = getText();
        if(text.length() > 10*1000){
            setText(text.substring(text.length() - 10*1000));
        }
        super.append(str);
        //setCaretPosition(getDocument().getLength());//scroll to bottom
    }
}
