package com.playerswap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class PSManager implements Listener {

    /*
     * TO DO:
     *      - Create a system for keeping the swapping enabled when dying & respawning with 2 people.
     *      - Create a better chunk loading system
     *      - Teleport entities that are attached to players like boats, pigs, horses
     *      - Use dropItem instead of dropItemNaturally()
     *      - Enchantment Smithing Stonecutter Cortagoraphy -- Add to anvil check
     *      - InventoryTypes: Merchant, Loom, 
     * TO TEST:
     *      - Remove swapping from currently dead players
     * INTENTIONAL FEATURES:
     *      - A player can throw an ender pearl before they swap, and will get teleported again when their ender pearl lands
     * */

    private final PlayerSwap main;
    private final PSConfig config;
    private final Random random = ThreadLocalRandom.current();

    private boolean enabled = false;
    private BukkitTask currentTask;

    public final ArrayList<Player> PLAYERS = new ArrayList<>();
    public final ArrayList<PSChar> PSCHARS = new ArrayList<>();
    public final ArrayList<Chunk> LOADED_CHUNKS = new ArrayList<>();

    public PSManager(PlayerSwap main) {
        this.main = main;
        this.config = new PSConfig(main);
        this.main.getServer().getPluginManager().registerEvents(this, main);
    }

    public void toggleSwap(CommandSender sender) {
        if(!enabled) {

            if(PLAYERS.size() < 2) {
                sender.sendMessage(ChatColor.RED + "[PlayerSwap] You need at least two people to turn on player swap.");
                return;
            }

            enabled = true;
            initiateSwap();
            sender.sendMessage(ChatColor.GREEN + "[PlayerSwap] Swapping has been enabled.");
            return;
        }

        enabled = false;
        currentTask.cancel();
        sender.sendMessage(ChatColor.GREEN + "[PlayerSwap] Swapping has been disabled.");
    }

    /*
    * Initiates a delayed BukkitRunnable task based from 5 to the provided maxDelay in seconds.
    * */
    private void initiateSwap() {
        double delay = random.nextDouble() * (config.getMaxDelay() - config.getMinDelay()) + config.getMinDelay();
        currentTask = new BukkitRunnable() {
            @Override
            public void run() {

                int index = 0;
                // Loop through online players, and save a snapshot of their information
                for(Player currentPlayer : PLAYERS) {
                    PSCHARS.get(index).fromPlayer(currentPlayer);
                    // Add a chunk ticket to ensure the chunk is loading
                    LOADED_CHUNKS.add(currentPlayer.getWorld().getChunkAt(currentPlayer.getLocation()));
                    currentPlayer.getWorld().getChunkAt(currentPlayer.getLocation()).addPluginChunkTicket(main);
                    index++;
                }

                // Shuffle the saved snapshots
                ArrayList<PSChar> shuffled = new ArrayList<>(PSCHARS);
                validateShuffle(PSCHARS, shuffled);

                // Print swap message & replace placeholders
                int swapIndex = 0;
                for(String s : config.getSwapMessage()) {
                    if(s.contains("%msg%")) break; // Break early if it's the player's message.
                    Bukkit.broadcastMessage(s.replaceAll("%delay%", String.format("%.2f", delay)));
                    swapIndex++;
                }

                index = 0;
                // Apply all the saved information to the new Player
                for(Player currentPlayer : PLAYERS) {
                    PSCHARS.get(index).applyTo(currentPlayer);

                    Bukkit.broadcastMessage(config.getSwapFormat().replaceAll("%original%", PSCHARS.get(index).getPlayerName())
                            .replaceAll("%new%", currentPlayer.getName()));

                    index++;
                }

                // Print the rest of the swap message.
                for(int i = swapIndex+1; i < config.getSwapMessage().size(); i++) {
                    Bukkit.broadcastMessage(config.getSwapMessage().get(i).replaceAll("%delay%", String.format("%.2f", delay)));
                }

                // Remove all the chunk tickets
                for(Chunk chunk : LOADED_CHUNKS) {
                    chunk.removePluginChunkTicket(main);
                }

                initiateSwap();

            }

        }.runTaskLater(main, (long)delay*20);
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        addPlayer(e.getPlayer());
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        removePlayer(e.getPlayer());
        // Automatically disables swap mode when all players leave
        if(e.getPlayer().getServer().getOnlinePlayers().size() == 2) {
            enabled = false;
            currentTask.cancel();
            System.out.println("Swapping has been disabled since there is less than 2 people on.");
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        removePlayer(e.getEntity());
        // Automatically disables swap mode when all players leave
        if(e.getEntity().getServer().getOnlinePlayers().size() == 2) {
            enabled = false;
            currentTask.cancel();
            System.out.println("Swapping has been disabled since there is less than 2 people on.");
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent e) {
        addPlayer(e.getPlayer());
    }

    // Ensures the shuffle is not the same as the original
    private void validateShuffle(ArrayList<PSChar> original, ArrayList<PSChar> shuffled) {
        do {
            Collections.shuffle(shuffled);
        } while (original.equals(shuffled));
        original.clear();
        original.addAll(shuffled);
    }

    // Adds an empty PSChar object to the list
    private void addPlayer(Player player) {
        PLAYERS.add(player);
        PSCHARS.add(new PSChar());
    }

    // Removes the last PSChar object from the list
    private void removePlayer(Player player) {
        PLAYERS.remove(player);
        PSCHARS.remove(PSCHARS.size() - 1);
    }

    public PSConfig getConfig() {
        return config;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
