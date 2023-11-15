package de.lightplugins.lightcrafting.util;

import de.lightplugins.lightcrafting.main.LightCrafting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.logging.Level;

public class FileManager {

    private final LightCrafting plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    private final String configName;
    private final String subfolderName; // Neue Variable für den Unterordner

    public FileManager(LightCrafting plugin, String subfolderName, String configName) {
        this.plugin = plugin;
        this.subfolderName = subfolderName;
        this.configName = configName;

        // Debug-Ausgabe hinzufügen
        plugin.getLogger().info("subfolderName: " + subfolderName);
        plugin.getLogger().info("configName: " + configName);

        // Initialisiere configFile bevor saveDefaultConfig aufgerufen wird
        if(subfolderName != null) {
            File dataFolder = this.plugin.getDataFolder();
            plugin.getLogger().info("dataFolder: " + dataFolder.getAbsolutePath());

            File subFolder = new File(dataFolder, subfolderName);
            if (!subFolder.exists()) {
                boolean success = subFolder.mkdirs();
                if (!success) {
                    plugin.getLogger().warning("Could not create subfolder: " + subFolder.getPath());
                }
            }

            this.configFile = new File(subFolder, configName);
            plugin.getLogger().info("configFile: " + configFile.getAbsolutePath());
        } else {
            this.configFile = new File(this.plugin.getDataFolder(), configName);
            plugin.getLogger().info("configFile: " + configFile.getAbsolutePath());
        }

        saveDefaultConfig(configName);
    }

    public void reloadConfig(String configName) {
        if(this.configFile == null)
            this.configFile = new File(this.plugin.getDataFolder() + File.separator + subfolderName, configName);

        this.plugin.reloadConfig();

        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        InputStream defaultStream = this.plugin.getResource(configName);
        if(defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if(this.dataConfig == null)
            reloadConfig(configName);

        return this.dataConfig;
    }

    public void saveConfig() {
        if(this.dataConfig == null || this.configFile == null)
            return;

        try {
            this.getConfig().save(this.configFile);
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save config to " + this.configFile, e);
        }
    }

    private void saveDefaultConfig(String configName) {
        if (this.configFile == null) {
            File dataFolder = this.plugin.getDataFolder();
            if(subfolderName != null) {
                File subFolder = new File(dataFolder, subfolderName);
                if (!subFolder.exists()) {
                    boolean success = subFolder.mkdirs();
                    if (!success) {
                        plugin.getLogger().warning("Could not create subfolder: " + subFolder.getPath());
                    }
                }

                this.configFile = new File(subFolder, configName);
            }
        }

        assert this.configFile != null;
        if (!this.configFile.exists()) {
            this.plugin.saveResource(configName, false);
        } else {
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(Objects.requireNonNull(this.plugin.getResource(configName))));
            FileConfiguration existingConfig = getConfig();
            for (String key : defaultConfig.getKeys(true)) {
                if (!existingConfig.getKeys(true).contains(key)) {
                    Bukkit.getConsoleSender().sendMessage(LightCrafting.consolePrefix +
                            "Found §cnon existing config key§r. Adding §c" + key + " §rinto §c" + configName);
                    existingConfig.set(key, defaultConfig.get(key));
                }
            }

            try {
                existingConfig.save(configFile);
                Bukkit.getConsoleSender().sendMessage(LightCrafting.consolePrefix +
                        "Your config §c" + configName + " §ris up to date.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            saveConfig();
        }
    }
}
