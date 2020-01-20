package me.saurpuss.dutymode;

import co.aikar.taskchain.BukkitTaskChainFactory;
import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainFactory;
import me.saurpuss.dutymode.commands.DutyCommand;
import me.saurpuss.dutymode.duty.DutyManager;
import me.saurpuss.dutymode.listeners.ItemEvents;
import me.saurpuss.dutymode.listeners.JoinLeaveEvents;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class DutyMode extends JavaPlugin {

    private static DutyMode instance;
    private DutyManager dutyManager;
    private static TaskChainFactory taskChainFactory;

    @Override
    public void onEnable() {
        // Le moi!
        instance = this;

        // Set up config files
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();

        // Duty!
        taskChainFactory = BukkitTaskChainFactory.create(this);
        dutyManager = new DutyManager(this);

        // Last bits!
        getCommand("duty").setExecutor(new DutyCommand(this));
        registerEvents();

        // We done!
        getLogger().log(Level.INFO, "Ready for duty!");
    }

    @Override
    public void onDisable() {
        // purge any active duties // TODO persist offline support
        dutyManager.purge();
    }

    private void registerCommands() {
        getCommand("duty").setExecutor(new DutyCommand(this));
    }

    private void registerEvents() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new JoinLeaveEvents(this), this);
        pm.registerEvents(new ItemEvents(this), this);
    }

    public static DutyMode getInstance() { return instance; }

    public DutyManager getDutyManager() {
        return dutyManager;
    }

    public void reloadDutyManager() {
        dutyManager.purge(); // Kick people out of duty
        dutyManager = new DutyManager(this);
    }

    public static <T> TaskChain<T> newChain() {
        return taskChainFactory.newChain();
    }
    public static <T> TaskChain<T> newSharedChain(String name) {
        return taskChainFactory.newSharedChain(name);
    }
}
