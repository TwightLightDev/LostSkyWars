package org.twightlight.skywars.bungee;

public abstract class CoreSender {

    protected Object sender;

    public CoreSender(Object sender) {
        this.sender = sender;
    }

    public abstract void sendMessage(String message);
}
