package de.lightplugins.lightcrafting.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import de.lightplugins.lightcrafting.main.LightCrafting;
import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseConnection {

    public void connectToDataBaseViaMariaDB() {

        FileConfiguration config = LightCrafting.settings.getConfig();

        String host = config.getString("mysql.host");
        String port = config.getString("mysql.port");
        String database = config.getString("mysql.database");
        String user = config.getString("mysql.user");
        String password = config.getString("mysql.password");
        Boolean ssl = config.getBoolean("mysql.ssl");
        Boolean useServerPrepStmts = config.getBoolean("mysql.advanced.useServerPrepStmts");
        Boolean cachePrepStmts = config.getBoolean("mysql.advanced.cachePrepStmts");
        int prepStmtCacheSize = config.getInt("mysql.advanced.prepStmtCacheSize");
        int prepStmtCacheSqlLimit = config.getInt("mysql.advanced.prepStmtCacheSqlLimit");
        int connectionPoolSize = config.getInt("mysql.advanced.connectionPoolSize");

        HikariConfig hikariConfig = new HikariConfig();
        //hikariConfig.setDataSourceClassName("org.mariadb.jdbc.MariaDbDataSource");
        hikariConfig.setJdbcUrl("jdbc:mariadb://" +host + ":" + port + "/" + database);
        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");
        hikariConfig.addDataSourceProperty("serverName", host);
        hikariConfig.addDataSourceProperty("port", port);
        hikariConfig.addDataSourceProperty("databaseName", database);
        hikariConfig.addDataSourceProperty("user", user);
        hikariConfig.addDataSourceProperty("password", password);
        hikariConfig.addDataSourceProperty("useSSL", ssl);
        hikariConfig.setMaximumPoolSize(connectionPoolSize);
        hikariConfig.addDataSourceProperty("useServerPrepStmts", useServerPrepStmts);
        hikariConfig.addDataSourceProperty("cachePrepStmts", cachePrepStmts);
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize);
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit);

        LightCrafting.getInstance.ds = new HikariDataSource(hikariConfig);


    }
}
