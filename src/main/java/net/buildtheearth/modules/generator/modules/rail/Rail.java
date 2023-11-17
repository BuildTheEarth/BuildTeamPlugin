package net.buildtheearth.modules.generator.modules.rail;

import com.sk89q.worldedit.regions.ConvexPolyhedralRegion;
import com.sk89q.worldedit.regions.Region;
import net.buildtheearth.Main;
import net.buildtheearth.modules.generator.Generator;
import net.buildtheearth.modules.generator.model.GeneratorModule;
import net.buildtheearth.modules.generator.model.GeneratorType;
import org.bukkit.entity.Player;

public class Rail extends GeneratorModule {

    public Rail() {
        super(GeneratorType.RAILWAY);

        wikiPage = "https://github.com/BuildTheEarth/BuildTeamTools/wiki/Road-Command";
    }

    @Override
    public boolean checkPlayer(Player p) {
        if (!Generator.checkIfWorldEditIsInstalled(p))
            return false;

        if (!Generator.checkForWorldEditSelection(p))
            return false;

        if (!Generator.checkForConvexSelection(p))
            return false;

        if (getPlayerSettings().get(p.getUniqueId()).getBlocks() == null)
            getPlayerSettings().get(p.getUniqueId()).setBlocks(Generator.analyzeRegion(p, p.getWorld()));

        return true;
    }

    @Override
    public void generate(Player p) {
        if (!Main.getBuildTeamTools().getGenerator().getRail().checkPlayer(p))
            return;

        Region region = Generator.getWorldEditSelection(p);

        if (region == null || !(region instanceof ConvexPolyhedralRegion))
            return;

        ConvexPolyhedralRegion convexRegion = (ConvexPolyhedralRegion) region;

        RailScripts.railscript_v_1_3(p, this, convexRegion);

        sendSuccessMessage(p);
    }
}