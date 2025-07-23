package com.playerswap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Objects;

public class PSCommand implements CommandExecutor {

    private final PSManager psManager;

    public PSCommand(PlayerSwap main, PSManager swapManager) {
        this.psManager = swapManager;
        Objects.requireNonNull(main.getCommand("playerswap")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("playerswap") && sender.hasPermission("playerswap.admin")) {

            if(args.length < 1) {
                sender.sendMessage(ChatColor.GRAY + "/playerswap toggle - Toggles swapping on/off" +
                        "\n/playerswap min [time in seconds] - Sets the minimum amount of time between swaps" +
                        "\n/playerswap max [time in seconds] - Sets the maximum amount of time between swaps" +
                        "\n/playerswap chars - Displays the amount of available template copies [DEBUG]");
                return true;
            }

            switch(args[0].toLowerCase()) {
                case "toggle": {
                    psManager.toggleSwap(sender);
                    break;
                }
                case "min": {
                    break;
                }
            }

            return true;
        }

        sender.sendMessage(ChatColor.RED + "Invalid permissions.");
        return false;
    }

}
