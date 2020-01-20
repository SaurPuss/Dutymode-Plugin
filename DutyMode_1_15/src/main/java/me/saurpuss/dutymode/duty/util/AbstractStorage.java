package me.saurpuss.dutymode.duty.util;

import me.saurpuss.dutymode.duty.memory.Snapshot;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class AbstractStorage {

    public abstract boolean savePlayerMemory(Player player, Snapshot snapshot);

    public abstract Snapshot retrievePlayerMemory(Player player);

    public abstract Snapshot retrievePlayerMemory(UUID uuid);

}
