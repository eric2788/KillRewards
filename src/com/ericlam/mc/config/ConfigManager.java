package com.ericlam.mc.config;

import com.ericlam.mc.main.KillRewards;
import org.apache.commons.io.FileUtils;
import org.bukkit.ChatColor;
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
    private FileConfiguration rewards;
    public static String sucess, zero, check, notfound, checkother, noperm, unknown, reload, reset, resetplayer, fail, notvalue;
    private File rewardFile;
    private File configFile;
    private File folder;
    public static String[] help;
    private static ConfigManager configManager;
    private FileConfiguration lang;

    private HashMap<Double,Rewards> keyMap = new HashMap<>();
    private HashMap<UUID, Integer> playerUse = new HashMap<>();

    public static ConfigManager getInstance() throws IOException {
        if (configManager == null) configManager = new ConfigManager();
        return configManager;
    }

    private ConfigManager() throws IOException {
        Plugin plugin = KillRewards.plugin;

        rewardFile = new File(plugin.getDataFolder(), "rewards.yml");
        configFile = new File(plugin.getDataFolder(),"config.yml");
        folder = new File(plugin.getDataFolder(), "PlayerData");
        File langFile = new File(plugin.getDataFolder(), "lang.yml");

        if (!folder.exists()) FileUtils.forceMkdir(folder);
        if (!rewardFile.exists()) plugin.saveResource("rewards.yml",true);
        if (!configFile.exists()) plugin.saveResource("config.yml",true);
        if (!langFile.exists()) plugin.saveResource("lang.yml", true);

        rewards = YamlConfiguration.loadConfiguration(rewardFile);
        config = YamlConfiguration.loadConfiguration(configFile);
        lang = YamlConfiguration.loadConfiguration(langFile);

        for (String key : rewards.getKeys(false)){
            double dkey = Double.parseDouble(key.replace("X","."));
            List<String> killercmd = rewards.getStringList(key+".killer");
            List<String> victimcmd = rewards.getStringList(key+".victim");
            String killermsg = rewards.getString(key+".killer-msg");
            String victimmsg = rewards.getString(key+".victim-msg");
            keyMap.put(dkey,new Rewards(killercmd,victimcmd,killermsg,victimmsg));
        }
        String prefix = lang.getString("prefix");
        sucess = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("success"));
        zero = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("zero"));
        check = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("check"));
        help = lang.getStringList("help").stream().map(text -> ChatColor.translateAlternateColorCodes('&', prefix + text)).toArray(String[]::new);
        notfound = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("not-found"));
        checkother = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("check-other"));
        unknown = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("unknown-cmd"));
        noperm = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("no-perm"));
        reload = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("reloaded"));
        reset = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("reset"));
        resetplayer = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("reset-player"));
        fail = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("fail"));
        notvalue = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("not-value"));
    }

    public HashMap<Double, Rewards> getKeyMap() {
        return keyMap;
    }

    public void resetData() throws IOException {
        for (UUID uuid : playerUse.keySet()) {
            reset(uuid);
        }
    }

    public void resetData(UUID uuid) throws IOException {
        reset(uuid);
    }

    private void reset(UUID uuid) throws IOException {
        int maxuse = config.getInt("max-uses");
        File playerdata = new File(folder, uuid.toString() + ".yml");
        if (playerdata.exists()) {
            FileConfiguration data;
            data = YamlConfiguration.loadConfiguration(playerdata);
            data.set("max-uses", maxuse);
            data.save(playerdata);
            YamlConfiguration.loadConfiguration(playerdata);
        }
        playerUse.put(uuid, maxuse);
    }

    public int checkUses(UUID uuid) {
        if (playerUse.get(uuid) < 0) playerUse.put(uuid, 0);
        return playerUse.get(uuid);
    }

    public void reduceUses(UUID uuid) throws IOException {
        int uses = playerUse.get(uuid) - 1;
        if (uses < 0) uses = 0;
        setUses(uuid, uses);
    }

    public void reduceUses(UUID uuid, int reduce) throws IOException {
        int uses = playerUse.get(uuid) - reduce;
        if (uses < 0) uses = 0;
        setUses(uuid, uses);
    }

    public void setUses(UUID uuid, int uses) throws IOException {
        playerUse.put(uuid, uses);
        File playerdata = new File(folder, uuid.toString() + ".yml");
        if (playerdata.exists()) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerdata);
            data.set("max-uses", uses);
            data.save(playerdata);
            YamlConfiguration.loadConfiguration(playerdata);
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
            add(playerdata, maxuse);
        }
    }

    private void add(File playerdata, int maxuse) throws IOException {
        playerdata.createNewFile();
        FileConfiguration data = YamlConfiguration.loadConfiguration(playerdata);
        data.set("max-uses", maxuse);
        data.save(playerdata);
        YamlConfiguration.loadConfiguration(playerdata);
    }

    public void addUses(UUID uuid, int add) throws IOException {
        File playerdata = new File(folder, uuid.toString() + ".yml");
        if (playerdata.exists()) {
            FileConfiguration data = YamlConfiguration.loadConfiguration(playerdata);
            int uses = data.getInt("max-uses") + add;
            data.set("max-uses", uses);
            data.save(playerdata);
            YamlConfiguration.loadConfiguration(playerdata);
            playerUse.put(uuid, uses);
        } else {
            int maxuse = config.getInt("max-uses") + add;
            playerUse.put(uuid, maxuse);
            add(playerdata, maxuse);
        }
    }

    public void reloadData(){
        rewards = YamlConfiguration.loadConfiguration(rewardFile);
        config = YamlConfiguration.loadConfiguration(configFile);
        keyMap.clear();
        for (String key : rewards.getKeys(false)){
            double dkey = Double.parseDouble(key.replace("X","."));
            List<String> killercmd = rewards.getStringList(key+".killer");
            List<String> victimcmd = rewards.getStringList(key+".victim");
            String killermsg = rewards.getString(key+".killer-msg");
            String victimmsg = rewards.getString(key+".victim-msg");
            keyMap.put(dkey,new Rewards(killercmd,victimcmd,killermsg,victimmsg));
        }
    }
}
