package me.saurpuss.dutymode.listeners;

import me.saurpuss.dutymode.DutyMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JoinLeaveEvents implements Listener {

    private DutyMode plugin;

    public JoinLeaveEvents(DutyMode plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!event.getPlayer().hasPermission("duties.use")) return;

        // TODO duty persist check and then restore duty if last snap onDuty is true
        // TODO Add to onDuty with retrieved snapshot
        // TODO set up the permission attachment again
        // TODO send reminder about being on duty
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if (!event.getPlayer().hasPermission("duties.use")) return;
        if (!plugin.getDutyManager().isOnDuty(event.getPlayer())) return;

        // TODO check if duty persist is on


        // disable duty
        plugin.getDutyManager().disableDuty(event.getPlayer());
    }

}
