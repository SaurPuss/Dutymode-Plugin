package me.saurpuss.dutymode.commands;

import me.saurpuss.dutymode.DutyMode;
import me.saurpuss.dutymode.duty.util.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class DutyToggle extends SubCommand {

    private DutyMode plugin;

    DutyToggle(DutyMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        if (!player.hasPermission("duties.use")) return true;

        // TODO everything
        boolean success;
        if (args.length == 0 || args[1].equalsIgnoreCase("toggle")) {
            success = plugin.getDutyManager().toggleActiveDuty(player);

            if (!success) player.sendMessage("Failed to toggle duty!");
            return true;
        }

        if (args[1].equalsIgnoreCase("on")) {
            success = plugin.getDutyManager().enableDuty(player);

            if (!success) player.sendMessage("Failed to enable duty!");
            return true;
        }

        if (args[1].equalsIgnoreCase("off")) {
            success = plugin.getDutyManager().disableDuty(player);

            if (!success) player.sendMessage("Failed to disable duty!");
            return true;
        }

        if (args[1].equalsIgnoreCase("purge")) {
            if (!player.hasPermission("duties.others"))  {
                player.sendMessage(ChatColor.DARK_RED + "You don't have permission to purge " +
                        "active duty users!");
                return true;
            }

            player.sendMessage(ChatColor.RED + "Purging active duties!"); // TODO messages config
            plugin.getDutyManager().purge();
            player.sendMessage(ChatColor.RED + "Finished purging active duties!"); // TODO messages config

            return true;
        }

        return false;
    }

    @Override
    public String name() {
        return "toggle";
    }

    @Override
    public String[] getUsage() {
        ArrayList<String> usage = new ArrayList<>();

        usage.add("Apply duty commands:");
        usage.add("/duty - Toggle duty mode");
        usage.add("/duty on - Enable duty mode");
        usage.add("/duty off - Disable duty mode");
        usage.add("/duty toggle - Toggle duty mode");

        usage.add("Apply duty status on another player: ");
        usage.add("/duty purge - Disable duty mode for all active users");
        usage.add("/duty [player] on - Enable duty mode for another player");
        usage.add("/duty [player] off - Disable duty mode for another player");
        usage.add("/duty [player] toggle - Toggle duty mode for another player");

        return (String[]) usage.toArray();
    }

    @Override
    public String[] aliases() {
        return new String[] {"on", "off", "purge"};
    }
}
