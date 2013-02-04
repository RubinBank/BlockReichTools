package me.criztovyl.blockreichtools;


import java.util.logging.Logger;

import me.criztovyl.blockreichtools.config.Config;
import me.criztovyl.blockreichtools.timeshift.TimeShift;
import me.criztovyl.blockreichtools.timeshift.TimeShiftType;
import me.criztovyl.blockreichtools.tools.MySQL;
import me.criztovyl.blockreichtools.tools.MySQL_;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is BlockReichTools. Tools for your Minecraft Bukkit Server!
 * @author criztovyl
 *
 */
public class BlockReichTools extends JavaPlugin{
	private static Logger log;
	private static MySQL_ mysql;
	/**
	 * Bukkit Stuff
	 */
	public void onEnable(){
		log = this.getLogger();
		log.info("BlockReichTools enabeling");
		this.saveDefaultConfig();
		Bukkit.getPluginManager().registerEvents(new Listeners(), this);
		log.info("Registered Listeners");
		mysql = new MySQL_(Config.HostAddress(), Config.HostUser(), Config.HostPassword());
		String query;
			if(Config.isSet("MySQL.Host.Table_Users")){
				query = String.format("Create table if not exists %s (id int auto_increment not null primary key, user varchar(50), lastlog date, password varchar(40))",
						Config.UsersTable());
				mysql.executeUpdate(query);
			}
			else
				log.warning("The Users Table is not set in the Config!");
			if(Config.isSet("MySQL.Host.Table_Sign")){
				query = String.format("Create table if not exists %s (id int auto_increment not null primary key, LocX int, LocY int, LocZ int, LocWorldUUID varchar(128), Pos varchar(10), Multi boolean default 1, Type varchar(20))",
						Config.SignsTable());
				mysql.executeUpdate(query);
			}
			else
				log.warning("The Signs Table is not set in the Config!");
		log.info("Checked Tables");
		MySQL.loadSigns();
		log.info("Loaded Signs");
		log.info("BlockReichTools enabled");
	}
	/**
	 * Bukkit Stuff
	 */
	public void onDisable(){
		log.info("BlockReichTools disabled");
	}
	/**
	 * Bukkit Stuff
	 */
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args){
		if(sender instanceof Player){
			Player player = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("BlockReichTools")){
				if(args.length > 0){
					if(args[0].toLowerCase().equals("day")){
						player.getWorld().setTime(800);
						return true;
					}
					if(args[0].toLowerCase().equals("rain")){
						player.getWorld().setStorm(!player.getWorld().hasStorm());
						return true;
					}
					if(args[0].toLowerCase().equals("inv")){
						if(args[1].toLowerCase().equals("nodirt")){
							if(args.length == 3){
								try{
									ItemStack items;
									items = new ItemStack(Material.DIRT, Integer.parseInt(args[2]));
									player.getInventory().removeItem(items);
								} catch(NumberFormatException e){
									player.sendMessage(ChatColor.RED + "'amount' muss eine Zahl sein!");
									player.sendMessage(ChatColor.YELLOW + "/brt inv nodirt amount");
									return true;
								}				
							}
							else{
								player.getInventory().remove(Material.DIRT);
							}
							
							player.sendMessage(ChatColor.GREEN + "Dirt entfernt.");
							return true;
						}
						if(args[1].toLowerCase().equals("clear")){
							player.getInventory().clear();
							player.sendMessage(ChatColor.GREEN + "Inventar geleert.");
							return true;
						}
					}
					if(args[0].toLowerCase().equals("help")){
						player.sendMessage(ChatColor.GREEN + "/brt day: Zeit auf Tag setzten (08:00h)");
						player.sendMessage(ChatColor.GREEN + "/brt inv nodirt [X]: entfernt allen oder X Dirtblöcke aus deinem Inventar.");
						player.sendMessage(ChatColor.GREEN + "/brt inv clear: leert deinen Inventar.");
						player.sendMessage(ChatColor.GREEN + "/brt rain: Ändert das Wetter");
						player.sendMessage(ChatColor.GREEN + "/brt passwort: Setzt das Passwort für das WebInterface.");
						player.sendMessage(ChatColor.GREEN + "/brt help: Diese Hilfe.");
						return true;
					}
					if(args[0].toLowerCase().equals("password") || equals("passwd") || equals("passwort") || equals("pw")){
						TimeShift.addShifted(player.getName(), TimeShiftType.UCP_PASS);
						return true;
					}
					if(args[0].toLowerCase().equals("mysql")){
						return MySQL.MySQLCommand(args, sender);
					}
				}

			}//BRT END
			if(cmd.getName().equalsIgnoreCase("mysql")){
				return MySQL.MySQLCommand(args, sender);
			}
		}
		else{
			if(cmd.getName().equalsIgnoreCase("blockreichtools")){
				info("BlockReichTools working ;)");
				return true;
			}
			if(cmd.getName().equalsIgnoreCase("mysql")){
				return MySQL.MySQLCommand(args, sender);
			}
			return true;
		}
		return false;
	}
	/**
	 * The BlockReichTools Logger.
	 * @return the Logger
	 */
	public static Logger getLog(){
		return log;
	}
	/**
	 * Logs a "[SEVERE]" Message.
	 * @param msg - Severe Message to log.
	 */
	public static void severe(String msg){
		log.severe(msg);
	}
	/**
	 * Logs a "[INFO]" Message.
	 * @param msg - Info Message to log.
	 */
	public static void info(String msg){
		log.info(msg);
	}
	public static MySQL_ getMySQL_() {
		return mysql;
	}
}
