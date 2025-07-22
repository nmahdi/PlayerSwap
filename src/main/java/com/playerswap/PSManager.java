package com.playerswap;

import org.bukkit.Bukkit;
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

    private final PlayerSwap main;
    private final Random random = ThreadLocalRandom.current();
    private boolean enabled = false;
    private BukkitTask currentTask;

    private ArrayList<PSChar> psChars = new ArrayList<>();

    public PSManager(PlayerSwap main) {
        this.main = main;
        this.main.getServer().getPluginManager().registerEvents(this, main);
    }

    public void swapOn() {
        enabled = true;
        initiateSwap(3f);
        System.out.println("Player Swap is now enabled");
    }

    public void swapOff() {
        enabled = false;
        currentTask.cancel();
        System.out.println("Player Swap is now disabled");
    }

    /*
    * Initiates an asynchronous delayed task based on the formula baseDelay+-2.0
    * in seconds.
    * */
    private void initiateSwap(float baseDelay) {
        float delay = baseDelay+random.nextFloat(-2, 2);
        currentTask = new BukkitRunnable() {
            @Override
            public void run() {

                int index = 0;
                for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
                    psChars.get(index).fromPlayer(currentPlayer);
                    index++;
                }
                
                Collections.shuffle(psChars);

                index = 0;
                for(Player currentPlayer : Bukkit.getOnlinePlayers()) {
                    psChars.get(index).swapTo(currentPlayer);
                    index++;
                }

                initiateSwap(baseDelay);
                System.out.println("Ran after " + delay + " seconds");

            }

        }.runTaskLaterAsynchronously(main, (long)delay*20);
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
