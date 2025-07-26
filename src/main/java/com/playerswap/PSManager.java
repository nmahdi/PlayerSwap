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
     * TO TEST:
     *      - Top inventory view dropping items
     *      - Remove swapping from currently dead players
     *      - Teleport entities that are attached to players like boats, pigs, horses
     *      - New config settings (swap-settings)
     *      - Spamming /ps run
     *      - ps settings/sound
     * INTENTIONAL FEATURES:
     *      - A player can throw an ender pearl before they swap, and will get teleported again when their ender pearl lands
     * */

    private final PlayerSwap main;
    private final PSConfig config;
    private final Random random = ThreadLocalRandom.current();

    private boolean swapToggle = false;
    private boolean singleSwap = false;
    private BukkitTask currentTask;

    public final ArrayList<Player> PLAYERS = new ArrayList<>();
    public final ArrayList<Chunk> LOADED_CHUNKS = new ArrayList<>();

    public final String PREFIX = "[PlayerSwap] ";

    public PSManager(PlayerSwap main) {
        this.main = main;
        this.config = new PSConfig(main);
        this.main.getServer().getPluginManager().registerEvents(this, main);
    }

    // Toggles the swap mode on/off
    public void toggleSwap(CommandSender sender) {
        if(!swapToggle) {

            if(PLAYERS.size() < 2) {
                sender.sendMessage(ChatColor.RED + PREFIX + "You need at least two people to turn on player swap.");
                return;
            }

            swapToggle = true;
            initiateSwap(true);
            sender.sendMessage(ChatColor.GREEN + PREFIX + "Swapping has been enabled.");
            return;
        }

        swapToggle = false;
        currentTask.cancel();
        sender.sendMessage(ChatColor.GREEN + PREFIX + "Swapping has been disabled.");
    }

    public void swap(CommandSender sender) {
        if(singleSwap) {
            sender.sendMessage(ChatColor.RED + PREFIX + "A swap has already been initiated.");
            return;
        }
        else if(PLAYERS.size() < 2) {
            sender.sendMessage(ChatColor.RED + PREFIX + "You need at least two people to turn on player swap.");
            return;
        }

        // Set singleSwap to true to reduce '/ps run' spam
        singleSwap = true;
        initiateSwap(false);
        sender.sendMessage(ChatColor.GREEN + PREFIX + "A single swap has been initiated.");
    }

    /*
    * Initiates a delayed BukkitRunnable task based from 5 to the provided maxDelay in seconds.
    * Will recursively run if continuous is true
    * */
    private void initiateSwap(boolean continuous) {
        double delay = random.nextDouble() * (config.getMaxDelay() - config.getMinDelay()) + config.getMinDelay();
        currentTask = new BukkitRunnable() {
            @Override
            public void run() {

                ArrayList<PSChar> chars = new ArrayList<>();

                int index = 0;
                // Loop through online players, and save a snapshot of their information
                for(Player currentPlayer : PLAYERS) {
                    chars.add(new PSChar(currentPlayer));
                    // Add a chunk ticket to ensure the chunk is loading
                    loadChunk(currentPlayer.getLocation().getChunk());
                    index++;
                }

                // Shuffle the saved snapshots
                ArrayList<PSChar> shuffled = new ArrayList<>(chars);
                validateShuffle(chars, shuffled);

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
                    chars.get(index).applyTo(config, currentPlayer);

                    Bukkit.broadcastMessage(config.getSwapFormat().replaceAll("%original%",currentPlayer.getName())
                            .replaceAll("%new%", chars.get(index).getPlayerName()));

                    index++;
                }

                // Print the rest of the swap message.
                for(int i = swapIndex+1; i < config.getSwapMessage().size(); i++) {
                    Bukkit.broadcastMessage(config.getSwapMessage().get(i).replaceAll("%delay%", String.format("%.2f", delay)));
                }

                // Remove all the chunk tickets & clear LOADED_CHUNKS
                for(Chunk chunk : LOADED_CHUNKS) {
                    chunk.removePluginChunkTicket(main);
                }
                LOADED_CHUNKS.clear();

                // Set single swap back to false so '/ps run' can be used again
                singleSwap = false;

                if(continuous)
                    initiateSwap(true);

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
            swapToggle = false;
            currentTask.cancel();
            System.out.println("Swapping has been disabled since there is less than 2 people on.");
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        removePlayer(e.getEntity());
        // Automatically disables swap mode when all players leave
        if(e.getEntity().getServer().getOnlinePlayers().size() == 2) {
            swapToggle = false;
            currentTask.cancel();
            System.out.println("Swapping has been disabled since there is less than 2 people alive.");
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
    }

    // Removes the last PSChar object from the list
    private void removePlayer(Player player) {
        PLAYERS.remove(player);
    }

    // Adds a chunk ticket to the 5x5 chunks around (including) the provided chunk
    private void loadChunk(Chunk chunk) {
        for(int x = chunk.getX()-2; x < chunk.getX()+2; x++){
            for(int z = chunk.getZ()-2; z < chunk.getZ()+2; z++) {
                Chunk newChunk = chunk.getWorld().getChunkAt(x, z);
                if(!newChunk.getPluginChunkTickets().contains(main)) {
                    chunk.addPluginChunkTicket(main);
                    LOADED_CHUNKS.add(newChunk);
                }
            }
        }
    }

    public PSConfig getConfig() {
        return config;
    }

}
