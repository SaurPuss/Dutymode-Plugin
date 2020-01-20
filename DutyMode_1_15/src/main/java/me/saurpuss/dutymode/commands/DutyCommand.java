package me.saurpuss.dutymode.commands;

import me.saurpuss.dutymode.DutyMode;
import me.saurpuss.dutymode.duty.util.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DutyCommand implements CommandExecutor {

    private List<SubCommand> commands = new ArrayList<>();

    public DutyCommand(DutyMode plugin) {
        commands.add(new DutyToggle(plugin));
        commands.add(new Reload(plugin));
        commands.add(new DutyRestore(plugin));
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 && (sender instanceof ConsoleCommandSender)) {
            // TODO check and rework entire command if needed
            sender.sendMessage(ChatColor.DARK_RED + "Only players can use this command!");
            return false;
        }

        if (args[0].equalsIgnoreCase("?") || args[0].equalsIgnoreCase("help")) {
            if (args.length == 1) {
                // TODO split into pages
                commands.forEach(subCommand -> {
                    for (String string : subCommand.getUsage())
                        sender.sendMessage(string);
                });

                return true;
            }

            SubCommand sub = getSubCommand(args[1]);
            if (sub != null) {
                // TODO split into pages
                for (String string : sub.getUsage())
                    sender.sendMessage(string);
            }
            return true;
        }

        ArrayList<String> list;
        Player player;
        if (sender instanceof Player) {
            list = new ArrayList<>(Arrays.asList(args));

            // Determine player to perform this on
            player = Bukkit.getPlayer(list.get(0));
            if (player == null)
                player = (Player) sender; // first arg is not a player
            else
                list.remove(0); // remove player arg
        } else {
            if (args[0].equalsIgnoreCase("reload")) {
                SubCommand sub = getSubCommand("reload");
                return sub.onCommand(null, args);
            }

            player = Bukkit.getPlayer(args[0]);
            if (player == null) {
                sender.sendMessage(ChatColor.DARK_RED + args[0] + " is not a valid player!");
                return true;
            }

            list = new ArrayList<>(Arrays.asList(args));
            list.remove(0); // remove player arg
        }

        // Get SubCommand
        SubCommand target = getSubCommand(list.get(0));
        if (target == null)
            return false;
        list.remove(0); // remove subcommand arg

        return target.onCommand(player, (String[]) list.toArray());
    }

    private SubCommand getSubCommand(String name) {
        for (SubCommand subCommand : commands) {
            if (subCommand.name().equalsIgnoreCase(name))
                return subCommand;

            String[] aliases;
            int length = (aliases = subCommand.aliases()).length;
            for (int i = 0; i < length; ++i) {
                if (name.equalsIgnoreCase(aliases[i]))
                    return subCommand;
            }
        }

        return null;
    }
}
