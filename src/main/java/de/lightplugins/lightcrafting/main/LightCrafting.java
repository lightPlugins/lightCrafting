package de.lightplugins.lightcrafting.main;

import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.lightcrafting.database.tables.PlayerData;
import de.lightplugins.lightcrafting.events.GainExp;
import de.lightplugins.lightcrafting.util.FileManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;

public final class LightCrafting extends JavaPlugin {

    public static LightCrafting getInstance;
    public static final String consolePrefix = "§r[light§cCrafting§r] ";
    public static HashMap<UUID, PlayerData> level = new HashMap<>();
    public HikariDataSource ds;

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

    @Override
    public void onLoad() {
        getInstance = this;

        settings = new FileManager(this, null, "settings.yml");
        messages = new FileManager(this, null, "messages.yml");

        armorsmith = new FileManager(this, "jobs", "jobs/armorsmith.yml");
        blacksmith = new FileManager(this, "jobs", "jobs/blacksmith.yml");
        carpentry = new FileManager(this, "jobs", "jobs/carpentry.yml");
        cooking = new FileManager(this, "jobs", "jobs/cooking.yml");
        elementsmith = new FileManager(this, "jobs", "jobs/elementsmith.yml");
        loom = new FileManager(this, "jobs", "jobs/loom.yml");
        melting = new FileManager(this, "jobs", "jobs/melting.yml");


    }

    @Override
    public void onEnable() {

        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(new GainExp(), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
