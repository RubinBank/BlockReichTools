package me.criztovyl.blockreichtools.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import me.criztovyl.blockreichtools.BlockReichTools;
import me.criztovyl.blockreichtools.config.Config;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MySQL {
	public static void setPassword(String p_n, String password){
		try{
			BlockReichTools.getConnection().createStatement().executeUpdate("UPDATE " + Config.UserTable() + " SET password=SHA1(\"" + password + "\") WHERE user=\"" + p_n + "\"");
		} catch(SQLException e){
			BlockReichTools.severe("SQL Exception: " +  e.toString() + "\nQuery: UPDATE " + Config.UserTable() + " SET password=SHA1(\"" + password + "\") WHERE user=\"" + p_n + "\"");
		}
	}
	public static boolean inDB(String p_n){
		try{
			ResultSet rs = BlockReichTools.getConnection().createStatement().executeQuery("Select user from " + Config.UserTable());
			while(rs.next()){
				if(rs.getString("user").equals(p_n)){
					return true;
				}
			}
			return false;
		} catch(SQLException e){
			BlockReichTools.severe("SQL Exception: " + e.toString() + "\nAt inDB Method");
			return false;
		}
	}
	public static void addUser(String p_n){
		try{
			BlockReichTools.getConnection().createStatement().executeUpdate("INSERT INTO " + Config.UserTable() + " (user, lastlog) values(\"" + p_n + "\", NOW())");
		} catch(SQLException e){
			BlockReichTools.severe("SQL Exception: " + e.toString() + "\nAt addUser Method Query: INSERT INTO " + Config.UserTable() + " (user, lastlog) values(\"" + p_n + "\", NOW())");
		}
	}
	public static void updateUserLogin(String p_n){
		try{
			BlockReichTools.getConnection().createStatement().executeUpdate("Update " + Config.UserTable() + " set lastlog=NOW() where user=\"" + p_n + "\"");
		} catch(SQLException e){
			BlockReichTools.severe("SQL Exception: " + e.toString() + "\nAt updateUserLogin Method Query: Update " + Config.UserTable() + " set lastlog=NOW() where user=\"" + p_n + "\"");
		}
	}
	private enum MySQLCommandType {
		PLAYER,
		CONSOLE;
	}
	public static boolean MySQLCommand(String[] args, CommandSender sender){
		MySQLCommandType type;
		if(sender instanceof Player){
			type = MySQLCommandType.PLAYER;
		}
		else{
			type = MySQLCommandType.CONSOLE;
			BlockReichTools.info("Works?");
		}
		try{
			
			Statement stmt = BlockReichTools.getConnection().createStatement();
			
			ResultSet resultset;
			
			if(args.length == 0){
				resultset = stmt.executeQuery("describe " + Config.UserTable());
			}
			else{
				if(args.length > 2){
					if(args[0].equals("update")){
						String query = "";
						for(int i = 1; i < args.length; i++){
							query += " " + args[i];
						}
						
						stmt.executeUpdate(query);
						return true;
					}
				}
				String query = "";
				for(int i = 0; i < args.length; i++){
					query += " " + args[i];
				}
				
				resultset = stmt.executeQuery(query);
			}
			resultset.last();
			int rowcount = resultset.getRow();
			resultset.beforeFirst();
			int columns = resultset.getMetaData().getColumnCount();
			String results[][] = new String[rowcount+1][columns];
			for(int k = 1; k <= columns; k++){
				results[0][k-1] = resultset.getMetaData().getColumnName(k);
			}
			for(int i = 1; resultset.next(); i++){
				for(int j = 1; j <= columns; j++){
					results[i][j-1] = resultset.getString(j);
				}
			}
			for(int i = 0; i < results.length; i++){
				String out = "";
				for(int j = 0; j < results[i].length; j++){
					out += results[i][j]+ " ";
				}
				if(type.equals(MySQLCommandType.CONSOLE))
				BlockReichTools.info(out);
				else
					sender.sendMessage(out);
			}
			return true;
		} catch(SQLException e){
			BlockReichTools.severe("MySQL exception:\n" + e.toString());
			return true;
		}
	}
	
}
