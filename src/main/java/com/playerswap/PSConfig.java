package com.playerswap;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;

public class PSConfig {

    private final File file;
    private final FileConfiguration config;

    private double minDelay, maxDelay;
    private String swapMessage;
    private ArrayList<String> swapFormat = new ArrayList<>();

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

        if(config.contains("min-delay")) {
            minDelay = config.getDouble("min-delay");
        }else{
            minDelay = 5d;
        }

        maxDelay = config.getInt("max-delay");

        swapMessage = ChatColor.translateAlternateColorCodes('&', config.getString("swap-message"));

        for(String s : config.getStringList("swap-format")) {
            swapFormat.add(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    public double getMinDelay() {
        return minDelay;
    }

    public double getMaxDelay() {
        return maxDelay;
    }

    public String getSwapMessage() {
        return swapMessage;
    }

    public ArrayList<String> getSwapFormat() {
        return swapFormat;
    }


}
