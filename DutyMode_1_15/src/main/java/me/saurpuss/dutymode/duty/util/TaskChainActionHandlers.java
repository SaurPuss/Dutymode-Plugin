package me.saurpuss.dutymode.duty.util;

import co.aikar.taskchain.TaskChain;
import co.aikar.taskchain.TaskChainAbortAction;
import me.saurpuss.dutymode.DutyMode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class TaskChainActionHandlers {

    // Send player & console a message
    public static final TaskChainAbortAction<Player, String, ?> MESSAGE =
            new TaskChainAbortAction<Player, String, Object>() {
        @Override
        public void onAbort(TaskChain<?> chain, Player player, String message) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
            DutyMode.getInstance().getLogger().log(Level.WARNING,
                    ChatColor.translateAlternateColorCodes('&', message));
        }
    };
}
