package me.saurpuss.dutymode.duty.memory;

import me.saurpuss.dutymode.DutyMode;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Snapshot {

    private transient DutyMode plugin = DutyMode.getInstance();

    private UUID uuid;
    private String group;
    private Map<String, Boolean> permissions;
    private boolean onDuty; // TODO for persisting duty

    // TODO timestamp, then multiply difference and remove from ticksLived on restore

    private Location location;
    private Entity vehicle;
    private GameMode gameMode;
    private ItemStack[] inventory;
    private float walkSpeed;
    private float flySpeed;
    private boolean isFlying;
    private double healthScale;
    private double health;
    private int foodLevel;
    private int level;
    private float experience;
    private float saturation;
    private float exhaustion;
    private int fireTicks;
    private int remainingAir;
    private Collection<PotionEffect> potionEffects;
    private Location bedSpawnLocation;

    //The memory where the plugin saves your inventory, location, armor etc...
    public Snapshot(Player player) {
        uuid = player.getUniqueId();
        group = plugin.getDutyManager().getGroup(uuid).getGroupName();
        permissions = plugin.getDutyManager().getGroup(uuid).getTemporaryPermissions();
        onDuty = true; // TODO

        location = player.getLocation();
        vehicle = player.getVehicle();
        gameMode = player.getGameMode();
        inventory = player.getInventory().getContents(); // TODO verify for armor slots
        walkSpeed = player.getWalkSpeed();
        flySpeed = player.getFlySpeed();
        isFlying = player.isFlying();
        healthScale = player.getHealthScale();
        health = player.getHealth();
        foodLevel = player.getFoodLevel();
        level = player.getLevel();
        experience = player.getExp();
        saturation = player.getSaturation();
        exhaustion = player.getExhaustion();
        remainingAir = player.getRemainingAir();
        fireTicks = player.getFireTicks();
        potionEffects = player.getActivePotionEffects();
        bedSpawnLocation = player.getBedSpawnLocation();
    }

    public boolean restore() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            plugin.getLogger().log(Level.WARNING, "Attempt to restore last save state failed! " +
                    "You cannot restore an offline player!");
            return false;
        }

        return restore(player);
    }

    public boolean restore(Player player) {
        UUID id = player.getUniqueId();

        PermissionAttachment attachment = plugin.getDutyManager().getAttachment(id);
        if (attachment == null) return false;

        for (String perm : permissions.keySet())
            attachment.unsetPermission(perm);

        onDuty = false; // TODO

        player.teleport(location);
        if (vehicle != null) vehicle.addPassenger(player);
        player.setGameMode(gameMode);
        player.getInventory().setContents(inventory); // TODO verify armor slots
        player.setWalkSpeed(walkSpeed);
        player.setFlySpeed(flySpeed);
        player.setFlying(isFlying);
        player.setHealthScale(healthScale);
        player.setHealth(health);
        player.setFoodLevel(foodLevel);
        player.setLevel(level);
        player.setExp(experience);
        player.setSaturation(saturation);
        player.setExhaustion(exhaustion);
        player.setRemainingAir(remainingAir);
        player.setFireTicks(fireTicks);
        player.addPotionEffects(potionEffects);
        player.setBedSpawnLocation(bedSpawnLocation);

        return true;
    }

    public boolean restoreFromDatabase(Player player, Snapshot snapshot) {
        // TODO

        return true;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getGroup() {
        return group;
    }

    public Map<String, Boolean> getPermissions() {
        return permissions;
    }

    public boolean isOnDuty() {
        return onDuty;
    }

    public Location getLocation() {
        return location;
    }

    public Entity getVehicle() {
        return vehicle;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public ItemStack[] getInventory() {
        return inventory;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    public boolean isFlying() {
        return isFlying;
    }

    public double getHealthScale() {
        return healthScale;
    }

    public double getHealth() {
        return health;
    }

    public int getFoodLevel() {
        return foodLevel;
    }

    public int getLevel() {
        return level;
    }

    public float getExperience() {
        return experience;
    }

    public float getSaturation() {
        return saturation;
    }

    public float getExhaustion() {
        return exhaustion;
    }

    public int getFireTicks() {
        return fireTicks;
    }

    public int getRemainingAir() {
        return remainingAir;
    }

    public Collection<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public Location getBedSpawnLocation() {
        return bedSpawnLocation;
    }
}
