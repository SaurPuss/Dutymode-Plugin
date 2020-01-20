package me.saurpuss.dutymode.duty.util;

import org.bukkit.entity.Player;

public abstract class SubCommand {

    public abstract boolean onCommand(Player player, String args[]);

    public abstract String name();

    public abstract String[] getUsage();

    public abstract String[] aliases();

}
