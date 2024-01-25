package net.buildtheearth.modules.network.model;

import lombok.Getter;
import net.buildtheearth.modules.warp.WarpGroup;
import net.buildtheearth.modules.warp.model.Warp;

import java.util.ArrayList;
import java.util.List;

public class BuildTeam {

    @Getter
    private final String ID;
    @Getter
    private final String IP;
    @Getter
    private final String name;
    @Getter
    private final String blankName;
    @Getter
    private final String serverName;
    @Getter
    private final boolean isConnected;
    @Getter
    private final boolean hasBTToolsInstalled;
    @Getter
    private final Continent continent;
    @Getter
    private final List<Region> regions;
    @Getter
    private final List<WarpGroup> warpGroups;


    public BuildTeam(String ID, String serverIP, String name, String blankName, String serverName, Continent continent, boolean isConnected, boolean hasBTToolsInstalled) {
        this.ID = ID;
        this.name = name;
        this.blankName = blankName;
        this.serverName = serverName;
        this.continent = continent;
        this.isConnected = isConnected;
        this.hasBTToolsInstalled = hasBTToolsInstalled;

        this.regions = new ArrayList<>();
        this.warpGroups = new ArrayList<>();

        if(!isConnected)
            this.IP = serverIP;
        else
            this.IP = null;
    }
}
