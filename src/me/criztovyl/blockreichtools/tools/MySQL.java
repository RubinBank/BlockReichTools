package me.criztovyl.blockreichtools.tools;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import me.criztovyl.blockreichtools.BlockReichTools;
import me.criztovyl.blockreichtools.config.Config;
import me.criztovyl.blockreichtools.timeshift.TimeShiftType;
import me.criztovyl.clicklesssigns.ClicklessSigns.SignPos;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MySQL {
	//TODO Make more secure with a Salt
	public static void setPassword(String p_n, String password){
		String query = String.format("Update %s set password=SHA1('%s') where user='%s'", Config.UsersTable(),
				password, p_n);
		BlockReichTools.getMySQL_().executeUpdate(query);
	}
	public static boolean inDB(String p_n){
		String query = String.format("Select user from %s", Config.UsersTable());
		try{
			ResultSet rs = BlockReichTools.getMySQL_().executeQuery(query);
			while(rs.next()){
				if(rs.getString("user").equals(p_n)){
					return true;
				}
			}
			return false;
		} catch(SQLException e){
			BlockReichTools.severe("MySQL ResultSet Exception: " + e.toString() + "\n @ inDB Method");
			return false;
		}
	}
	public static void addUser(String p_n){
		String query = String.format("Insert into %s (user, lastlog) values('%s', NOW())", Config.UsersTable(), p_n);
		BlockReichTools.getMySQL_().executeUpdate(query);
	}
	public static void updateUserLogin(String p_n){
		String query = String.format("Update %s set lastlog=NOW() where user='%s'", Config.UsersTable(), p_n);
		BlockReichTools.getMySQL_().executeUpdate(query);
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
			
			ResultSet resultset;
			
			if(args.length == 0){
				switch(type){
				case CONSOLE:
					BlockReichTools.info("MySQL Command. Write /mysql [query...] do execute a Query or /mysql update [query...] to execute a update.");
					break;
				case PLAYER:
					Tools.msg(sender.getName(), "Nutze /mysql [query...] um eine Abfrage auszuführen oder /mysql update [query...] um ein Update auszuführen.");
				}
				return true;
			}
			else{
				if(args.length > 2){
					if(args[0].equals("update")){
						String query = "";
						for(int i = 1; i < args.length; i++){
							query += " " + args[i];
						}
						BlockReichTools.getMySQL_().executeUpdate(query);
						return true;
					}
				}
				String query = "";
				for(int i = 0; i < args.length; i++){
					query += " " + args[i];
				}
				
				resultset = BlockReichTools.getMySQL_().executeQuery(query);
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
	public static void addSignToDB(Location loc, SignPos pos, TimeShiftType t){
		String query = String.format("Insert into %s (LocX, LocY, LocZ, LocWorldUUID, Type, Pos, Multi) values (%d, %d, %d, '%s', '%s', '%s', 0",
				loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getUID().toString(), t.toString(), pos.toString());
		BlockReichTools.getMySQL_().executeUpdate(query);
	}
	public static void loadSigns(){
		int x, y, z;
		World world;
		String query = String.format("SELECT * FROM %s", Config.SignsTable());
		try{
			ResultSet rs = BlockReichTools.getMySQL_().executeQuery(query);
			BlockReichTools.info("Executed Query");
			while(rs.next()){
				x = rs.getInt("LocX");
				y = rs.getInt("LocY");
				z = rs.getInt("LocZ");
				world = Bukkit.getWorld(UUID.fromString(rs.getString("LocWorldUUID")));
				SignPos pos = SignPos.valueOf(rs.getString("Pos").toUpperCase());
				TimeShiftType t;
				if(!rs.getBoolean("Multi"))
					t = TimeShiftType.valueOf(rs.getString("Type").toUpperCase());
				else
					t = TimeShiftType.DEFAULT;
				Tools.addSign(new Location(world, x, y, z), pos, t);
				BlockReichTools.info("Added Sign");
			}
		}catch (SQLException e) {
			BlockReichTools.severe("MySQL ResultSet Exception @ load Signs\n" + e.toString());
		}
	}
	public static void removeSign(Location loc){
		BlockReichTools.info("Requested Sign Remove");
		String query = String.format("Delete from %s.%s where LocX=%d AND LocY=%d AND LocZ=%dm AND LocWorldUUID='%s'",
				Config.HostDatabase(), Config.SignsTable(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getUID().toString());
		BlockReichTools.getMySQL_().executeUpdate(query);
		BlockReichTools.info("Removed Sign.");
	}
}
