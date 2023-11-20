package de.lightplugins.lightcrafting.inventories;

import de.lightplugins.lightcrafting.main.LightCrafting;
import de.lightplugins.lightcrafting.util.ItemBuilder;
import io.github.rysefoxx.inventory.plugin.animator.*;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItem;
import io.github.rysefoxx.inventory.plugin.content.IntelligentItemColor;
import io.github.rysefoxx.inventory.plugin.content.InventoryContents;
import io.github.rysefoxx.inventory.plugin.content.InventoryProvider;
import io.github.rysefoxx.inventory.plugin.enums.AnimatorDirection;
import io.github.rysefoxx.inventory.plugin.enums.IntelligentItemAnimatorType;
import io.github.rysefoxx.inventory.plugin.enums.TimeSetting;
import io.github.rysefoxx.inventory.plugin.pagination.RyseInventory;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MeltingCategory implements InventoryProvider {


    public static final FileConfiguration melting = LightCrafting.melting.getConfig();

    public void itemNameAnimation(Player player) {
        RyseInventory.builder()
                .title("Item-Name Animation")
                .rows(6)
                .disableUpdateTask()
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        IntelligentItem item = IntelligentItem.empty(new ItemStack(Material.STONE));
                        contents.set(5, item);

                        IntelligentItemNameAnimator itemNameAnimator = IntelligentItemNameAnimator.builder(LightCrafting.getInstance)
                                .loop()
                                .item(item)
                                .slot(5)
                                .delay(1, TimeSetting.SECONDS)
                                .period(3, TimeSetting.MILLISECONDS)
                                .type(IntelligentItemAnimatorType.WORD_BY_WORD)
                                .colors(Arrays.asList('A', 'B', 'C', 'D'),
                                        IntelligentItemColor.builder().paragraph("§a").bold().build(),
                                        IntelligentItemColor.builder().rgbColor(23, 53, 234).bold().build(),
                                        IntelligentItemColor.builder().bukkitColor(ChatColor.GRAY).build(),
                                        IntelligentItemColor.builder().hexColor("#126b58").underline().build())
                                .frames("ABCD")
                                .build(contents);
                        itemNameAnimator.animate();
                    }
                })
                .build(LightCrafting.getInstance).open(player);
    }

    public void loreAnimaton(Player player) {

        RyseInventory.builder()
                .title("Lore Animation")
                .rows(6)
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents) {
                        IntelligentItem item = IntelligentItem.empty(new ItemBuilder(Material.STONE).lore("This is a lore", "This is a lore 2").build());
                        contents.set(5, item);

                        IntelligentItemLoreAnimator loreAnimator = IntelligentItemLoreAnimator.builder(LightCrafting.getInstance)
                                .loop()
                                .item(item)
                                .slot(5)
                                .delay(1, TimeSetting.SECONDS)
                                .period(3, TimeSetting.MILLISECONDS)
                                .type(IntelligentItemAnimatorType.WORD_BY_WORD)
                                .colors(Arrays.asList('A', 'B', 'C', 'D'),
                                        IntelligentItemColor.builder().paragraph("§9").bold().build(),
                                        IntelligentItemColor.builder().rgbColor(250, 1, 52).bold().build(),
                                        IntelligentItemColor.builder().bukkitColor(ChatColor.DARK_GRAY).build(),
                                        IntelligentItemColor.builder().hexColor("#e610c9").underline().build()
                                )
                                .lore(0, "ABCD")
                                .lore(1, "DCBA")
                                .build(contents);
                        loreAnimator.animate();
                    }
                })
                .build(LightCrafting.getInstance).open(player);
    }

    public void slideAnimation(Player player) {
        RyseInventory.builder()
                .title("Slide Animation")
                .rows(6)
                .disableUpdateTask()
                .animation(SlideAnimation.builder(LightCrafting.getInstance)
                        .from(3)
                        .to(30)
                        .items(IntelligentItem.empty(new ItemStack(Material.STONE)))
                        .delay(1, TimeSetting.SECONDS)
                        .period(11, TimeSetting.MILLISECONDS)
                        .blockClickEvent()
                        .direction(AnimatorDirection.VERTICAL_UP_DOWN)
                        .build())
                .provider(new InventoryProvider() {
                    @Override
                    public void init(Player player, InventoryContents contents, SlideAnimation animation) {
                        IntelligentItem item = IntelligentItem.empty(new ItemStack(Material.OAK_BOAT));
                        contents.set(0, item);

                        animation.animate(contents);
                    }
                })
                .build(LightCrafting.getInstance).open(player);
    }
}
