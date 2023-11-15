package de.lightplugins.lightcrafting.database.tables;

import de.lightplugins.lightcrafting.main.LightCrafting;
import de.lightplugins.lightcrafting.util.TableStatements;
import org.bukkit.configuration.file.FileConfiguration;

public class PlayerData {

    public void createMoneyTable() {

        String tableName = "PlayerData";
        String tableStatement;
        TableStatements tableStatements = new TableStatements();
        FileConfiguration settings = LightCrafting.settings.getConfig();

        tableStatement = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                + "uuid TEXT,"
                + "money DOUBLE,"
                + "isPlayer BOOL,"
                + "PRIMARY KEY (uuid))";

        if(settings.getBoolean("mysql.enable")) {
            tableStatement = "CREATE TABLE IF NOT EXISTS " + tableName + " ("
                    + "uuid TEXT(200),"
                    + "name TEXT,"
                    + "money DOUBLE,"
                    + "isPlayer BOOL,"
                    + "PRIMARY KEY (uuid(200)))";
        }

        tableStatements.createTableStatement(tableStatement);
    }
}
