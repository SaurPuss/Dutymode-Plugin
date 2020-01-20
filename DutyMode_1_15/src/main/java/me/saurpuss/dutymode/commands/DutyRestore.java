package me.saurpuss.dutymode.commands;

import me.saurpuss.dutymode.DutyMode;
import me.saurpuss.dutymode.duty.util.SubCommand;
import org.bukkit.entity.Player;

public class DutyRestore extends SubCommand {

    private DutyMode plugin;

    DutyRestore(DutyMode plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(Player player, String[] args) {
        if (!plugin.getDutyManager().isOnDuty(player))
            plugin.getDutyManager().getDatabaseSnapshot(player).restore();

        return true;
    }

    @Override
    public String name() {
        return "restore";
    }

    @Override
    public String[] getUsage() {
        return new String[] { "/duty restore - Restore the player state that was last saved to " +
                "the database before activating duty mode."};
    }

    @Override
    public String[] aliases() {
        return new String[0];
    }
}