package me.saurpuss.dutymode.duty;

import me.saurpuss.dutymode.DutyMode;
import me.saurpuss.dutymode.duty.memory.DutyGroup;
import me.saurpuss.dutymode.duty.memory.KryoStorage;
import me.saurpuss.dutymode.duty.memory.Memory;
import me.saurpuss.dutymode.duty.memory.Snapshot;
import me.saurpuss.dutymode.duty.util.AbstractStorage;
import me.saurpuss.dutymode.duty.util.StorageType;
import me.saurpuss.dutymode.duty.util.TaskChainActionHandlers;
import me.saurpuss.dutymode.tasks.DutyReminder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;

public class DutyManager {

    private DutyMode plugin;

    private final AbstractStorage storage;
    private final FileConfiguration config;

    private final Map<String, DutyGroup> dutyGroups;
    private Map<UUID, Memory> onDuty;

    private boolean allowPersistOffline; // TODO

    public DutyManager(DutyMode plugin) {
        this.plugin = plugin;
        config = plugin.getConfig();

        // Get settings related stuff
        StorageType storageType = StorageType.getIfPresent(config.getString("storage-method"));
        switch (storageType) {
            case MYSQL:
                // TODO validate sql credentials
//                storage = new MySQLStorage(plugin);
//                break;
            case KRYO:
            default:
                storage = new KryoStorage(plugin);
        }

//        allowPersistOffline = config.getBoolean("persist-offline"); // TODO

        dutyGroups = getDutyGroups();

        // Get runtime storage ready
        onDuty = new IdentityHashMap<>();

        plugin.getLogger().log(Level.INFO, "DutyManager is ready for action!");
    }

    private Map<String, DutyGroup> getDutyGroups() {
        Map<String, DutyGroup> list = new HashMap<>();

        // Loop through the groups in groups.yml
        for (String group : config.getConfigurationSection("group").getKeys(false)) {
            // Grab variables
            boolean broadcast = config.getBoolean("group." + group + ".broadcast");
            long reminder = config.getLong("group." + group + ".reminder");
            boolean inventory = config.getBoolean("group." + group + ".duty-inventory");
            // TODO duty items
            boolean op = config.getBoolean("group." + group + ".toggle-op");
            GameMode gameMode = GameMode.valueOf(config.getString("group." + group + ".game-mode"));
            boolean dropItems = config.getBoolean("group." + group + ".drop-items");
            List<String> perms = config.getStringList("group." + group + ".temporary-permissions");
            List<String> cmdON = config.getStringList("group." + group + ".console-commands" +
                    "-enable");
            List<String> cmdOFF = config.getStringList("group." + group + ".console-commands" +
                    "-disable");

            // Build the DutyGroup
            list.put(group,
                    new DutyGroup.Builder(group).withBroadcast(broadcast).withReminder(reminder)
                            .withInventory(inventory).withOp(op).withGameMode(gameMode)
                            .withDropItems(dropItems).withTemporaryPermissions(perms)
                            .withConsoleCommandsOn(cmdON).withConsoleCommandsOff(cmdOFF).build());
        }

        return list;
    }

    // duty application
    public boolean enableDuty(Player player) {
        if (!player.hasPermission("duties.use")) return false;

        DutyGroup group = hasDutyGroup(player);
        if (group == null) {
            player.sendMessage(config.getString("messages.group-not-found"));
            return false;
        }

        // Create a player snapshot & save it
        Snapshot snap = new Snapshot(player);
        boolean saved = saveDatabaseSnapshot(player, snap); // save snapshot

        BukkitTask task = null;
        if (group.getReminder() >= 1) {
            task = new DutyReminder(player).runTaskTimer(plugin, group.getReminder() * 20,
                    group.getReminder() * 20);
        }

        Memory memory = new Memory(player.getUniqueId(), snap, group,
                permissionSetter(player, snap), task);
        onDuty.put(player.getUniqueId(), memory); // list player as active duty
        dutyCommands(player.getName(), group.getConsoleCommandsOn()); // run commands

        boolean state = applyDutyGroupState(player, group);

        broadcast(player, true, group.isBroadcast());
        return saved && state;
    }

    public boolean disableDuty(Player player) {
        DutyGroup group = hasDutyGroup(player);
        if (group == null) {
            player.sendMessage(config.getString("messages.group-not-found"));
            plugin.getLogger().log(Level.WARNING,
                    "Failed to retrieve a valid duty group for " + player.getName() + "!");
            return false;
        }

        Memory memory = onDuty.get(player.getUniqueId());
        Snapshot snap = memory.getSnapshot();
        if (snap == null) {
            plugin.getLogger().log(Level.WARNING, "Failed to retrieve in memory snapshot for " +
                    "player " + player.getName() + " attempting to restore from storage!");
            snap = storage.retrievePlayerMemory(player); // retrieve backup
            if (snap == null) {
                plugin.getLogger().log(Level.SEVERE, "Failed to restore player snapshot for " +
                        "player " + player.getName() + " from storage! Make sure valid storage is" +
                        " configured!!");
                player.sendMessage(config.getString("messages.disable-duty-fail"));
                return false;
            }
        }

        if (memory.getReminderTask() != null) {
            memory.getReminderTask().cancel(); // TODO should I do this in the scheduler?
        }
        boolean removed = permissionRemover(player.getUniqueId());
        if (!removed) plugin.getLogger().log(Level.WARNING,
                "Failed to remove duty permissions from " + player.getName() + "!");

        dutyCommands(player.getName(), group.getConsoleCommandsOff());
        boolean restore = snap.restore(player);

        onDuty.remove(player.getUniqueId());

        broadcast(player, false, group.isBroadcast());
        return removed && restore;
    }

