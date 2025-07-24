package com.playerswap;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class PSCommand implements CommandExecutor, TabExecutor {

    private final PSManager psManager;
    private final PSConfig psConfig;

    public PSCommand(PlayerSwap main, PSManager swapManager) {
        this.psManager = swapManager;
        this.psConfig = psManager.getConfig();
        Objects.requireNonNull(main.getCommand("playerswap")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(/*cmd.getName().equalsIgnoreCase("playerswap") && */ sender.hasPermission("playerswap.admin")) {

            if(args.length < 1) {
                sender.sendMessage(ChatColor.GRAY + "/playerswap toggle - Toggles swapping on/off" +
                        "\n/playerswap min [time in seconds] - Sets the minimum amount of time between swaps" +
                        "\n/playerswap max [time in seconds] - Sets the maximum amount of time between swaps" +
                        "\n/playerswap players - Displays the amount of players vs available template copies [DEBUG]");
                return true;
            }

            switch(args[0].toLowerCase()) {
                case "toggle": {
                    psManager.toggleSwap(sender);
                    return true;
                }
                case "min": {
                    if(args.length < 2) {
                        sender.sendMessage(ChatColor.GRAY + "/playerswap min [time in seconds]");
                        return true;
                    }
                    try {
                        try {
                            psConfig.setMinDelay(Double.parseDouble(args[1]));
                            sender.sendMessage(ChatColor.GREEN + "[PlayerSwap] Minimum delay is now: " + args[1] + " seconds.");
                            return true;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
                        return false;
                    }
                }
                case "max": {
                    if(args.length < 2) {
                        sender.sendMessage(ChatColor.GRAY + "/playerswap max [time in seconds]");
                        return true;
                    }
                    try {
                        try {
                            psConfig.setMaxDelay(Double.parseDouble(args[1]));
                            sender.sendMessage(ChatColor.GREEN + "[PlayerSwap] Maximum delay is now: " + args[1] + " seconds.");
                            return true;
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }catch (NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "'" + args[1] + "' is not a valid number.");
                        return false;
                    }
                }
                case "players": {
                    sender.sendMessage(ChatColor.GREEN + "[PlayerSwap] There are currently " + psManager.PLAYERS.size() + " players and " + psManager.PSCHARS.size() + " chars saved.");
                    return true;
                }
            }
        }

        sender.sendMessage(ChatColor.RED + "Invalid permissions.");
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        if(args.length == 1) {

        }
        return List.of();
    }
}
