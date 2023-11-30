package de.lightplugins.lightcrafting.inventories;

import de.lightplugins.lightcrafting.main.LightCrafting;
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
                                player.sendMessage("§c§oDu bist bereits auf der ersten Seite");
                                LightCrafting.util.playSuccessSound(player, false);
                                return;
                            }

                            RyseInventory currentInventory = pagination.inventory();
                            currentInventory.open(player, pagination.previous().page());
                            LightCrafting.util.playSuccessSound(player, true);
                        }));

                        /**
                         *      Seite vorwärts
                         */

                        int page = pagination.page() + 1;
                        contents.set(5, 6, IntelligentItem.of(new ItemBuilder(Material.ARROW)
                                .amount((pagination.isLast() ? 1 : page))
                                .displayName(!pagination.isLast()
                                        ? pageForward :
                                        "§cDas ist die letzte Seite").build(), event -> {
                            if (pagination.isLast()) {
                                player.sendMessage("§c§oDu bist bereits auf der letzten Seite");
                                LightCrafting.util.playSuccessSound(player, false);
                                return;
                            }

                            RyseInventory currentInventory = pagination.inventory();
                            currentInventory.open(player, pagination.next().page());
                            LightCrafting.util.playSuccessSound(player, true);
                        }));



                        /*

                               Zeige nur die Items an, die zu der zuvor angeklickten Kategorie zählen.

                                 content:
                                   '0':
                                     category: erze

                             */


                        for(String path : Objects.requireNonNull(jobConfig.getConfigurationSection(
                                "settings.content")).getKeys(false)) {



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

                            is.setItemMeta(im);

                            /*
                                    Hier beginnen die Abfragen zum Herstellen
                             */

                            pagination.addItem(IntelligentItem.of(is, event -> {

                                List<ItemStack> requiredItems = getRequiredMaterials(jobConfig, path);
                                List<ItemStack> rewardItems = new ArrayList<>();

                                jobConfig.getStringList("settings.content." + path + ".reward.items")
                                        .forEach(singleRewardItem -> {

                                    String[] itemMaterial = singleRewardItem.split(":");

                                    if(itemMaterial[0].equalsIgnoreCase("vanilla")) {

                                        String[] amountParam = itemMaterial[1].split(" ");

                                        ItemStack finalItemStack = new ItemStack(
                                                Material.valueOf(amountParam[0].toUpperCase()), Integer.parseInt(amountParam[1]));

                                        rewardItems.add(finalItemStack);
                                    }
                                });

                                if(!hasMoney(player, jobConfig, path)) {
                                    LightCrafting.util.sendMessage(player,
                                            "&cDu hast nicht das nötige &4Börgergeld&7!");
                                    LightCrafting.util.playSuccessSound(player, false);
                                    return;
                                }

                                if(!checksum(player, requiredItems)) {
                                    LightCrafting.util.sendMessage(player,
                                            "&cDir fehlen für das Herstellen Resourcen&7!");
                                    LightCrafting.util.playSuccessSound(player, false);
                                    return;
                                }

                                if(!hasLevel(player, 5, path)) {
                                    LightCrafting.util.sendMessage(player,
                                            "&cDein Level ist für diese Herstellung zu niedrig&7!");
                                    LightCrafting.util.playSuccessSound(player, false);
                                    return;
                                }

                                removeRequiredItems(player, requiredItems);


                                rewardItems.forEach(singleRewardItem -> {
                                    if(LightCrafting.util.isInventoryEmpty(player)) {
                                        player.getInventory().addItem(singleRewardItem);
                                    } else {
                                        player.getWorld().dropItem(player.getLocation(), singleRewardItem);
                                    }

                                    LightCrafting.util.playSuccessSound(player, true);
                                });





                                event.getWhoClicked().sendMessage("§7Du hast die Kategorie §c" + path + "§7 angeklickt.");
                            }));
                        }

                        //contents.fillEmpty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
                    }
                })
                .build(LightCrafting.getInstance).open(player);
    }


    private Boolean checksum(Player player, List<ItemStack> itemStacks) {

        List<ItemStack> checksum = new ArrayList<>();
        itemStacks.forEach(singleItem -> {
            for (ItemStack playerSingleItem : player.getInventory().getContents()) {

                if(playerSingleItem != null && playerSingleItem.getItemMeta() != null) {

                    if(amountOfItem(playerSingleItem) >= amountOfItem(singleItem)) {
                        if(playerSingleItem.getType().equals(singleItem.getType())) {
                            checksum.add(singleItem);
                            break;
                        }
                    }
                }
            }
        });

        Bukkit.getLogger().log(Level.WARNING, "itemStacks " + itemStacks);
        Bukkit.getLogger().log(Level.WARNING, "checksum   " + checksum);

        return itemStacks.equals(checksum);
    }

    private int amountOfItem(ItemStack itemStack) {
        return itemStack.getAmount();
    }

    private Boolean hasLevel(Player player, int requiredLevel, String jobID) {


        return true;
    }

    private Boolean hasMoney(Player player, FileConfiguration jobConfig, String itemID) {
        double neededMoney = jobConfig.getDouble("settings.content." + itemID + ".required.money");
        double currentMoney = LightCrafting.getEconomy().getBalance(player);
        return currentMoney >= neededMoney;
    }

    private Double getMoney(Player player) {
        return LightCrafting.getEconomy().getBalance(player);
    }

    private List<ItemStack> getRequiredMaterials(FileConfiguration jobConfig, String itemID) {

        List<ItemStack> requiredItems = new ArrayList<>();

        jobConfig.getStringList("settings.content." + itemID + ".required.items").forEach(singleItem -> {

            String[] splitMaterial = singleItem.split(":");

            if(splitMaterial[0].equalsIgnoreCase("vanilla")) {
                String[] splitParams = splitMaterial[1].split(" ");
                ItemStack is = new ItemStack(Material.valueOf(splitParams[0].toUpperCase()), Integer.parseInt(splitParams[1]));
                requiredItems.add(is);

            }
        });

        return requiredItems;

    }

    private void removeRequiredItems(Player player, List<ItemStack> removeItems) {
        removeItems.forEach(removeItem -> {
            int remainingAmount = removeItem.getAmount();

            for (ItemStack singleItem : player.getInventory().getContents()) {
                if (singleItem == null || !singleItem.getType().equals(removeItem.getType())) {
                    continue;
                }

                if (singleItem.getAmount() >= remainingAmount) {
                    // Genug Gegenstände im aktuellen Stapel
                    singleItem.setAmount(singleItem.getAmount() - remainingAmount);
                    remainingAmount = 0; // Keine Menge mehr übrig
                    break; // Verlasse die Schleife, da die erforderliche Menge erreicht ist
                } else {
                    // Nicht genug Gegenstände im aktuellen Stapel, entferne diesen Stapel
                    remainingAmount -= singleItem.getAmount();
                    player.getInventory().remove(singleItem);
                }

                if (remainingAmount <= 0) {
                    // Die erforderliche Menge wurde erreicht
                    break;
                }
            }
        });
    }
}
