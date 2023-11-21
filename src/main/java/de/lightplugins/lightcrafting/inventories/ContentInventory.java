package de.lightplugins.lightcrafting.inventories;

import de.lightplugins.lightcrafting.main.LightCrafting;
import de.lightplugins.lightcrafting.util.FileManager;
import de.lightplugins.lightcrafting.util.ItemBuilder;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.enums.TimeSetting;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class ContentInventory implements InventoryProvider {



    public void paginationInventory(Player player, String categories, FileConfiguration jobConfig) {


        /**
         *              Inventory Build für den Content pro job
         *
         * @param categories - Welche Kategorien soll angezeigt werden
         * @param jobConfig - Die übergebene Job-config related zu den Kategorien
         * @param player - Der Spieler, der das Inventory öffnet inklusive Job Stats
         *
         */

        final ItemStack[] playerSkull = {new ItemStack(Material.PLAYER_HEAD)};
        CompletableFuture<ItemStack> skullFuture = LightCrafting.skullAPI.getSkull(player.getName());
        skullFuture.thenAcceptAsync(skullData -> {
            playerSkull[0] = skullData;
        });


        RyseInventory.builder()
                .title(LightCrafting.colorTranslation.hexTranslation(jobConfig.getString("settings.guiTitle")))
                .rows(6)
                .period(1, TimeSetting.SECONDS)
                .provider(new InventoryProvider() {
                    @Override
                    public void update(Player player, InventoryContents contents) {
                        contents.update(4, playerSkull[0]);
                        Bukkit.getLogger().log(Level.WARNING, "Timer active Content");
                    }
                    @Override
                    public void init(Player player, InventoryContents contents) {

                        contents.fill(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));

                        Pagination pagination = contents.pagination();
                        pagination.setItemsPerPage(7);
                        pagination.iterator(SlotIterator
                                .builder()
                                .override()
                                .startPosition(2, 1)
                                .type(SlotIterator.SlotIteratorType.HORIZONTAL)
                                .blackList(Arrays.asList(26, 27))
                                .build());

                        contents.set(0, 4, playerSkull[0]);

                        String pageBack = LightCrafting.colorTranslation.hexTranslation("&#ffdc73Zurück");
                        String pageForward = LightCrafting.colorTranslation.hexTranslation("&#ffdc73Vorwärts");

                        /**
                         *      Seite zurück
                         */

                        int previousPage = pagination.page() - 1;
                        contents.set(5, 2, IntelligentItem.of(new ItemBuilder(Material.ARROW).
                                amount(pagination.isFirst()
                                        ? 1
                                        : pagination.page() - 1)
                                .displayName(pagination.isFirst()
                                        ? "§cDas ist die erste Seite"
                                        : pageBack).build(), event -> {
                            if (pagination.isFirst()) {
                                player.sendMessage("§c§oYou are already on the first page.");
                                return;
                            }

                            RyseInventory currentInventory = pagination.inventory();
                            currentInventory.open(player, pagination.previous().page());
                        }));

                        /**
                         *      Seite vorwärts
                         */

                        int page = pagination.page() + 1;
                        contents.set(4, 6, IntelligentItem.of(new ItemBuilder(Material.ARROW)
                                .amount((pagination.isLast() ? 1 : page))
                                .displayName(!pagination.isLast()
                                        ? pageForward :
                                        "§cDas ist die letzte Seite").build(), event -> {
                            if (pagination.isLast()) {
                                player.sendMessage("§c§oDu bist bereits auf der letzten Seite");
                                return;
                            }

                            RyseInventory currentInventory = pagination.inventory();
                            currentInventory.open(player, pagination.next().page());
                        }));



                        /*

                                Liste alle Kategorien in die pages und generiere nach Anzahl
                                der Kategorien die Pages.

                         */


                        for(String path : Objects.requireNonNull(jobConfig.getConfigurationSection(
                                "settings.content")).getKeys(false)) {

                            /*

                               Zeige nur die Items an, die zu der zuvor angeklickten Kategorie zählen.

                                 content:
                                   '0':
                                     category: erze

                             */

                            if(!categories.equalsIgnoreCase(
                                    jobConfig.getString("settings.content." + path + ".category"))) {
                                continue;
                            }

                            Material material = Material.STONE;
                            boolean glow = jobConfig.getBoolean("settings.content." + path + ".glow");


                            String[] splitMaterial =
                                    jobConfig.getString("settings.content." + path + ".material")
                                            .split(":");


                            if(splitMaterial[0].equalsIgnoreCase("vanilla")) {
                                material = Material.valueOf(splitMaterial[1].toUpperCase());
                            }

                            ItemStack is = new ItemStack(material);
                            ItemMeta im = is.getItemMeta();

                            if(im == null) {
                                throw new RuntimeException("Found a config error on " + path);
                            }

                            im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

                            if(glow) {
                                im.addEnchant(Enchantment.THORNS, 1, false);
                                im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                            }

                            List<String> lore = new ArrayList<>();

                            if(im.getLore() != null) {
                                im.getLore().clear();
                            }

                            //im.setLore(lore);
                            is.setItemMeta(im);

                            pagination.addItem(IntelligentItem.of(is, event -> {
                                event.getWhoClicked().sendMessage("§7Du hast die Kategorie §c" + path + "§7 angeklickt.");
                            }));
                        }

                        //contents.fillEmpty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
                    }
                })
                .build(LightCrafting.getInstance).open(player);
    }
    
    
    
}
