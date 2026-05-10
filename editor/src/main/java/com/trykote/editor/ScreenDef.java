package com.trykote.editor;

import java.util.ArrayList;
import java.util.List;

public class ScreenDef {

    public enum ScreenType {
        FULLSCREEN(ScreenCategory.FULLSCREEN),
        FULLSCREEN_ALT(ScreenCategory.FULLSCREEN),
        FULLSCREEN_NOSCROLL(ScreenCategory.FULLSCREEN),
        FULLSCREEN_NOSCROLL_ALT(ScreenCategory.FULLSCREEN),
        POPUP(ScreenCategory.POPUP),
        DIALOG_CENTER(ScreenCategory.DIALOG),
        DIALOG_BOTTOM(ScreenCategory.DIALOG),
        DIALOG_CORNER(ScreenCategory.DIALOG),
        DIALOG_LOW(ScreenCategory.DIALOG),
        TOAST(ScreenCategory.TOAST),
        TOAST_CENTER(ScreenCategory.TOAST),
        MAP(ScreenCategory.GRID);

        public final ScreenCategory category;

        ScreenType(ScreenCategory category) {
            this.category = category;
        }

        static ScreenType fromString(String s) {
            return switch (s) {
                case "fullscreen" -> FULLSCREEN;
                case "fullscreen_alt" -> FULLSCREEN_ALT;
                case "fullscreen_noscroll" -> FULLSCREEN_NOSCROLL;
                case "fullscreen_noscroll_alt" -> FULLSCREEN_NOSCROLL_ALT;
                case "popup" -> POPUP;
                case "dialog_center" -> DIALOG_CENTER;
                case "dialog_bottom" -> DIALOG_BOTTOM;
                case "dialog_corner" -> DIALOG_CORNER;
                case "dialog_low" -> DIALOG_LOW;
                case "toast" -> TOAST;
                case "toast_center" -> TOAST_CENTER;
                case "map" -> MAP;
                default -> FULLSCREEN;
            };
        }

        public String toConfigString() {
            return name().toLowerCase();
        }
    }

    public enum ItemType {
        ACTION, SEPARATOR, CHECKBOX, DROPDOWN, TEXT_SEPARATOR,
        TEXT_INPUT, LABEL_SEPARATOR, CONDITIONAL_IF, CONDITIONAL_UNLESS,
        LOGIN, PASSWORD, IMAGE, REDIRECT;

        static ItemType fromString(String s) {
            return switch (s) {
                case "action" -> ACTION;
                case "separator" -> SEPARATOR;
                case "checkbox" -> CHECKBOX;
                case "dropdown" -> DROPDOWN;
                case "text_separator" -> TEXT_SEPARATOR;
                case "text_input" -> TEXT_INPUT;
                case "label_separator" -> LABEL_SEPARATOR;
                case "conditional_if" -> CONDITIONAL_IF;
                case "conditional_unless" -> CONDITIONAL_UNLESS;
                case "login" -> LOGIN;
                case "password" -> PASSWORD;
                case "image" -> IMAGE;
                case "redirect" -> REDIRECT;
                default -> ACTION;
            };
        }

        public String toConfigString() {
            return name().toLowerCase();
        }
    }

    public static class SoftKey {
        public int label;
        public int cmd;
        public String labelHint;

        public SoftKey(int label, int cmd, String labelHint) {
            this.label = label;
            this.cmd = cmd;
            this.labelHint = labelHint;
        }
    }

    public static class Item {
        public ItemType type;
        public int label;
        public String labelHint;
        public int icon;
        public int cmd;
        public int condKey;
        public int dataKey;
        public int hint;
        public String style;
        // Preserve all original JSON fields for lossless save
        public java.util.Map<String, Object> rawJson;

        public Item(ItemType type, int label, String labelHint, int icon, int cmd,
                    int condKey, int dataKey, int hint, String style,
                    java.util.Map<String, Object> rawJson) {
            this.type = type;
            this.label = label;
            this.labelHint = labelHint;
            this.icon = icon;
            this.cmd = cmd;
            this.condKey = condKey;
            this.dataKey = dataKey;
            this.hint = hint;
            this.style = style;
            this.rawJson = rawJson;
        }

        public Item copy() {
            var rawCopy = rawJson != null ? new java.util.LinkedHashMap<>(rawJson) : null;
            return new Item(type, label, labelHint, icon, cmd, condKey, dataKey, hint, style, rawCopy);
        }
    }

    public String name;
    public int screenId;
    public ScreenType type;
    public int title;
    public boolean checkboxes;
    public String handler;
    public SoftKey leftKey;
    public SoftKey rightKey;
    public int extraCmd;
    public final List<Item> items;
    public com.fasterxml.jackson.databind.node.ObjectNode rawJson;

    public ScreenDef(String name, int screenId, ScreenType type, int title,
                     boolean checkboxes, String handler, SoftKey leftKey, SoftKey rightKey,
                     int extraCmd, List<Item> items) {
        this.name = name;
        this.screenId = screenId;
        this.type = type;
        this.title = title;
        this.checkboxes = checkboxes;
        this.handler = handler != null ? handler : "";
        this.leftKey = leftKey;
        this.rightKey = rightKey;
        this.extraCmd = extraCmd;
        this.items = new ArrayList<>(items);
    }

    @Override
    public String toString() {
        return name;
    }
}
