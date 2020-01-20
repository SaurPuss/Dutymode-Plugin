package me.saurpuss.dutymode.listeners;

import me.saurpuss.dutymode.DutyMode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class ItemEvents implements Listener {

    private DutyMode plugin;
    private final String message;

    public ItemEvents(DutyMode plugin) {
        this.plugin = plugin;
        message = ChatColor.translateAlternateColorCodes('&',
                plugin.getConfig().getString("messages.duty-itemdrop"));
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (!event.getPlayer().hasPermission("duties.use")) return;
        Player player = event.getPlayer();
        if (!plugin.getDutyManager().isOnDuty(player)) return;

        if (!plugin.getDutyManager().getGroup(player.getUniqueId()).isDropItems()) {
            player.sendMessage(message);
            event.setCancelled(true);
        }
    }
}
