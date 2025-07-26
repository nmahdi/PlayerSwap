package com.playerswap;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class PSConfig {

    private final String MIN_DELAY = "min-delay";
    private final String MAX_DELAY = "max-delay";
    private final String SWAP_FORMAT = "swap-format";
    private final String SWAP_MESSAGE = "swap-message";
    private final String SWAP_SETTINGS = "swap.";
    private final String SOUND_NAME = "swap-sound.name";
    private final String SOUND_VOLUME = "swap-sound.volume";
    private final String SOUND_PITCH = "swap-sound.pitch";

    private final PlayerSwap main;
    private final File file;
    private FileConfiguration config;

    private double minDelay, maxDelay;

    private final boolean[] toSWAP = new boolean[8];

    private Sound soundEffect;
    private double soundVolume;
    private double soundPitch;

    private String swapFormat;
    private final ArrayList<String> swapMessage = new ArrayList<>();

    public PSConfig(PlayerSwap main) {
        this.main = main;
        this.file = new File(main.getDataFolder(), "config.yml");

        loadConfig();
    }

    public void loadConfig() {
        // If the plugin's data folder does not exist, create it.
        if(!main.getDataFolder().exists()) {
            main.getDataFolder().mkdirs();
        }

        // If config.yml does not exist, create it with default values.
        if(!file.exists()) {
            System.out.println("Creating 'config.yml' from defaults...");
            main.saveResource("config.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(file);

        /* Load all config values */
        // Minimum delay
        if (config.contains(MIN_DELAY)) {
            minDelay = config.getDouble(MIN_DELAY);
        } else {
            minDelay = 5d;
            sendDefaultMsg(MIN_DELAY);
        }

        // Maximum delay
        if (config.contains(MAX_DELAY)) {
            maxDelay = config.getInt(MAX_DELAY);
        } else {
            maxDelay = 45d;
            sendDefaultMsg(MAX_DELAY);
        }

        // Swap attributes
        for (SwapAttribute attribute : SwapAttribute.values()) {
            if (!config.contains(SWAP_SETTINGS + attribute.getConfigValue())){
                toSWAP[attribute.getIndex()] = true;
                sendDefaultMsg(SWAP_SETTINGS + attribute.getConfigValue());
                continue;
            }

            toSWAP[attribute.getIndex()] = config.getBoolean(SWAP_SETTINGS + attribute.getConfigValue());
        }

        // Swap sound
        if(config.contains(SOUND_NAME)) {
            soundEffect = Sound.valueOf(config.getString(SOUND_NAME));
        }else{
            soundEffect = Sound.ENTITY_EXPERIENCE_ORB_PICKUP;
            sendDefaultMsg(SOUND_NAME);
        }

        if(config.contains(SOUND_VOLUME)) {
            soundVolume = config.getDouble(SOUND_VOLUME);
        }else{
            soundVolume = 0.5d;
            sendDefaultMsg(SOUND_VOLUME);
        }

        if(config.contains(SOUND_PITCH)) {
            soundPitch = config.getDouble(SOUND_PITCH);
        }else{
            soundPitch = 0.5d;
            sendDefaultMsg(SOUND_PITCH);
        }

        // Swap format
        if(config.contains(SWAP_FORMAT)){
            swapFormat = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(config.getString(SWAP_FORMAT)));
        }else{
            swapFormat = ChatColor.AQUA + "%original% -> %new%";
            sendDefaultMsg(SWAP_FORMAT);
        }

        // Swap message
        for (String s : config.getStringList(SWAP_MESSAGE)) {
            swapMessage.add(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    // Alerts server admins which config keyword is missing
    private void sendDefaultMsg(String setting) {
        System.out.println("Could not find '" + setting + "' in config.yml... Using default value.");
    }

    public double getMinDelay() {
        return minDelay;
    }

    public void setMinDelay(double minDelay) {
        this.minDelay = minDelay;
        config.set(MIN_DELAY, minDelay);
        saveConfig();
    }

    public double getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(double maxDelay) {
        this.maxDelay = maxDelay;
        config.set(MAX_DELAY, maxDelay);
        saveConfig();
    }

    public boolean getSwapSetting(SwapAttribute attribute) {
        return toSWAP[attribute.getIndex()];
    }

    public void setSwapSetting(SwapAttribute attribute, boolean value){
        toSWAP[attribute.getIndex()] = value;
        config.set(SWAP_SETTINGS + attribute.getConfigValue(), value);
        saveConfig();
    }

    public Sound getSoundEffect() {
        return soundEffect;
    }

    public double getSoundVolume() {
        return soundVolume;
    }

    public double getSoundPitch() {
        return soundPitch;
    }

    public void setSoundEffect(Sound soundEffect, double soundVolume, double soundPitch) {
        this.soundEffect = soundEffect;
        this.soundVolume = soundVolume;
        this.soundPitch = soundPitch;
        config.set(SOUND_NAME, soundEffect.name());
        config.set(SOUND_VOLUME, soundVolume);
        config.set(SOUND_PITCH, soundPitch);
        saveConfig();
    }

    private void saveConfig() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getSwapFormat() {
        return swapFormat;
    }

    public void setSwapFormat(String swapFormat) {
        this.swapFormat = swapFormat;
    }

    public ArrayList<String> getSwapMessage() {
        return swapMessage;
    }


}
