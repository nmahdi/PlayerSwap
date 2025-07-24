package com.playerswap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PSConfig {

    private final String MIN_DELAY = "min-delay";
    private final String MAX_DELAY = "max-delay";
    private final String SWAP_FORMAT = "swap-format";
    private final String SWAP_MESSAGE = "swap-message";

    private final File file;
    private final FileConfiguration config;

    private double minDelay, maxDelay;
    private String swapFormat;
    private final ArrayList<String> swapMessage = new ArrayList<>();

    public PSConfig(PlayerSwap main) {
        this.file = new File(main.getDataFolder(), "config.yml");

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

        if(config.contains(MIN_DELAY)) {
            minDelay = config.getDouble(MIN_DELAY);
        }else{
            minDelay = 5d;
        }

        if(config.contains(MAX_DELAY)) {
            maxDelay = config.getInt(MAX_DELAY);
        }else{
            maxDelay = 45d;
        }

        swapFormat = ChatColor.translateAlternateColorCodes('&', config.getString(SWAP_FORMAT));

        for(String s : config.getStringList(SWAP_MESSAGE)) {
            swapMessage.add(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public double getMinDelay() {
        return minDelay;
    }

    public void setMinDelay(double minDelay) throws IOException {
        this.minDelay = minDelay;
        config.set(MIN_DELAY, minDelay);
        config.save(file);
    }

    public double getMaxDelay() {
        return maxDelay;
    }

    public void setMaxDelay(double maxDelay) throws IOException {
        this.maxDelay = maxDelay;
        config.set(MAX_DELAY, maxDelay);
        config.save(file);
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
