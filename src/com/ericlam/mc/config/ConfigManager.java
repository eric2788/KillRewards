package com.ericlam.mc.config;

import com.ericlam.mc.main.KillRewards;
import org.apache.commons.io.FileUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ConfigManager {
    private FileConfiguration config;
    private static ConfigManager configManager;
    private File folder;
    private Plugin plugin;

    private HashMap<Double,Rewards> keyMap = new HashMap<>();
    private HashMap<UUID, Integer> playerUse = new HashMap<>();

    public static ConfigManager getInstance() throws IOException {
        if (configManager == null) configManager = new ConfigManager();
        return configManager;
    }

    private ConfigManager() throws IOException {
        plugin = KillRewards.plugin;

        File rewardFile = new File(plugin.getDataFolder(), "rewards.yml");
        File configFile = new File(plugin.getDataFolder(),"config.yml");
        folder = new File(plugin.getDataFolder(), "PlayerData");

        if (!folder.exists()) FileUtils.forceMkdir(folder);
        if (!rewardFile.exists()) plugin.saveResource("rewards.yml",true);
        if (!configFile.exists()) plugin.saveResource("config.yml",true);


        FileConfiguration rewards = YamlConfiguration.loadConfiguration(rewardFile);
        config = YamlConfiguration.loadConfiguration(configFile);

        for (String key : rewards.getKeys(false)){
            double dkey = Double.parseDouble(key);
            List<String> killercmd = rewards.getStringList(key+".killer");
            List<String> victimcmd = rewards.getStringList(key+".victim");
            String killermsg = rewards.getString(key+".killer-msg");
            String victimmsg = rewards.getString(key+".victim-msg");
            keyMap.put(dkey,new Rewards(killercmd,victimcmd,killermsg,victimmsg));
        }
    }

    public HashMap<Double, Rewards> getKeyMap() {
        return keyMap;
    }

    public void resetData() throws IOException {
        for (UUID uuid : playerUse.keySet()) {
            int maxuse = config.getInt("max-uses");
            File playerdata = new File(folder,uuid.toString()+".yml");
            if (playerdata.exists()){
                FileConfiguration data;
                data = YamlConfiguration.loadConfiguration(playerdata);
                data.set("max-uses",maxuse);
                data.save(playerdata);
            }
            playerUse.put(uuid,maxuse);
        }
    }

    public void reduceUses(UUID uuid) throws IOException {
        int uses = playerUse.get(uuid) - 1;
        playerUse.put(uuid,uses);
        File playerdata = new File(folder,uuid.toString()+".yml");
        if (playerdata.exists()){
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerdata);
            data.set("max-uses",uses);
            data.save(playerdata);
        }
    }

    public boolean isUsed(UUID uuid){
        if (playerUse.get(uuid) < 0) playerUse.put(uuid,0);
        return playerUse.get(uuid) <= 0;
    }

    public void addUses(UUID uuid) throws IOException {
        File playerdata = new File(folder,uuid.toString()+".yml");
        if (playerdata.exists()){
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerdata);
            int uses = data.getInt("max-uses");
            playerUse.put(uuid,uses);
        }else{
            int maxuse = config.getInt("max-uses");
            playerUse.put(uuid,maxuse);
            playerdata.createNewFile();
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerdata);
            data.set("max-uses",maxuse);
            data.save(playerdata);
        }
    }

    public HashMap<UUID, Integer> getPlayerUse() {
        return playerUse;
    }
}
