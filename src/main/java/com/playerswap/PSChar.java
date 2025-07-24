package com.playerswap;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PSChar {

    // All inventory types that will drop their items when a swap is initiated.
    private static HashSet<InventoryType> INV_TYPES = new HashSet<>(Set.of(InventoryType.CRAFTING, InventoryType.WORKBENCH, InventoryType.ANVIL,
            InventoryType.ENCHANTING, InventoryType.SMITHING, InventoryType.CARTOGRAPHY, InventoryType.STONECUTTER, InventoryType.GRINDSTONE));

    private String playerName;
    private double health;
    private int foodLevel;
    private float saturation;
    private Location location;
    private ItemStack[] inventory;
    private Collection<PotionEffect> potionEffects;
    private Entity vehicle;
    private Vector velocity;

    public void fromPlayer(Player player) {
        this.playerName = player.getName();
        this.health = player.getHealth();
        this.foodLevel = player.getFoodLevel();
        this.saturation = player.getSaturation();
        this.location = player.getLocation();
        this.inventory = player.getInventory().getContents();
        this.potionEffects = player.getActivePotionEffects();
        if(player.isInsideVehicle()) {
            this.vehicle = player.getVehicle();
            assert vehicle != null;
            this.velocity = vehicle.getVelocity();
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
    * */
    public void applyTo(Player otherPlayer) {
        if(otherPlayer.getName().equals(playerName)) return; // Return early if it's the same player

        World world = otherPlayer.getWorld();

        // Check if the player has an item on their cursor, then remove it and drop it in the world
        ItemStack cursor = otherPlayer.getItemOnCursor();
        if(!cursor.getType().isAir()) {
            otherPlayer.setItemOnCursor(null);
            world.dropItemNaturally(otherPlayer.getLocation(), cursor);
        }

        // Check if a player had an external inventory open that drops items, then drop the items in the world.
        Inventory topInv = otherPlayer.getOpenInventory().getTopInventory();
        if(INV_TYPES.contains(topInv.getType())) {
            ItemStack[] contents = topInv.getContents();
            topInv.clear();
            // Drop all items except the output slot items. With the exception being anvils.
            int cutoffIndex = contents.length - (topInv.getType().equals(InventoryType.ANVIL) ? 0 : 1);
            for(int i = 0; i < cutoffIndex; i++) {
                world.dropItemNaturally(otherPlayer.getLocation(), contents[i]);
            }
        }

        otherPlayer.setHealth(health);
        otherPlayer.setFoodLevel(foodLevel);
        otherPlayer.setSaturation(saturation);

        // If vehicle is null then teleport the player. If not, teleport the vehicle.
        if(vehicle == null) {
            otherPlayer.teleport(location);
            otherPlayer.setVelocity(velocity);
        }else{
            vehicle.teleport(location.add(0, 0.5d, 0));
            vehicle.setVelocity(velocity);
        }

        otherPlayer.getInventory().setContents(inventory);
        // Remove all potion effects
        for(PotionEffect effect : otherPlayer.getActivePotionEffects()) {
            otherPlayer.removePotionEffect(effect.getType());
        }
        otherPlayer.addPotionEffects(potionEffects);

        otherPlayer.playSound(otherPlayer, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 0.5f);
    }

    public String getPlayerName() {
        return playerName;
    }
}
