package org.twightlight.skywars.utils;

import org.twightlight.skywars.bungee.Core;
import org.twightlight.skywars.bungee.CoreSender;

public class Logger {

    private String prefix;
    private CoreSender sender;

    public Logger() {
        this.prefix = "[SkyWars] ";
        this.sender = Core.getCoreSender();
    }

    public Logger(String prefix) {
        this.prefix = prefix;
        this.sender = Core.getCoreSender();
    }

    public void info(String message) {
        this.log(Level.INFO, message);
    }

    public void warning(String message) {
        this.log(Level.WARNING, message);
    }

    public void severe(String message) {
        this.log(Level.SEVERE, message);
    }

    public void log(Level level, String message) {
        this.log(level, message, null);
    }

    public void log(Level level, String message, Throwable throwable) {
        StringBuilder result = new StringBuilder(this.prefix + message);
        if (throwable != null) {
            result.append("\n" + throwable.getMessage());
            for (StackTraceElement ste : throwable.getStackTrace()) {
                if (ste.toString().contains("tk.kanaostore.losteddev.skywars")) {
                    result.append("\n" + ste.toString());
                }
            }
        }

        this.sender.sendMessage(level.format(result.toString()));
    }

    public Logger getModule(String module) {
        return new Logger(this.prefix + "[" + module + "] ");
    }

    public static enum Level {
        INFO("§a"),
        WARNING("§c"),
        SEVERE("§4");

        private String color;

        Level(String color) {
            this.color = color;
        }

        public String format(String message) {
            return this.color + message;
        }
    }
}
