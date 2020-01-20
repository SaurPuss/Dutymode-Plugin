package me.saurpuss.dutymode.duty.memory;

import me.saurpuss.dutymode.DutyMode;
import me.saurpuss.dutymode.duty.util.AbstractStorage;
import me.saurpuss.dutymode.duty.util.BukkitKryogenics;
import me.saurpuss.dutymode.duty.util.Kryogenic;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Level;

public class KryoStorage extends AbstractStorage implements Kryogenic {

    private DutyMode plugin;

    public KryoStorage(DutyMode plugin) {
        this.plugin = plugin;

        BukkitKryogenics.registerSerializers(Kryogenic.KRYO);
    }

    @Override
    public boolean savePlayerMemory(Player player, Snapshot snapshot) {
        final UUID uuid = player.getUniqueId();
        final File file = new File(plugin.getDataFolder(), "save/" + uuid.toString() + ".dat");

        if (!file.exists()) {
            try {
                file.mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.WARNING, "Failed to create save file for player "
                        + player.getName() + "!");
                return false;
            }
        }

        try {
            Kryogenic.freeze(file.toPath(), snapshot);
        } catch (RuntimeException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to save player memory for player "
                    + player.getName() + "!", e);
            return false;
        }
        return true;
    }

    @Override
    public Snapshot retrievePlayerMemory(final Player player) {
        return retrievePlayerMemory(player.getUniqueId());
    }

    @Override
    public Snapshot retrievePlayerMemory(final UUID uuid) {
        Path datafile = plugin.getDataFolder().toPath().resolve("save/" + uuid.toString() + ".dat");
        File file = new File(String.valueOf(datafile));

        if (!file.exists()) {
            plugin.getLogger().log(Level.WARNING, "Player memory does not exist!!");
            return null;
        }

        try {
            return Kryogenic.thaw(datafile);
        } catch (RuntimeException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to retrieve player memory for uuid "
                    + uuid.toString() + "!", e);
            return null;
        }
    }
}
