package me.saurpuss.dutymode.commands;

import me.saurpuss.dutymode.DutyMode;
import me.saurpuss.dutymode.duty.util.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Reload extends SubCommand {

    private DutyMode plugin;

    Reload(DutyMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        CommandSender sender;
        if (player == null)
            sender = Bukkit.getConsoleSender();
        else {
            if (!player.hasPermission("duties.reload"))
                return true;

            sender = player;
        }

        sender.sendMessage(ChatColor.GRAY + "Disabling duty mode for active users!");
        plugin.getDutyManager().purge();

        sender.sendMessage(ChatColor.GRAY + "Reloading plugin configurations!");
        plugin.reloadConfig();

        sender.sendMessage(ChatColor.GRAY + "Reloading Duty Manager!");
        plugin.reloadDutyManager();

        sender.sendMessage(ChatColor.GREEN + "Finished reloading plugin!");

        return true;
    }

    @Override
    public String name() {
        return "reload";
    }

    @Override
    public String[] getUsage() {
        return new String[] { "/duty reload - Reload plugin configurations" };
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}
