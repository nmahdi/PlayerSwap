package com.playerswap;


import org.bukkit.plugin.java.JavaPlugin;

public class PlayerSwap extends JavaPlugin {

    private PSManager psManager;
    private PSCommand psCommand;

    @Override
    public void onEnable() {
        super.onEnable();
        this.psManager = new PSManager(this);
        this.psCommand = new PSCommand(this, psManager);
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }


}