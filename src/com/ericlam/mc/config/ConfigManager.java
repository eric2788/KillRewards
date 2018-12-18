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
    public static String success, zero, check, notfound, checkother, noperm, unknown, reload, reset, resetplayer, fail, notvalue, zone;
    public static boolean debug;
    public static int interval;
    private FileConfiguration config;
    private FileConfiguration rewards;
    private Plugin plugin;
    private File rewardFile;
    private File configFile;
    private File folder;
    public static String[] help, time;
    private static ConfigManager configManager;
    private FileConfiguration lang;

    private HashMap<Double,Rewards> keyMap = new HashMap<>();
    private HashMap<UUID, Integer> playerUse = new HashMap<>();

    public static ConfigManager getInstance() throws IOException {
        if (configManager == null) configManager = new ConfigManager();
        return configManager;
    }

    private ConfigManager() throws IOException {
        plugin = KillRewards.plugin;

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

        boolean customzone = config.getBoolean("custom-zone");

        if (customzone) zone = config.getString("time-zone");
        else zone = "";

        debug = config.getBoolean("debug-mode");
        time = config.getString("reset-timer").split(":");
        interval = config.getInt("check-interval");

        String prefix = lang.getString("prefix");
        success = ChatColor.translateAlternateColorCodes('&', prefix + lang.getString("success"));
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
        int maxuse = config.getInt("max-uses");
        File[] data = folder.listFiles();
        if (data == null || data.length == 0) return;
        for (File playerdata : data) {
            if (debug) plugin.getLogger().info("正在重設資料 \"" + playerdata.getName() + "\"");
            if (!playerdata.exists()) {
                if (debug) plugin.getLogger().info("文件不存在，已取消重設");
                continue;
            }
            FileConfiguration filedata = YamlConfiguration.loadConfiguration(playerdata);
            filedata.set("max-uses", maxuse);
            filedata.save(playerdata);
            YamlConfiguration.loadConfiguration(playerdata);
            if (debug) plugin.getLogger().info("重設成功");
        }
        playerUse.keySet().forEach(key -> playerUse.put(key, maxuse));
    }

    public void resetData(UUID uuid) throws IOException {
        int maxuse = config.getInt("max-uses");
        File playerdata = new File(folder, uuid.toString() + ".yml");
        if (debug) plugin.getLogger().info("正在重設玩家資料: " + playerdata.getName());
        if (playerdata.exists()) {
            FileConfiguration data;
            data = YamlConfiguration.loadConfiguration(playerdata);
            data.set("max-uses", maxuse);
            data.save(playerdata);
            YamlConfiguration.loadConfiguration(playerdata);
            if (debug) plugin.getLogger().info("重設成功");
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
            if (debug) plugin.getLogger().info("資料設置成功");
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
            if (debug) plugin.getLogger().info("成功把文件資料添加到快取");
        }else{
            int maxuse = config.getInt("max-uses");
            playerUse.put(uuid,maxuse);
            add(playerdata, maxuse);
            if (debug) plugin.getLogger().info("資料不存在，添加一個新的");
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
            if (debug) plugin.getLogger().info("次數添加成功");
        } else {
            int maxuse = config.getInt("max-uses") + add;
            playerUse.put(uuid, maxuse);
            add(playerdata, maxuse);
            if (debug) plugin.getLogger().info("資料不存在，添加一個新的");
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
