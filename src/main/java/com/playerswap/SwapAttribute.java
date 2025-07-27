package com.playerswap;

import javax.annotation.Nullable;

public enum SwapAttribute {
    Health(0, "health"),
    Hunger(1, "hunger"),
    Saturation(2, "saturation"),
    AirBubbles(3, "air-bubbles"),
    FireTicks(4, "fire-ticks"),
    Location(5, "location"),
    Inventory(6, "inventory"),
    PotionEffects(7, "potion-effects"),
    Vehicle(8, "vehicle"),
    Velocity(9, "velocity"),
    EnderPearl(10, "ender-pearl");

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

    @Nullable
    public static SwapAttribute fromConfigValue(String configValue) {
        for(SwapAttribute attribute : SwapAttribute.values()){
            if(attribute.getConfigValue().equalsIgnoreCase(configValue)) {
                return attribute;
            }
        }
        return null;
    }
}
