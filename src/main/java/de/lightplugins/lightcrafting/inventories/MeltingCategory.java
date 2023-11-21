package de.lightplugins.lightcrafting.inventories;

import de.lightplugins.lightcrafting.main.LightCrafting;
import de.lightplugins.lightcrafting.util.ItemBuilder;
import dev.rollczi.liteskull.api.SkullData;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.pagination.Pagination;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import io.github.rysefoxx.inventory.plugin.pagination.SlotIterator;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
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

public class MeltingCategory implements InventoryProvider {

    public void paginationInventory(Player player) {

        FileConfiguration melting = LightCrafting.melting.getConfig();

        ConfigurationSection settingsSection = melting.getConfigurationSection("settings");

        for (String key : settingsSection.getKeys(false)) {
            if (key.startsWith("categories")) {
                ConfigurationSection categorySection = settingsSection.getConfigurationSection(key);

                // Hier kannst du den restlichen Code für die Kategorien-Verarbeitung einfügen
            }
        }

        RyseInventory.builder()
                .title(LightCrafting.colorTranslation.hexTranslation(melting.getString("settings.guiTitle")))
                .rows(6)
                .disableUpdateTask()
                .provider(new InventoryProvider() {
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

                        int previousPage = pagination.page() - 1;
                        contents.set(5, 3, IntelligentItem.of(new ItemBuilder(Material.ARROW).
                                amount(pagination.isFirst()
                                        ? 1
                                        : pagination.page() - 1)
                                .displayName(pagination.isFirst()
                                        ? "§c§oThis is the first page"
                                        : "§ePage §8⇒ §9" + previousPage).build(), event -> {
                            if (pagination.isFirst()) {
                                player.sendMessage("§c§oYou are already on the first page.");
                                return;
                            }

                            RyseInventory currentInventory = pagination.inventory();
                            currentInventory.open(player, pagination.previous().page());
                        }));

                        CompletableFuture<ItemStack> skullFuture = LightCrafting.skullAPI.getSkull(player.getName());
                        skullFuture.thenAcceptAsync(skullData -> {
                            contents.set(0, 4, skullData);
                        });


                        for(String path : Objects.requireNonNull(melting.getConfigurationSection(
                                "settings.categories")).getKeys(false)) {

                            Material material = Material.STONE;
                            boolean glow = melting.getBoolean("settings.categories." + path + ".glow");
                            //Component displayName = LightCrafting.colorTranslation.deserialize( melting.getString("settings.categories." + path + ".displayname"));
                            String displayName = LightCrafting.colorTranslation.hexTranslation(melting.getString("settings.categories." + path + ".displayname"));


                            String[] splitMaterial =
                                    melting.getString("settings.categories." + path + ".material")
                                            .split(":");


                            if(splitMaterial[0].equalsIgnoreCase("vanilla")) {
                                material = Material.valueOf(splitMaterial[1].toUpperCase());
                            }

                            ItemStack is = new ItemStack(material);
                            ItemMeta im = is.getItemMeta();

                            if(im == null) {
                                throw new RuntimeException("Found a config error on " + path);
                            }

                            im.setDisplayName(displayName);

                            im.getItemFlags().add(ItemFlag.HIDE_ATTRIBUTES);

                            if(glow) {
                                im.addEnchant(Enchantment.THORNS, 1, false);
                                im.getItemFlags().add(ItemFlag.HIDE_ENCHANTS);
                            }

                            List<String> lore = new ArrayList<>();

                            melting.getStringList("settings.categories." + path + ".lore").forEach(singleLine -> {
                                lore.add(LightCrafting.colorTranslation.hexTranslation(singleLine));
                            });

                            if(im.getLore() != null) {
                                im.getLore().clear();
                            }

                            im.setLore(lore);
                            is.setItemMeta(im);

                            pagination.addItem(IntelligentItem.of(is, event -> {
                                event.getWhoClicked().sendMessage("§7Du hast die Kategorie §c" + path + "§7 angeklickt.");
                            }));
                        }

                        int page = pagination.page() + 1;
                        contents.set(5, 5, IntelligentItem.of(new ItemBuilder(Material.ARROW)
                                .amount((pagination.isLast() ? 1 : page))
                                .displayName(!pagination.isLast()
                                        ? "§ePage §8⇒ §9" + page :
                                        "§c§oThis is the last page").build(), event -> {
                            if (pagination.isLast()) {
                                player.sendMessage("§c§oYou are already on the last page.");
                                return;
                            }

                            RyseInventory currentInventory = pagination.inventory();
                            currentInventory.open(player, pagination.next().page());
                        }));


                        //contents.fillEmpty(new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
                    }
                })
                .build(LightCrafting.getInstance).open(player);
    }
}
