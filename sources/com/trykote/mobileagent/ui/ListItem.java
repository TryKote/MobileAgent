package com.trykote.mobileagent.ui;


public interface ListItem {
    int getHeight();
    boolean isSelected();
    void select();
    void deselect();
    int getWidth();
    int getBaseHeight();
    String getText();
    int getCommandCount();
    boolean isHighlighted();
    int getCommandId(int index);
    int executeCommand(int index);
}
