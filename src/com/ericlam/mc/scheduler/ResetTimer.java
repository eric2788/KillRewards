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

public class ResetTimer {
    private LocalDateTime firstcheck;

    public ResetTimer(){
        Plugin plugin = KillRewards.plugin;
        LocalTime resetTime = LocalTime.NOON;
        synchronized (this){
            new BukkitRunnable(){
                @Override
                public void run() {
                    LocalDateTime now = LocalDateTime.now();
                    if (firstcheck == null) firstcheck = now.plusMinutes(1);
                    else{

                        long first = Timestamp.valueOf(firstcheck).getTime();
                        long second = Timestamp.valueOf(now).getTime();
                        long reset = Timestamp.valueOf(LocalDateTime.of(LocalDate.now(),resetTime)).getTime();

                        if (reset >= first && reset <= second) {
                            try {
                                ConfigManager.getInstance().resetData();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }.runTaskTimerAsynchronously(plugin,0L,300L);
        }
    }

}
