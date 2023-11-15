package de.lightplugins.lightcrafting.enums;

import de.lightplugins.lightcrafting.main.LightCrafting;
import org.bukkit.configuration.file.FileConfiguration;

public enum MessagePath {

    Prefix("prefix"),

    ;

    private final String path;

    MessagePath(String path) { this.path = path; }
    public String getPath() {
        FileConfiguration paths = LightCrafting.messages.getConfig();
        try {
            return paths.getString(this.path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "ERROR";
    }

}
