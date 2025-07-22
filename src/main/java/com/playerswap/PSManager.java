package com.playerswap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class PSManager implements Listener {

    /*
     * TO FIX:
     *   - Holding items on your cursor in the inventory brings them with you on swap
     *   - Remove swapping from currently dead players
     *   - Public chat announcement
     *   - Keeping chunks loaded
     *   - Re-shuffling in case of bad shuffling
     * */

    private final PlayerSwap main;
    private final Random random = ThreadLocalRandom.current();

    private boolean enabled = false;
    private BukkitTask currentTask;
    private final ArrayList<PSChar> psChars = new ArrayList<>();

    public PSManager(PlayerSwap main) {
        this.main = main;
        this.main.getServer().getPluginManager().registerEvents(this, main);
    }

    public void swapOn() {
        enabled = true;
        initiateSwap(45f);
        System.out.println("Player Swap is now enabled");
    }

    public void swapOff() {
        enabled = false;
        currentTask.cancel();
        System.out.println("Player Swap is now disabled");
    }

    /*
    * Initiates a delayed BukkitRunnable task based on the formula baseDelay+-2.0
    * in seconds.
    * */
    private void initiateSwap(float maxDelay) {
        float delay = random.nextFloat() * (maxDelay - 5) + 5;
        currentTask = new BukkitRunnable() {
            @Override
            public void run() {

                Bukkit.broadcastMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "--------------------------");
                Bukkit.broadcastMessage(ChatColor.GREEN + "Swap took " + delay + " seconds.");

                int index = 0;
                // Loop through online players, and save a snapshot of their information
                for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
                    psChars.get(index).fromPlayer(currentPlayer);
                    index++;
                }

                // Shuffle the saved snapshots
                Collections.shuffle(psChars);

                index = 0;
                // Apply all the saved information to the new Player
                for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
                    psChars.get(index).applyTo(currentPlayer);
                    Bukkit.broadcastMessage(ChatColor.AQUA + currentPlayer.getName() + " -> " + psChars.get(index).getPlayerName());
                    index++;
                }

                initiateSwap(maxDelay);
                Bukkit.broadcastMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "--------------------------");

            }

        }.runTaskLater(main, (long)delay*20);
    }

    // Adds an empty PSChar object to the list
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        psChars.add(new PSChar());
    }

    // Removes the last PSChar object from the list
    @EventHandler
    public void onLeave(PlayerQuitEvent e) {
        psChars.remove(psChars.size()-1);
    }

    public boolean isEnabled() {
        return enabled;
    }
}
