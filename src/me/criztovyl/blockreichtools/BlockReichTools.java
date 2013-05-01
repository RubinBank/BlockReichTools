package me.criztovyl.blockreichtools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.criztovyl.reopenable.Reopenable;

import org.bukkit.plugin.Plugin;

public class BlockReichTools {
    private Reopenable reopenable;
    final private Plugin plugin;
    public BlockReichTools(final Plugin plugin){
        this.plugin = plugin;
        reopenable = new Reopenable() {
            
            @Override
            public Connection getDatabaseConnection() throws SQLException {
                String dbaddress = "jdbc:mysql://" + plugin.getConfig().getString("MySQL.Host.Address") + ":"
                        + plugin.getConfig().getInt("MySQL.Host.Port");
                String dbuser = plugin.getConfig().getString("MySQL.Host.User");
                String dbpassword = plugin.getConfig().getString("MySQL.Host.Password");
                return DriverManager.getConnection(dbaddress, dbuser, dbpassword);
            }
        };
    }
    public Plugin getPlugin() {
        return plugin;
    }
    public Reopenable getReopenable() {
        return reopenable;
    }
}
