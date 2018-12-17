package com.ericlam.mc.main;

import com.ericlam.mc.config.ConfigManager;
import com.ericlam.mc.listener.OnDeath;
import com.ericlam.mc.scheduler.ResetTimer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class KillRewards extends JavaPlugin {
    public static Plugin plugin;
    @Override
    public void onEnable() {
    plugin = this;
        try {
            ConfigManager.getInstance();
            this.getServer().getPluginManager().registerEvents(new OnDeath(),this);
            new ResetTimer();
            this.getLogger().info("殺敵獎勵已啟用。");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("resetdata")){
            if (!sender.hasPermission("group.admin")){
                sender.sendMessage("§c你沒有權限");
                return false;
            }
            try {
                ConfigManager.getInstance().resetData();
                sender.sendMessage("§a清除成功");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        if (command.getName().equalsIgnoreCase("reloaddata")){
            if (!sender.hasPermission("group.admin")){
                sender.sendMessage("§c你沒有權限");
                return false;
            }
            try {
                ConfigManager.getInstance().reloadData();
                sender.sendMessage("§a重載成功");
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return true;
    }
}
