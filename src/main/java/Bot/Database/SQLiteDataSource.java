package Bot.Database;

import Bot.Config;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class SQLiteDataSource implements IDataBaseManager {
    private final HikariDataSource dataSource;

    public SQLiteDataSource() {
        try {
            final File dbFile = new File("database.db");

            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }
        } catch (IOException error) {
            error.printStackTrace();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:database.db");
        config.setConnectionTestQuery("SELECT 1");
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtsCacheSize", "250");
        config.addDataSourceProperty("prepStmtsCacheSqlLimit", "2048");

        dataSource = new HikariDataSource(config);

        try (Connection connection = dataSource.getConnection();
             final Statement statement = connection.createStatement()) {
            final String defaultPrefix = Config.get("PREFIX");

            //language=SQLite
            statement.execute("CREATE TABLE IF NOT EXISTS guild_settings (" +
                    "guild_id VARCHAR(20) PRIMARY KEY NOT NULL," +
                    "prefix VARCHAR(255) NOT NULL DEFAULT '" + defaultPrefix + "'" + ")");

            statement.execute("CREATE TABLE IF NOT EXISTS alpacas_manager (" +
                    "member_id VARCHAR(20) PRIMARY KEY NOT NULL, " +
                    "hunger VARCHAR(3) DEFAULT 100, " +
                    "thirst VARCHAR(3) DEFAULT 100 " +
                    ")");

            statement.execute("CREATE TABLE IF NOT EXISTS inventory_manager (" +
                    "member_id VARCHAR(20) PRIMARY KEY NOT NULL, " +
                    "currency VARCHAR(3) DEFAULT 0, " +
                    "salad VARCHAR(3) DEFAULT 0, " +
                    "waterbottle VARCHAR(3) DEFAULT 0, " +
                    "battery VARCHAR(3) DEFAULT 0 " +
                    ")");

        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    @Override
    public String getPrefix(long guildID) {

        try (Connection connection = dataSource.getConnection();
             final PreparedStatement preparedStatement = connection
                     // language=SQLite
                     .prepareStatement("SELECT prefix FROM guild_settings WHERE guild_id = ?")) {

            preparedStatement.setString(1, String.valueOf(guildID));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("prefix");
                }
            }

            try (final PreparedStatement insertStatement = connection
                    // language=SQLite
                    .prepareStatement("INSERT INTO guild_settings(guild_id) VALUES(?)")) {

                insertStatement.setString(1, String.valueOf(guildID));
                insertStatement.execute();
            }

        } catch (SQLException error) {
            error.printStackTrace();
        }

        return Config.get("PREFIX");
    }

    @Override
    public void setPrefix(long guildID, String newPrefix) {

        try (Connection connection = dataSource.getConnection();
             final PreparedStatement preparedStatement = connection
                     // language=SQLite
                     .prepareStatement("UPDATE guild_settings SET prefix = ? WHERE guild_id = ?")) {

            preparedStatement.setString(1, newPrefix);
            preparedStatement.setString(2, String.valueOf(guildID));

            preparedStatement.executeUpdate();

        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    @Override
    public String getAlpaca(long memberID, String column) {

        try (Connection connection = dataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM alpacas_manager WHERE member_id = ?")) {

            preparedStatement.setString(1, String.valueOf(memberID));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(column);
                }
            }

            try (final PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO alpacas_manager(member_id, hunger, thirst, energy) VALUES(?, ?, ?, ?)")) {

                insertStatement.setString(1, String.valueOf(memberID));
                insertStatement.setString(2, String.valueOf(100));
                insertStatement.setString(3, String.valueOf(100));
                insertStatement.setString(4, String.valueOf(100));
                insertStatement.execute();
            }

        } catch (SQLException error) {
            error.printStackTrace();
        }

        return Config.get(column.toUpperCase());
    }

    @Override
    public void setAlpaca(long memberID, String column, String newValue) {
        String oldValue = IDataBaseManager.INSTANCE.getAlpaca(memberID, column);

        try (Connection connection = dataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE alpacas_manager SET " + column + " = ? WHERE member_id = ?")) {

            preparedStatement.setString(1, String.valueOf(Integer.parseInt(oldValue) + Integer.parseInt(newValue)));
            preparedStatement.setString(2, String.valueOf(memberID));

            preparedStatement.executeUpdate();

        } catch (SQLException error) {
            error.printStackTrace();
        }
    }

    @Override
    public String getInventory(long memberID, String column) {

        try (Connection connection = dataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM inventory_manager WHERE member_id = ?")) {

            preparedStatement.setString(1, String.valueOf(memberID));

            try (final ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString(column);
                }
            }

            try (final PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO inventory_manager(member_id, currency, salad, waterbottle, battery) VALUES(?, ?, ?, ?, ?)")) {

                insertStatement.setString(1, String.valueOf(memberID));
                insertStatement.setString(2, String.valueOf(0));
                insertStatement.setString(3, String.valueOf(0));
                insertStatement.setString(4, String.valueOf(0));
                insertStatement.setString(5, String.valueOf(0));
                insertStatement.execute();
            }

        } catch (SQLException error) {
            error.printStackTrace();
        }

        return Config.get(column.toUpperCase());
    }

    @Override
    public void setInventory(long memberID, String column, String newValue) {
        String oldValue = IDataBaseManager.INSTANCE.getInventory(memberID, column);

        try (Connection connection = dataSource.getConnection();
             final PreparedStatement preparedStatement = connection.prepareStatement("UPDATE inventory_manager SET " + column + " = ? WHERE member_id = ?")) {

            preparedStatement.setString(1, String.valueOf(Integer.parseInt(oldValue) + Integer.parseInt(newValue)));
            preparedStatement.setString(2, String.valueOf(memberID));

            preparedStatement.executeUpdate();

        } catch (SQLException error) {
            error.printStackTrace();
        }
    }
}
