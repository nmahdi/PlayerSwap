package com.playerswap;


import org.bukkit.plugin.java.JavaPlugin;

public class PlayerSwap extends JavaPlugin {

    private PSManager swapManager;

    @Override
    public void onEnable() {
        super.onEnable();
        this.swapManager = new PSManager(this);
        new PSCommand(this, swapManager);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


}