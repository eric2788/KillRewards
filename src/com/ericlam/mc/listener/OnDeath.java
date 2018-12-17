package com.ericlam.mc.listener;

import com.ericlam.mc.config.ConfigManager;
import com.ericlam.mc.config.Rewards;
import com.ericlam.mc.main.KillRewards;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.*;

public class OnDeath implements Listener {
    private Plugin plugin = KillRewards.plugin;
    private HashMap<Double, Rewards> keys;
    private ConfigManager configManager;

    {
        try {
            configManager = ConfigManager.getInstance();
            keys = configManager.getKeyMap();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) throws IOException {
        if (e.getEntity().getKiller() == null) return;
        Player victim = e.getEntity().getPlayer();
        Player killer = e.getEntity().getKiller();
        List<Double> doubles = new ArrayList<>(keys.keySet());
        Collections.sort(doubles);
        boolean selected = false;
        if (configManager.isUsed(killer.getUniqueId())) return;
        while (!selected) {
            double pert = new Random().nextFloat() * 100;
            for (double rand : doubles) {
                if (pert > rand) continue;
                Rewards rewards = keys.get(rand);
                victim.sendMessage(rewards.getVictimmsg().replace("<killer>",killer.getName()).replace("<victim>",victim.getName()));
                killer.sendMessage(rewards.getKillermsg().replace("<killer>",killer.getName()).replace("<victim>",victim.getName()));
                rewards.getKillercmd().forEach(cmd->Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(),cmd.replace("<killer>",killer.getName()).replace("<victim>",victim.getName())));
                rewards.getVictimcmd().forEach(cmd->Bukkit.dispatchCommand(plugin.getServer().getConsoleSender(),cmd.replace("<killer>",killer.getName()).replace("<victim>",victim.getName())));
                selected = true;
                configManager.reduceUses(killer.getUniqueId());
                break;
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) throws IOException {
        UUID puuid = e.getPlayer().getUniqueId();
        configManager.addUses(puuid);
    }
}
