package com.playerswap;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PSCommand implements TabExecutor {

    private final PSManager psManager;
    private final PSConfig psConfig;

    private enum ValidArgs {
        Toggle("toggle", "- Toggles swapping on/off continuously"),
        Min("min", "[time in seconds] - Sets the minimum amount of time between swaps"),
        Max("max", "[time in seconds] - Sets the maximum amount of time between swaps"),
        Settings("settings", "[setting configName] [true:false] - Toggles the following"),
        Sound("sound", "[effect name] [volume] [pitch] - Changes the volume played when swapping"),
        Reload("reload", "- Reloads all values from 'config.yml'"),
        Players("players", "- Displays the amount of players vs available template copies [DEBUG]"),
        Chunks("chunks", "- Displays the amount of chunks currently loaded by the plugin [DEBUG]"),
        Run("run", "- Initiates a single swap [DEBUG]")
        ;

        private final String name, desc;

        ValidArgs(String arg, String desc) {
            this.name = arg;
            this.desc = desc;
        }

        public String getName() {
            return name;
        }

        public String getDesc() {
            return desc;
        }
    }

    public PSCommand(PlayerSwap main, PSManager swapManager) {
        this.psManager = swapManager;
        this.psConfig = psManager.getConfig();
        Objects.requireNonNull(main.getCommand("playerswap")).setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.hasPermission("playerswap.admin")) {

            if(args.length < 1) {
                sendSyntax(sender);
                return true;
            }

            switch(args[0].toLowerCase()) {
                case "toggle": {
                    psManager.toggleSwap(sender);
                    break;
                }

                case "min": {
                    if(args.length < 2) {
                        sender.sendMessage(ChatColor.GRAY + "/playerswap min [time in seconds]");
                        break;
                    }
                    changeMinDelay(sender, args[1]);
                    break;
                }

                case "max": {
                    if (args.length < 2) {
                        sender.sendMessage(ChatColor.GRAY + "/playerswap max [time in seconds]");
                        break;
                    }
                    changeMaxDelay(sender, args[1]);
                }

                case "settings": {
                    if(args.length < 3) {
                        sender.sendMessage(ChatColor.GRAY +
                                "/playerswap settings [health:hunger:saturation:location:inventory:potion-effects:vehicle:velocity] [true:false]");
                        break;
                    }
                    toggleSetting(sender, args[1], Boolean.parseBoolean(args[2]));
                    break;
                }

                case "sound": {
                    if(args.length < 4) {
                        sender.sendMessage("/sound [effect name] [volume] [pitch]");
                        break;
                    }

                    changeSound(sender, args[1], Double.parseDouble(args[2]), Double.parseDouble(args[3]));
                    break;
                }

                case "reload": {
                    psConfig.loadConfig();
                    sender.sendMessage(ChatColor.GREEN + psManager.PREFIX + "Config has been reloaded.");
                    break;
                }

                case "players": {
                    sender.sendMessage(ChatColor.GREEN + psManager.PREFIX + "There are currently " + psManager.PLAYERS.size() + " players available to swap.");
                    break;
                }

                case "chunks": {
                    sender.sendMessage(ChatColor.GREEN + psManager.PREFIX + "There are currently " + psManager.LOADED_CHUNKS.size() + " chunks with tickets.");
                    break;
                }

                case "run": {
                    psManager.swap(sender);
                    break;
                }

                default: {
                    sendSyntax(sender);
                    break;
                }
            }
            return true;
        }

        sender.sendMessage(ChatColor.RED + "Invalid permissions.");
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        ArrayList<String> toReturn = new ArrayList<>();

        if(args.length == 1) {
            for(ValidArgs arg : ValidArgs.values()) {
                toReturn.add(arg.name);
            }
        }

        return toReturn;
    }

    private void sendSyntax(CommandSender sender) {
        for(ValidArgs arg : ValidArgs.values()) {
            sender.sendMessage(ChatColor.GRAY + "/playerswap " + arg.name + " " + arg.desc);
        }
    }

    private void changeMinDelay(CommandSender sender, String delay){
        try {
            psConfig.setMinDelay(Double.parseDouble(delay));
            sender.sendMessage(ChatColor.GREEN + psManager.PREFIX + "Minimum delay is now: " + delay + " seconds.");
        }catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "'" + delay + "' is not a valid number.");
        }
    }

    private void changeMaxDelay(CommandSender sender, String delay) {
        try {
            psConfig.setMaxDelay(Double.parseDouble(delay));
            sender.sendMessage(ChatColor.GREEN + psManager.PREFIX + "Maximum delay is now: " + delay + " seconds.");
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "'" + delay + "' is not a valid number.");
        }
    }

    private void toggleSetting(CommandSender sender, String setting, boolean value) {
        try{
            psConfig.setSwapSetting(SwapAttribute.valueOf(setting), value);
        } catch(IllegalArgumentException e) {
            sender.sendMessage(ChatColor.GRAY +
                    "/playerswap settings [health:hunger:saturation:location:inventory:potion-effects:vehicle:velocity] [true:false]");
        }
    }

    private void changeSound(CommandSender sender, String effect, double volume, double pitch) {
        try {
            psConfig.setSoundEffect(Sound.valueOf(effect), volume, pitch);
        }catch(IllegalArgumentException e) {
            sender.sendMessage(ChatColor.GRAY + "/sound [effect name] [volume] [pitch]");
        }
    }
}
