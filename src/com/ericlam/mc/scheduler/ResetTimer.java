package com.ericlam.mc.scheduler;

import com.ericlam.mc.config.ConfigManager;
import com.ericlam.mc.main.KillRewards;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class ResetTimer {
    private LocalDateTime firstcheck;

    public ResetTimer(){
        Plugin plugin = KillRewards.plugin;
        LocalTime resetTime = ConfigManager.time == null ? LocalTime.MIDNIGHT : LocalTime.of(Integer.parseInt(ConfigManager.time[0]), Integer.parseInt(ConfigManager.time[1]));
        ZoneId zone = ConfigManager.zone.isEmpty() ? ZoneId.systemDefault() : ZoneId.of(ConfigManager.zone);
        synchronized (this){
            new BukkitRunnable(){
                @Override
                public void run() {
                    LocalDateTime now = LocalDateTime.now(zone);
                    if (firstcheck != null) {

                        long first = Timestamp.valueOf(firstcheck).getTime();
                        long second = Timestamp.valueOf(now).getTime();
                        long reset = Timestamp.valueOf(LocalDateTime.of(LocalDate.now(zone), resetTime)).getTime();

                        if (reset >= first && reset <= second) {
                            try {
                                ConfigManager.getInstance().resetData();
                                plugin.getLogger().info("已重置所有玩家資料");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    if (ConfigManager.debug) {
                        plugin.getLogger().info("DEBUG: 第一檢測時間截點: " + (firstcheck == null ? now.truncatedTo(ChronoUnit.MINUTES) : firstcheck.truncatedTo(ChronoUnit.MINUTES)));
                        plugin.getLogger().info("DEBUG: 第二檢測時間節點: " + now.truncatedTo(ChronoUnit.MINUTES));
                        plugin.getLogger().info("DEBUG: 重設檢測時間節點: " + resetTime.truncatedTo(ChronoUnit.MINUTES));
                    }

                    firstcheck = now;
                }
            }.runTaskTimerAsynchronously(plugin, 0L, ConfigManager.interval * 20L);
        }
    }

}
