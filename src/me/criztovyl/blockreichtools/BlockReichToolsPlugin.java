package me.criztovyl.blockreichtools;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import me.criztovyl.blockreichtools.config.Config;
import me.criztovyl.blockreichtools.tools.MySQL;
import me.criztovyl.clickless.ClicklessPlugin;
import me.criztovyl.questioner.MicroQuestioner;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * This is BlockReichTools. Tools for your Minecraft Bukkit Server!
 * @author criztovyl
 *
 */
public class BlockReichToolsPlugin extends JavaPlugin{
        private static Logger log;
        private static Connection con;
        private static Economy econ;
        private static BlockReichTools brt;
        /**
         * Bukkit Stuff  */
        public void onEnable(){
                log = this.getLogger();
                log.info("BlockReichTools enabeling");
                this.saveDefaultConfig();
                Bukkit.getPluginManager().registerEvents(new Listeners(), this);
                log.info("Registered Listeners");
                String query = "";
                econ = null;
                brt = new BlockReichTools(this);
                try{
                        brt.getReopenable().getDatabaseConnection();
                } catch (SQLException e) {
                        severe("Could not get Database Connection! Error:\n" + e.toString());
                        disable();
                }
                try{
                        con = getConnection();
                        Statement stmt = con.createStatement();
                        if(Config.isSet("MySQL.Host.Table_Users")){
                                query = String.format("Create table if not exists %s (id int auto_increment not null primary key, user varchar(50), lastlog date, password varchar(40), userhash varchar(40))", 
                                                Config.UsersTable());
                                stmt.executeUpdate(query);
                        }
                        else{
                                log.warning("The Users Table is not set in the Config!");
                                disable();
                        }
                        if(Config.isSet("MySQL.Host.Table_Signs")){
                                query = String.format("Create table if not exists %s (id int auto_increment not null primary key, LocX int, LocY int, LocZ int, LocWorldUUID varchar(128), Pos varchar(10), Multi boolean default 1, Type varchar(20))",
                                                Config.SignsTable());
                                stmt.executeUpdate(query);
                        }
                        else{
                                log.warning("The Signs Table is not set in the Config!");
                                disable();
                        }
                        con.close();
                } catch (SQLException e){
                        severe("Failed to Check Tables! Error:\n" + e.toString() + "\n@Query: " + query);
                }
                log.info("Checked Tables");
                MySQL.loadSigns();
                log.info("Loaded Signs");
                log.info("BlockReichTools enabled");
                if(!Config.isSet("MySQL.Global_Salt")){
                        log.warning("You must set a Global Salt in the Config!(MySQL.Global_Salt)");
                        disable();
                }
                if(Bukkit.getServer().getPluginManager().getPlugin("Vault") != null){
                    setEco();
                }
        }
        private void disable(){
                this.getPluginLoader().disablePlugin((Plugin) this);
        }
        public static boolean setEco(){
            if(Bukkit.getPluginManager().getPlugin("Vault") == null){
                return false;
            }
            RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
            if(rsp == null){
                return false;
            }
            econ = rsp.getProvider();
            return econ != null;
        }
        public static Economy getEco(){
            return econ;
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
        public boolean onCommand(final CommandSender sender, Command cmd, String commandLabel, String[] args){
                if(sender instanceof Player){
                        Player player = (Player) sender;
                        if(cmd.getName().equalsIgnoreCase("BlockReichTools")){
                                if(args.length > 0){
                                        if(args[0].toLowerCase().equals("invtest")){
                                            ItemStack[] stacks = player.getInventory().getContents();
                                            for(int i = 0; i < stacks.length; i++){
                                                if(stacks[i] != null)
                                                player.sendMessage(stacks[i].toString());
                                            }
                                        }
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
                                                player.sendMessage(ChatColor.GREEN + "/brt day: Zeit auf Tag setzen (08:00h)");
                                                player.sendMessage(ChatColor.GREEN + "/brt inv nodirt [X]: entfernt alle oder X Dirtblöcke aus deinem Inventar.");
                                                player.sendMessage(ChatColor.GREEN + "/brt inv clear: leert deinen Inventar.");
                                                player.sendMessage(ChatColor.GREEN + "/brt rain: Ändert das Wetter");
                                                player.sendMessage(ChatColor.GREEN + "/brt passwort: Setzt das Passwort für das WebInterface.");
                                                player.sendMessage(ChatColor.GREEN + "/brt help: Diese Hilfe.");
                                                return true;
                                        }
                                        if(args[0].toLowerCase().equals("password") ||
                                                        args[0].toLowerCase().equals("passwd") ||
                                                        args[0].toLowerCase().equals("passwort") ||
                                                        args[0].toLowerCase().equals("pw")){
                                                ClicklessPlugin.getMicroQuestions().addQuestioner(new MicroQuestioner() {
                                                        boolean isSet = false;
                                                        @Override
                                                        public boolean getSuccess() {
                                                                return isSet;
                                                        }
                                                        
                                                        @Override
                                                        public String getQuestion() {
                                                                return "Bitte gebe jetzt dein Passwort ein:";
                                                        }
                                                        
                                                        @Override
                                                        public String getPlayer() {
                                                                return sender.getName();
                                                        }
                                                        
                                                        @Override
                                                        public void executeAction(AsyncPlayerChatEvent arg0) {
                                                                String msg = arg0.getMessage();
                                                                if(msg != ""){
                                                                        MySQL.setPassword(arg0.getPlayer().getName(), msg);
                                                                        arg0.getPlayer().sendMessage(ChatColor.GREEN + "Passwort gesetzt!");
                                                                        arg0.setMessage("[PASSWORD]");
                                                                        isSet = true;
                                                                }
                                                                else{
                                                                        arg0.getPlayer().sendMessage(ChatColor.RED + "Kein Passwort gesetzt!");
                                                                }
                                                                arg0.setCancelled(true);
                                                        }
                                                });
                                                return true;
                                        }
                                        if(args[0].toLowerCase().equals("mysql")){
                                                return MySQL.MySQLCommand(args, sender);
                                        }
                                        if(args[0].toLowerCase().equals("vault")){
                                            
                                        }
                                }

                        }//BRT END
                        if(cmd.getName().equalsIgnoreCase("mysql")){
                                return MySQL.MySQLCommand(args, sender);
                        }
                }
                else{
                        if(cmd.getName().equalsIgnoreCase("blockreichtools")){
                                if(args[0].toLowerCase().equals("vault")){
                                    log.info(Boolean.toString(econ.hasAccount("christs96")));
                                }
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
        public static Connection getConnection() {
                try {
                    return brt.getReopenable().getDatabaseConnection();
                } catch (SQLException e) {
                    severe("Unable to create Database Connection: " + e.toString());
                    return null;
                }
        }
}
