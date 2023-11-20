package de.lightplugins.lightcrafting.util;

import de.lightplugins.lightcrafting.main.LightCrafting;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.logging.Level;

public class FileManager {

    private final LightCrafting plugin;
    private FileConfiguration dataConfig = null;
    private File configFile = null;
    private final String configName;
    private final String subfolderName;

    public FileManager(LightCrafting plugin, String subfolderName, String configName) {
        this.plugin = plugin;
        this.subfolderName = subfolderName;
        this.configName = configName;

        plugin.getLogger().info("subfolderName: " + subfolderName);
        plugin.getLogger().info("configName: " + configName);


        if (subfolderName != null) {
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
        if (this.configFile == null)
            this.configFile = new File("plugins/lightCrafting/" + subfolderName + File.separator + configName);

        plugin.getLogger().info("Config Path: " + this.configFile.getAbsolutePath());

        plugin.getLogger().info("Attempting to reload config: " + configFile.getAbsolutePath());

        this.plugin.reloadConfig();

        plugin.getLogger().info("Config reloaded.");

        this.dataConfig = YamlConfiguration.loadConfiguration(this.configFile);

        plugin.getLogger().info("Config loaded.");

        InputStream defaultStream = this.plugin.getResource(configName);
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream));
            this.dataConfig.setDefaults(defaultConfig);
        }
    }

    public FileConfiguration getConfig() {
        if (this.dataConfig == null)
            reloadConfig(configName);

        return this.dataConfig;
    }

    public void saveConfig() {
        if (this.dataConfig == null || this.configFile == null)
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
            if (subfolderName != null) {
                File subFolder = new File(dataFolder, subfolderName);
                if (!subFolder.exists()) {
                    boolean success = subFolder.mkdirs();
                    if (!success) {
                        plugin.getLogger().warning("Could not create subfolder: " + subFolder.getPath());
                    }
                }

                this.configFile = new File(subFolder, configName);
            } else {
                this.configFile = new File(dataFolder, configName);
            }
        }

        plugin.getLogger().info("Checking if default config needs to be saved: " + configFile.getAbsolutePath());
        String resourcePath = (subfolderName != null) ? subfolderName + "/" + configName : configName;
        if (!this.configFile.exists()) {
            // Versuche, die Ressource im Jar zu finden

            InputStream defaultStream = getClass().getClassLoader().getResourceAsStream(resourcePath);

            plugin.getLogger().warning("Default config in " + configName);

            if (defaultStream != null) {
                plugin.getLogger().info("Default config saved: " + configName);
                try {
                    // Kopiere den InputStream in die Konfigurationsdatei
                    Files.copy(defaultStream, configFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Error copying default config from resources" + e.getMessage(), e);
                }
            } else {
                plugin.getLogger().warning("Default config not found in resources: " + configName);
            }
        } else {
            FileConfiguration defaultConfig = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(Objects.requireNonNull(this.plugin.getResource(resourcePath))));
            FileConfiguration existingConfig = getConfig();

            for (String key : defaultConfig.getKeys(true)) {
                if (!existingConfig.getKeys(true).contains(key)) {
                    Bukkit.getConsoleSender().sendMessage(LightCrafting.consolePrefix +
                            "Found §cnon-existing config key§r. Adding §c" + key + " §rinto §c" + configName);
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
