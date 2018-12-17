package com.ericlam.mc.config;

import java.util.List;

public class Rewards {
    private List<String> killercmd;
    private List<String> victimcmd;
    private String killermsg;
    private String victimmsg;
    private boolean killerMSG = true;
    private boolean victimMSG = true;

    Rewards(List<String> killercmd, List<String> victimcmd, String killermsg, String victimmsg) {
        this.killercmd = killercmd;
        this.victimcmd = victimcmd;
        this.killermsg = killermsg;
        if (killermsg.isEmpty()) this.killerMSG = false;
        this.victimmsg = victimmsg;
        if (victimmsg.isEmpty()) this.victimMSG = false;
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

    public boolean isKillerMSG() {
        return killerMSG;
    }

    public boolean isVictimMSG() {
        return victimMSG;
    }
}