    public boolean isOnDuty(Player player) {
        return onDuty.containsKey(player.getUniqueId());
    }

    public boolean toggleActiveDuty(Player player) {
        return isOnDuty(player) ? disableDuty(player) : enableDuty(player);
    }

    public void purge() {
        plugin.getLogger().log(Level.INFO, "Purging all active duties!");

        onDuty.keySet().forEach(uuid -> disableDuty(Bukkit.getPlayer(uuid)));
        onDuty.clear(); // Clean map

        plugin.getLogger().log(Level.INFO, "Finished purging all active duties!");
    }

    // duty implementation
    private PermissionAttachment permissionSetter(Player player, Snapshot snapshot) {
        PermissionAttachment attachment = player.addAttachment(plugin);

        for (String perm : snapshot.getPermissions().keySet())
            attachment.setPermission(perm, true);

        return attachment;
    }

    private boolean permissionRemover(UUID uuid) {
        final Memory memory = onDuty.get(uuid);
        PermissionAttachment attachment = memory.getAttachment();

        if (attachment != null) {
            for (String perm : memory.getSnapshot().getPermissions().keySet())
                attachment.unsetPermission(perm);
        } else
            return false;

        return true;
    }

    private DutyGroup hasDutyGroup(Player player) {
        DutyGroup group = null;
        for (String dutyGroup : dutyGroups.keySet()) {
            if (player.hasPermission(dutyGroup))
                group = dutyGroups.get(dutyGroup);
        }

        return group;
    }

    private boolean applyDutyGroupState(Player player, DutyGroup group) {
        if (group.isInventory()) player.getInventory().clear();
        if (group.isOp()) player.setOp(true); // TODO

        player.setGameMode(group.getGameMode());

        return true;
    }

    private void dutyCommands(String playerName, List<String> commands) {
        if (commands.isEmpty()) return;

        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        for (String command : commands) {
            String cmd = command.replaceAll("%PLAYER_NAME%", playerName);
            Bukkit.dispatchCommand(sender, cmd);
        }
    }

    private void broadcast(Player player, boolean enable, boolean broadcast) {
        final String notify = ChatColor.translateAlternateColorCodes('&', enable ?
                config.getString("messages.duty-notify-on")
                        .replaceAll("%PLAYER_NAME%", player.getDisplayName()) :
                config.getString("messages.duty-notify-off")
                        .replaceAll("%PLAYER_NAME%", player.getDisplayName()));
        final String message = ChatColor.translateAlternateColorCodes('&', enable ?
                config.getString("messages.duty-on") : config.getString("messages.duty-off"));

        if (broadcast) {
            for (Player dutyUser : Bukkit.getOnlinePlayers()) {
                if (player.equals(dutyUser)) {
                    player.sendMessage(message);
                } else if (dutyUser.hasPermission("duties.notify"))
                    dutyUser.sendMessage(notify);
            }
        } else {
            player.sendMessage(message);
        }

        plugin.getLogger().log(Level.INFO, notify);
    }

    public Snapshot getSnapshot(Player player) {
        return onDuty.get(player.getUniqueId()).getSnapshot();
    }

    public Snapshot getDatabaseSnapshot(final Player player) {
        AtomicReference<Snapshot> snapshot = new AtomicReference<>();
        final String taskChain = "CHAIN_SAVE"; // TODO

        DutyMode.newSharedChain(taskChain).asyncFirst(() ->
            storage.retrievePlayerMemory(player.getUniqueId()))
                .abortIfNull(TaskChainActionHandlers.MESSAGE, player, config.getString(
                "messages.memory-retrieve-fail"))
                .syncLast(snapshot::set).execute();

        return snapshot.get(); // returns null if retrieval failed
    }

    public boolean saveDatabaseSnapshot(final Player player, final Snapshot snapshot) {
        final String taskChain = "CHAIN_RETRIEVE"; // TODO
        AtomicBoolean bool = new AtomicBoolean(false);

        DutyMode.newSharedChain(taskChain).asyncFirst(() -> storage.savePlayerMemory(player, snapshot))
                .abortIfNull(TaskChainActionHandlers.MESSAGE, player, config.getString(
                        "messages.memory-save-fail")).syncLast(bool::set).execute();

        return bool.get();
    }

    public Map<String, Boolean> getGroupPermissions(String groupName) {
        return dutyGroups.get(groupName).getTemporaryPermissions();
    }

    public PermissionAttachment getAttachment(UUID uuid) {
        return onDuty.get(uuid).getAttachment();
    }

    public DutyGroup getGroup(UUID uuid) {
        return onDuty.get(uuid).getGroup();
    }
}


