package me.saurpuss.dutymode.duty.memory;

import me.saurpuss.dutymode.DutyMode;
import me.saurpuss.dutymode.duty.util.AbstractStorage;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MySQLStorage extends AbstractStorage {

    // TODO hikari

    private DutyMode plugin;

    public MySQLStorage(DutyMode plugin) {
        this.plugin = plugin;


    }

    @Override
    public boolean savePlayerMemory(Player player, Snapshot snapshot) {
        return false;
    }

    @Override
    public Snapshot retrievePlayerMemory(Player player) {
        return null;
    }

    @Override
    public Snapshot retrievePlayerMemory(UUID uuid) {
        return null;
    }

    private void connect() {

        // TODO work with hikari

    }

    private void createTables() {

        // TODO create tables


    }
}
