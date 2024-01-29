package net.buildtheearth.modules.warp.menu;

import net.buildtheearth.Main;
import net.buildtheearth.modules.network.api.OpenStreetMapAPI;
import net.buildtheearth.modules.utils.*;
import net.buildtheearth.modules.utils.geo.CoordinateConversion;
import net.buildtheearth.modules.utils.menus.AbstractMenu;
import net.buildtheearth.modules.warp.model.Warp;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

public class WarpUpdateMenu extends AbstractMenu {

    public static String WARP_UPDATE_INV_NAME = "Configure the Warp";


    public static int WARP_SLOT = 4;
    public static int LOCATION_SLOT = 19;
    public static int NAME_SLOT = 21;
    public static int GROUP_SLOT = 23;
    public static int HIGHLIGHT_SLOT = 25;
    public static int CONFIRM_SLOT = 35;

    private final Warp warp;
    private final boolean alreadyExists;

    public WarpUpdateMenu(Player player, Warp warp, boolean alreadyExists) {
        super(4, WARP_UPDATE_INV_NAME, player);

        this.warp = warp;
        this.alreadyExists = alreadyExists;

        if(!this.alreadyExists){
            NAME_SLOT = 20;
            GROUP_SLOT = 22;
            HIGHLIGHT_SLOT = 24;
        }
    }

    @Override
    protected void setPreviewItems() {
        super.setPreviewItems();
    }

    @Override
    protected void setMenuItemsAsync() {
        // Set the confirmation item
        getMenu().getSlot(CONFIRM_SLOT).setItem(MenuItems.getCheckmarkItem(alreadyExists ? "§aUpdate" : "§aCreate"));

        // Set the warp item
        ArrayList<String> loreLines = null;

        if(alreadyExists){
            loreLines = new ArrayList<>(Arrays.asList("", "§eAddress:"));
            loreLines.addAll(Arrays.asList(Utils.splitStringByLineLength(warp.getAddress(), 30)));
        }

        getMenu().getSlot(WARP_SLOT).setItem(
                MenuItems.getLetterHead(
                        warp.getName().substring(0, 1),
                        MenuItems.LetterType.STONE,
                        "§6§l" + warp.getName(),
                        loreLines
                )
        );

        // Set the location item if the warp already exists. Otherwise, the location is set automatically on creation.
        if(alreadyExists){
            ArrayList<String> locationLore = ListUtil.createList("", "§eWorld: §7" + warp.getWorldName(), "§eLatitude: §7" + warp.getLat(), "§eLongitude: §7" + warp.getLon(), "§eElevation: §7" + warp.getY());
            getMenu().getSlot(LOCATION_SLOT).setItem(Item.create(Material.COMPASS, "§6§lChange Location", locationLore));

        }

        // Set the name item
        ArrayList<String> nameLore = ListUtil.createList("", "§eCurrent Name: ", warp.getName());
        getMenu().getSlot(NAME_SLOT).setItem(Item.create(Material.NAME_TAG, "§6§lChange Name", nameLore));

        // Set the group item
        ArrayList<String> groupLore = ListUtil.createList("", "§eCurrent Group: ", warp.getWarpGroup().getName());
        getMenu().getSlot(GROUP_SLOT).setItem(MenuItems.getLetterHead(
                warp.getWarpGroup().getName().substring(0, 1),
                MenuItems.LetterType.WOODEN,
                "§6§lChange Warp Group",
                groupLore
        ));


        // Set the highlight item
        ArrayList<String> highlightLore = ListUtil.createList("", "§eIs Highlight: ", "" + warp.isHighlight());
        getMenu().getSlot(HIGHLIGHT_SLOT).setItem(Item.create(Material.NETHER_STAR, warp.isHighlight() ? "§6§lMake Normal" : "§6§lMake Highlight", highlightLore));

    }

    @Override
    protected void setItemClickEventsAsync() {
        // Set click event for the confirmation item
        getMenu().getSlot(CONFIRM_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            if(alreadyExists)
                Main.getBuildTeamTools().getProxyManager().getBuildTeam().updateWarp(clickPlayer, warp);
            else
                Main.getBuildTeamTools().getProxyManager().getBuildTeam().createWarp(clickPlayer, warp);
        });

        // Set click event for the location item
        getMenu().getSlot(LOCATION_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

            // Get the geographic coordinates of the player's location.
            Location location = clickPlayer.getLocation();
            double[] coordinates = CoordinateConversion.convertToGeo(location.getX(), location.getZ());

            //Get the country belonging to the coordinates
            CompletableFuture<String[]> future = OpenStreetMapAPI.getCountryFromLocationAsync(coordinates);

            future.thenAccept(result -> {
                String regionName = result[0];
                String countryCodeCCA2 = result[1].toUpperCase();

                //Check if the team owns this region/country
                boolean ownsRegion = Main.getBuildTeamTools().getProxyManager().ownsRegion(regionName, countryCodeCCA2);

                if(!ownsRegion) {
                    clickPlayer.sendMessage(ChatHelper.error("This team does not own the country %s!", result[0]));
                    return;
                }

                warp.setCountryCode(countryCodeCCA2);
                warp.setWorldName(location.getWorld().getName());
                warp.setY(location.getY());
                warp.setLat(coordinates[0]);
                warp.setLon(coordinates[1]);
                warp.setYaw(location.getYaw());
                warp.setPitch(location.getPitch());

                clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);

                new WarpUpdateMenu(clickPlayer, warp, alreadyExists);
            }).exceptionally(e -> {
                clickPlayer.sendMessage(ChatHelper.error("An error occurred while changing the location of the warp!"));
                e.printStackTrace();
                return null;
            });
        });


        // Set click event for the name item
        getMenu().getSlot(NAME_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new AnvilGUI.Builder()
                    .onClose(player -> {
                        player.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0F, 1.0F);

                        new WarpUpdateMenu(clickPlayer, warp, alreadyExists);
                    })
                    .onClick((slot, stateSnapshot) -> {
                        if (slot != AnvilGUI.Slot.OUTPUT)
                            return Collections.emptyList();

                        stateSnapshot.getPlayer().playSound(clickPlayer.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
                        return Arrays.asList(
                                AnvilGUI.ResponseAction.close(),
                                AnvilGUI.ResponseAction.run(() -> {
                                    warp.setName(stateSnapshot.getText());
                                    new WarpUpdateMenu(clickPlayer, warp, alreadyExists);
                                })
                        );
                    })
                    .text("Name")
                    .itemLeft(Item.create(Material.NAME_TAG, "§6§lChange Name"))
                    .title("§8Change the warp name")
                    .plugin(Main.instance)
                    .open(clickPlayer);
        });

        // Set click event for the group item
        getMenu().getSlot(GROUP_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            new WarpGroupSelectionMenu(clickPlayer, warp.getWarpGroup().getBuildTeam(), warp, alreadyExists);
        });

        // Set click event for the highlight item
        getMenu().getSlot(HIGHLIGHT_SLOT).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.playSound(clickPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0F, 1.0F);

            warp.setHighlight(!warp.isHighlight());
            new WarpUpdateMenu(clickPlayer, warp, alreadyExists);
        });
    }

    @Override
    protected Mask getMask() {
        return BinaryMask.builder(getMenu())
                .item(Item.create(Material.STAINED_GLASS_PANE, " ", (short) 15, null))
                .pattern("000000000")
                .pattern("000000000")
                .pattern("000000000")
                .pattern("111111110")
                .build();
    }
}
