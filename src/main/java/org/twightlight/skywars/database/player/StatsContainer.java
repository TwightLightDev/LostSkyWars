package org.twightlight.skywars.database.player;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class StatsContainer {

    private Object value;

    public StatsContainer(Object value) {
        this.value = value;
    }

    public void set(Object value) {
        this.value = value;
    }

    public void addInt(int amount) {
        this.value = getAsInt() + amount;
    }

    public void addDouble(double amount) {
        this.value = getAsDouble() + amount;
    }

    public void removeInt(int amount) {
        this.value = getAsInt() - amount;
    }

    public void removeDouble(double amount) {
        this.value = getAsDouble() - amount;
    }

    public Object get() {
        return value;
    }

    public int getAsInt() {
        return Integer.parseInt(this.getAsString());
    }

    public long getAsLong() {
        return Long.valueOf(this.getAsString());
    }

    public double getAsDouble() {
        return Double.parseDouble(this.getAsString());
    }

    public String getAsString() {
        return value.toString();
    }

    public boolean getAsBoolean() {
        return Boolean.valueOf(this.getAsString());
    }

    public JSONArray getAsJsonArray() {
        try {
            return (JSONArray) new JSONParser().parse(this.getAsString());
        } catch (Exception ex) {
            throw new IllegalArgumentException("\"" + value + "\" is not a JsonArray: ", ex);
        }
    }

    public JSONObject getAsJsonObject() {
        try {
            return (JSONObject) new JSONParser().parse(this.getAsString());
        } catch (Exception ex) {
            throw new IllegalArgumentException("\"" + value + "\" is not a JSONObject: ", ex);
        }
    }

    public DeliveryContainer getDelivery() {
        return new DeliveryContainer(this, this.getAsString());
    }
}
