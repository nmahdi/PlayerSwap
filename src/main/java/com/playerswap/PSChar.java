package com.playerswap;

import org.bukkit.Location;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PSChar {

    private final Player player;
    private final double health;
    private final int foodLevel;
    private final float saturation;
    private final int airRemaining;
    private final int fireTicks;
    private final Location location;
    private final ItemStack[] inventory;
    private final Collection<PotionEffect> potionEffects;
    private final Vector velocity;

    public PSChar(Player player) {
        this.player = player;
        player.closeInventory();
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.airRemaining = player.getRemainingAir();
        this.fireTicks = player.getFireTicks();
        this.location = player.getLocation();
        this.inventory = player.getInventory().getContents();
        this.potionEffects = player.getActivePotionEffects();
        // If the player is riding a vehicle, copy the vehicle's velocity instead of the players
        if(player.isInsideVehicle()){
            this.velocity = Objects.requireNonNull(player.getVehicle()).getVelocity();
        }else{
            this.velocity = player.getVelocity();
        }
    }

    /*
        Applies the saved information to the given player
        Current Supports:
            - Health
            - Hunger & Saturation
            - Location
            - Inventory
            - Potion Effects
            - Vehicle
            - Velocity
            - Ender Pearl Owners
    * */
    public void applyTo(PSConfig config, List<EnderPearl> pearls, Player otherPlayer) {
        if(otherPlayer.getName().equals(player.getName())) return; // Return early if it's the same player

        if(config.getSwapSetting(SwapAttribute.Location)) {

            Entity vehicle = null;
            if(config.getSwapSetting(SwapAttribute.Vehicle)) {

                if(otherPlayer.isInsideVehicle()) {
                    vehicle = otherPlayer.getVehicle();
                }

            }

            if(vehicle != null) {

                // Creates a copy of the current passengers
                List<Entity> passengers = new ArrayList<>(vehicle.getPassengers());

                // Make every passenger leave the vehicle
                if(!vehicle.getPassengers().isEmpty()){
                    for(Entity entity : passengers) {
                        entity.leaveVehicle();
                    }
                }

                // Teleport vehicle
                vehicle.teleport(location.add(0, 0.5, 0));

                // Loop through the passengers copy, then teleport & add them as a passenger
                for(Entity entity : passengers) {
                    entity.teleport(vehicle);
                    vehicle.addPassenger(entity);
                }

                vehicle.setVelocity(velocity);
            }else{
                otherPlayer.teleport(location);
                otherPlayer.setVelocity(velocity);
            }

        }

        if(config.getSwapSetting(SwapAttribute.Health)) {
            otherPlayer.setHealth(health);
        }

        if(config.getSwapSetting(SwapAttribute.Hunger)) {
            otherPlayer.setFoodLevel(foodLevel);
        }

        if(config.getSwapSetting(SwapAttribute.Saturation)) {
            otherPlayer.setSaturation(saturation);
        }

        if(config.getSwapSetting(SwapAttribute.AirBubbles)) {
            otherPlayer.setRemainingAir(airRemaining);
        }

        if(config.getSwapSetting(SwapAttribute.FireTicks)) {
            otherPlayer.setFireTicks(fireTicks);
        }

        if(config.getSwapSetting(SwapAttribute.Inventory)) {
            otherPlayer.getInventory().setContents(inventory);
        }

        if(config.getSwapSetting(SwapAttribute.PotionEffects)) {
            // Remove all potion effects
            for (PotionEffect effect : otherPlayer.getActivePotionEffects()) {
                otherPlayer.removePotionEffect(effect.getType());
            }
            otherPlayer.addPotionEffects(potionEffects);
        }

        if(config.getSwapSetting(SwapAttribute.EnderPearl)) {
            for(EnderPearl pearl : pearls) {
                if(pearl.getShooter() instanceof Player owner) {

                    if(owner.getUniqueId().equals(player.getUniqueId())) {
                        pearl.setShooter(otherPlayer);
                    }

                }
            }
        }

        if(config.isSoundEnabled()) {
            otherPlayer.playSound(otherPlayer, config.getSoundEffect(), config.getSoundVolume(), config.getSoundPitch());
        }
    }

    public String getPlayerName() {
        return player.getName();
    }
}
