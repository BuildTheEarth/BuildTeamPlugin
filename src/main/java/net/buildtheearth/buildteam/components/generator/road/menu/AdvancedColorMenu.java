package net.buildtheearth.buildteam.components.generator.road.menu;

import net.buildtheearth.Main;
import net.buildtheearth.buildteam.components.generator.Settings;
import net.buildtheearth.buildteam.components.generator.road.RoadFlag;
import net.buildtheearth.buildteam.components.generator.road.RoadSettings;
import net.buildtheearth.utils.Item;
import net.buildtheearth.utils.menus.BlockListMenu;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AdvancedColorMenu extends BlockListMenu {

    private final RoadFlag roadFlag;

    public AdvancedColorMenu(Player player, RoadFlag roadFlag, String invName, List<ItemStack> items, boolean autoLoad) {
        super(player, invName, items, new AdvancedSettingsMenu(player, false), autoLoad);

        this.roadFlag = roadFlag;
    }

    @Override
    protected void setItemClickEventsAsync() {
        super.setItemClickEventsAsync();

        // Set click event for next item
        if(canProceed())
            getMenu().getSlot(NEXT_ITEM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
                Settings settings = Main.buildTeamTools.getGenerator().getRoad().getPlayerSettings().get(clickPlayer.getUniqueId());

                if(!(settings instanceof RoadSettings))
                    return;

                RoadSettings roadSettings = (RoadSettings) settings;
                roadSettings.setValue(roadFlag, Item.createStringFromItemList(selectedMaterials));

                clickPlayer.closeInventory();
                clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

                new AdvancedSettingsMenu(clickPlayer, true);
            });
    }
}
