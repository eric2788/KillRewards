package com.ericlam.mc.config;

import java.util.List;

public class Rewards {
    private List<String> killercmd;
    private List<String> victimcmd;
    private String killermsg;
    private String victimmsg;

    Rewards(List<String> killercmd, List<String> victimcmd, String killermsg, String victimmsg) {
        this.killercmd = killercmd;
        this.victimcmd = victimcmd;
        this.killermsg = killermsg;
        this.victimmsg = victimmsg;
    }

    public List<String> getKillercmd() {
        return killercmd;
    }

    public List<String> getVictimcmd() {
        return victimcmd;
    }

    public String getKillermsg() {
        return killermsg;
    }

    public String getVictimmsg() {
        return victimmsg;
    }
}
