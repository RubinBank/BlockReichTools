package me.criztovyl.blockreichtools.config;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
/**
 * The Configuration Tools for BlockReichTools
 * Only for internal use.
 * @author criztovyl
 *
 */
public class Config {
	private static FileConfiguration conf = Bukkit.getPluginManager().getPlugin("BlockReichTools").getConfig();
	public static String HostAddress(){
		return conf.getString("MySQL.Host.Address");
	}
	public static String HostUser(){
		return conf.getString("MySQL.Host.User");
	}
	public static String HostPassword(){
		return conf.getString("MySQL.Host.Password");
	}
	public static String HostDatabase(){
		return conf.getString("MySQL.Host.Database");
	}
	public static String MySQLConnectionURL(){
		return "jdbc:mysql://" + HostAddress() + "/" + HostDatabase() + "?user=" + HostUser() + "&password=" + HostPassword();
	}
	public static FileConfiguration getConf() {
		return conf;
	}
	public static void setConf(FileConfiguration conf) {
		Config.conf = conf;
	}
	public static String UsersTable(){
		return conf.getString("MySQL.Host.Table_Users");
	}
	public static String SignsTable(){
		return conf.getString("MySQL.Host.Table_Signs");
	}
	public static boolean isSet(String path){
		return conf.isSet(path);
	}
}
