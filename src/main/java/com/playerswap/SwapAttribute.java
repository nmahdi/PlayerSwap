package com.playerswap;

public enum SwapAttribute {
    Health(0, "health"),
    Hunger(1, "hunger"),
    Saturation(2, "saturation"),
    Location(3, "location"),
    Inventory(4, "inventory"),
    PotionEffects(5, "potion-effects"),
    Vehicle(6, "vehicle"),
    Velocity(7, "velocity");

    private final int index;
    private final String configValue;

    SwapAttribute(int index, String configValue) {
        this.index = index;
        this.configValue = configValue;
    }

    public int getIndex() {
        return index;
    }

    public String getConfigValue() {
        return configValue;
    }
}
