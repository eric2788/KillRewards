package com.ericlam.mc.command;

import com.ericlam.mc.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class KillRewardsCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {

        if (strings.length == 0) {
            commandSender.sendMessage(ConfigManager.help);
            return false;
        }

        ConfigManager cm;

        try {
            cm = ConfigManager.getInstance();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        if (strings.length == 1) {
            switch (strings[0]) {
                case "check":
                    if (!(commandSender instanceof Player)) {
                        commandSender.sendMessage("you are not player!");
                    } else {
                        Player player = (Player) commandSender;
                        player.sendMessage(ConfigManager.check.replace("<count>", cm.checkUses(player.getUniqueId()) + ""));
                    }
                    break;
                case "reload":
                    if (!commandSender.hasPermission("kw.admin")) {
                        commandSender.sendMessage(ConfigManager.noperm);
                        return false;
                    }
                    cm.reloadData();
                    commandSender.sendMessage(ConfigManager.reload);
                    break;
                default:
                    commandSender.sendMessage(ConfigManager.unknown);
                    break;
            }
            return true;
        }

        if (!commandSender.hasPermission("kw.admin")) {
            commandSender.sendMessage(ConfigManager.noperm);
            return false;
        }

        Player target = Bukkit.getPlayer(strings[1]);

        if (target == null || !target.isOnline()) {
            commandSender.sendMessage(ConfigManager.notfound);
            return false;
        }

        if (strings.length == 2) {
            switch (strings[0]) {
                case "check":
                    commandSender.sendMessage(ConfigManager.checkother.replace("<player>", target.getName()).replace("<count>", cm.checkUses(target.getUniqueId()) + ""));
                    break;
                case "add":
                    try {
                        cm.addUses(target.getUniqueId(), 1);
                        commandSender.sendMessage(ConfigManager.sucess);
                    } catch (IOException e) {
                        e.printStackTrace();
                        commandSender.sendMessage(ConfigManager.fail);
                        return false;
                    }
                    break;
                case "remove":
                    if (cm.isUsed(target.getUniqueId())) {
                        commandSender.sendMessage(ConfigManager.zero);
                        return false;
                    }
                    try {
                        cm.reduceUses(target.getUniqueId(), 1);
                        commandSender.sendMessage(ConfigManager.sucess);
                    } catch (IOException e) {
                        e.printStackTrace();
                        commandSender.sendMessage(ConfigManager.fail);
                        return false;
                    }
                    break;
                case "reset":
                    try {
                        cm.resetData(target.getUniqueId());
                        commandSender.sendMessage(ConfigManager.sucess);
                    } catch (IOException e) {
                        e.printStackTrace();
                        commandSender.sendMessage(ConfigManager.fail);
                        return false;
                    }
                    break;
                default:
                    commandSender.sendMessage(ConfigManager.unknown);
                    break;
            }
            return true;
        }

        try {
            Integer.parseInt(strings[2]);
        } catch (NumberFormatException e) {
            commandSender.sendMessage(ConfigManager.notvalue);
            return false;
        }

        int count = Integer.parseInt(strings[2]);

        switch (strings[0]) {
            case "add":
                try {
                    cm.addUses(target.getUniqueId(), count);
                    commandSender.sendMessage(ConfigManager.sucess);
                } catch (IOException e) {
                    e.printStackTrace();
                    commandSender.sendMessage(ConfigManager.fail);
                    return false;
                }
                break;
            case "remove":
                if (cm.isUsed(target.getUniqueId())) {
                    commandSender.sendMessage(ConfigManager.zero);
                    return false;
                }
                try {
                    cm.reduceUses(target.getUniqueId(), count);
                    commandSender.sendMessage(ConfigManager.sucess);
                } catch (IOException e) {
                    e.printStackTrace();
                    commandSender.sendMessage(ConfigManager.fail);
                    return false;
                }
                break;
            case "set":
                try {
                    cm.setUses(target.getUniqueId(), count);
                    commandSender.sendMessage(ConfigManager.sucess);
                } catch (IOException e) {
                    e.printStackTrace();
                    commandSender.sendMessage(ConfigManager.fail);
                    return false;
                }
                break;
            default:
                commandSender.sendMessage(ConfigManager.unknown);
                break;
        }

        return false;
    }
}
