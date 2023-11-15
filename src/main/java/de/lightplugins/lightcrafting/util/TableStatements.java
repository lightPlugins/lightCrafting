package de.lightplugins.lightcrafting.util;

import de.lightplugins.lightcrafting.main.LightCrafting;
import java.sql.*;

public class TableStatements {

    public void createTableStatement(String statement) {

        Connection connection = null;
        PreparedStatement ps = null;

        try {

            connection = LightCrafting.getInstance.ds.getConnection();

            ps = connection.prepareStatement(statement);
            ps.executeUpdate();
            //connection.commit();
            ps.close();

        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            if(connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if(ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}