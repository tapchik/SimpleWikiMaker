package com.example.SimpleWiki.enums;

public enum FileType {
    FILE("file"),
    DIR("dir"),
    SETTINGS("settings"),
    THEME("theme"),
    ADD_THEME("addTheme");

    public final String text;

    private FileType(String text) {
        this.text = text;
    }

    public String ToText() {
        return this.text;
    }

    static public FileType Equivalent(String type) {
        return switch (type) {
            case "file" -> FileType.FILE;
            case "dir" -> FileType.DIR;
            case "settings" -> FileType.SETTINGS;
            case "theme" -> FileType.THEME;
            case "addTheme" -> FileType.ADD_THEME;
            default -> null;
        };
    }
}
