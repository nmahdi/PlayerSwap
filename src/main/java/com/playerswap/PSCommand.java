package com.playerswap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class PSCommand implements CommandExecutor {

    private final PSManager swapManager;

    public PSCommand(PlayerSwap main, PSManager swapManager) {
        this.swapManager = swapManager;
        Objects.requireNonNull(main.getCommand("playerswap")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("playerswap") && sender.hasPermission("playerswap.admin")) {
            if(swapManager.isEnabled()) {
                swapManager.swapOff();
            }else{
                swapManager.swapOn();
            }
        }
        return false;
    }

}
