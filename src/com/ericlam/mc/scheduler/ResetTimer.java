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

public class ResetTimer {
    private LocalDateTime firstcheck;

    public ResetTimer(){
        Plugin plugin = KillRewards.plugin;
        LocalTime resetTime = LocalTime.MIDNIGHT;
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
                    firstcheck = now;
                }
            }.runTaskTimerAsynchronously(plugin, 0L, 300 * 20L);
        }
    }

}
