package com.ericlam.mc.main;

import com.ericlam.mc.command.KillRewardsCommand;
import com.ericlam.mc.config.ConfigManager;
import com.ericlam.mc.listener.OnDeath;
import com.ericlam.mc.scheduler.ResetTimer;
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
            this.getCommand("killrewards").setExecutor(new KillRewardsCommand());
            new ResetTimer();
            this.getLogger().info("殺敵獎勵已啟用。");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
