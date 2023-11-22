package de.lightplugins.lightcrafting.main;

import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.lightcrafting.database.tables.PlayerData;
import de.lightplugins.lightcrafting.events.GainExp;
import de.lightplugins.lightcrafting.util.ColorTranslation;
import de.lightplugins.lightcrafting.util.FileManager;
import de.lightplugins.lightcrafting.util.Util;
import dev.rollczi.liteskull.LiteSkullFactory;
import dev.rollczi.liteskull.api.SkullAPI;
import io.github.rysefoxx.inventory.plugin.pagination.InventoryManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Duration;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

public final class LightCrafting extends JavaPlugin {

    public static LightCrafting getInstance;
    public static final String consolePrefix = "§r[light§cCrafting§r] ";
    public static HashMap<UUID, PlayerData> level = new HashMap<>();
    public HikariDataSource ds;
    public static ColorTranslation colorTranslation;
    public static SkullAPI skullAPI;
    public static Util util;
    private static Economy econ = null;

    public static FileManager settings;
    public static FileManager messages;

    /**
     *  all jobs config files in subfolder "jobs"
     */

    public static FileManager armorsmith;
    public static FileManager blacksmith;
    public static FileManager carpentry;
    public static FileManager cooking;
    public static FileManager elementsmith;
    public static FileManager loom;
    public static FileManager melting;

    /**
     *  All Inventories Providers
     */

    public final InventoryManager inventoryManager = new InventoryManager(this);


    @Override
    public void onLoad() {
        getInstance = this;

        settings = new FileManager(this, "master", "settings.yml");
        messages = new FileManager(this, "master", "messages.yml");

        armorsmith = new FileManager(this, "jobs", "armorsmith.yml");
        blacksmith = new FileManager(this, "jobs", "blacksmith.yml");
        carpentry = new FileManager(this, "jobs", "carpentry.yml");
        cooking = new FileManager(this, "jobs", "cooking.yml");
        elementsmith = new FileManager(this, "jobs", "elementsmith.yml");
        loom = new FileManager(this, "jobs", "loom.yml");
        melting = new FileManager(this, "jobs", "melting.yml");

        colorTranslation = new ColorTranslation();
        util = new Util();

        String test = melting.getConfig().saveToString();

        /**
         * SkullData
         */

        skullAPI = LiteSkullFactory.builder()
                .cacheExpireAfterWrite(Duration.ofMinutes(45L))
                .bukkitScheduler(this)
                .build();

    }

    @Override
    public void onEnable() {

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new GainExp(), this);

        if (!setupEconomy() ) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        inventoryManager.invoke();

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static Economy getEconomy() {
        return econ;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }
}
