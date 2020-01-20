package me.saurpuss.dutymode.tasks;

import me.saurpuss.dutymode.DutyMode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class DutyReminder extends BukkitRunnable {

    private Player player;
    private static final String reminderMessage = ChatColor.translateAlternateColorCodes(
            '&', DutyMode.getInstance().getConfig().getString("messages.duty-reminder"));

    public DutyReminder(Player player) {
        this.player = player;
    }

    @Override
    public void run() {
        player.sendMessage(reminderMessage);
    }
}
