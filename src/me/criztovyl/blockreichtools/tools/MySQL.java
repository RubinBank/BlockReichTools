package me.criztovyl.blockreichtools.tools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.UUID;

import me.criztovyl.blockreichtools.BlockReichTools;
import me.criztovyl.blockreichtools.config.Config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MySQL {
	public static void setPassword(String p_n, String password){
		Connection con = BlockReichTools.getConnection();
		String query = String.format("Update %s set password=SHA1(CONCAT(userhash,'%s%s')) where user='%s'", Config.UsersTable(),
				password, Config.getGlobalSalt(), p_n);
		try{
			con.createStatement().executeUpdate(query);
			con.close();
		} catch(SQLException e){
			BlockReichTools.severe("Failed to set Password! Error:\n" + e.toString() + "\n @ Query" + String.format("Update %s set password=SHA1(CONCAT(userhash,'%s%s')) where user='%s'", Config.UsersTable(),
					"[PASSWORD]", Config.getGlobalSalt(), p_n));
		}
	}
	public static boolean inDB(String p_n){
		String query = String.format("Select user from %s", Config.UsersTable());
		Connection con = BlockReichTools.getConnection();
		try{
			ResultSet rs = con.createStatement().executeQuery(query);
			while(rs.next()){
				if(rs.getString("user").equals(p_n)){
					con.close();
					return true;
				}
			}
			con.close();
			return false;
		} catch(SQLException e){
			BlockReichTools.severe("MySQL Exception: " + e.toString() + "\n @ inDB Method");
			return false;
		}
	}
	public static void addUser(String p_n){
		Date d = new Date();
		String userhash = Tools.hash(Long.toString(d.getTime()));
		String query = String.format("Insert into %s (user, lastlog, userhash) values('%s', NOW(), '%s')", Config.UsersTable(), p_n, userhash);
		Connection con = BlockReichTools.getConnection();
		try{
			con.createStatement().executeUpdate(query);
			con.close();
		} catch(SQLException e){
			BlockReichTools.severe("Failed to add user! Error:\n" + e.toString() + "\n @ Query" + query);
		}
	}
	public static void updateUserLogin(String p_n){
		String query = String.format("Update %s set lastlog=NOW() where user='%s'", Config.UsersTable(), p_n);
		Connection con = BlockReichTools.getConnection();
		try{
			con.createStatement().executeUpdate(query);
			con.close();
		} catch(SQLException e){
			BlockReichTools.severe("Failed to update User login! Error:\n" + e.toString() + "\n @ Query" + query);
		}
	}
	private static enum MySQLCommandType {
		PLAYER,
		CONSOLE;
		public void msg(String msg, CommandSender sender){
			switch(this){
			case CONSOLE:
				BlockReichTools.info(msg);
				break;
			case PLAYER:
				Tools.msg(sender.getName(), msg);
			}
		}
	}
	public static boolean MySQLCommand(String[] args, CommandSender sender){
		
		MySQLCommandType type;
		Connection con = BlockReichTools.getConnection();
		if(sender instanceof Player){
			type = MySQLCommandType.PLAYER;
		}
		else{
			type = MySQLCommandType.CONSOLE;
		}
		try{
			
			ResultSet resultset;
			
			if(args.length == 0){
				switch(type){
				case CONSOLE:
					BlockReichTools.info("MySQL Command. Write /mysql [query...] to execute a Query or /mysql update [query...] to execute a update.");
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
						try{
							con.createStatement().executeUpdate(query);
							con.close();
							type.msg("Done ;)", sender);
						} catch(SQLException e){
							BlockReichTools.severe("Command failed! Error:\n" + e.toString() + "\n @ Query" + query);
						}
						return true;
					}
				}
				String query = "";
				for(int i = 0; i < args.length; i++){
					query += " " + args[i];
				}
				try{
					resultset = con.createStatement().executeQuery(query);
					con.close();
				} catch(SQLException e){
					BlockReichTools.severe("Command failed! Error:\n" + e.toString() + "\n @ Query" + query);
					return true;
				}
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
	public static void addSignToDB(Location loc, SignPos pos, SignType t){
		String query = String.format("Insert into %s (LocX, LocY, LocZ, LocWorldUUID, Type, Pos, Multi) values (%d, %d, %d, '%s', '%s', '%s', 0)",
				Config.SignsTable(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getUID().toString(), t.toString(), pos.toString());
		Connection con = BlockReichTools.getConnection();
		try{
			con.createStatement().executeUpdate(query);
			con.close();
		} catch(SQLException e){
			BlockReichTools.severe("Failed to insert Sign! Error:\n" + e.toString() + "\n @ Query" + query);
		}
	}
	public static void loadSigns(){
		int x, y, z;
		World world;
		String query = String.format("SELECT * FROM %s", Config.SignsTable());
		Connection con = BlockReichTools.getConnection();
		try{
			ResultSet rs = con.createStatement().executeQuery(query);
			BlockReichTools.info("Executed Query");
			while(rs.next()){
				x = rs.getInt("LocX");
				y = rs.getInt("LocY");
				z = rs.getInt("LocZ");
				world = Bukkit.getWorld(UUID.fromString(rs.getString("LocWorldUUID")));
				SignPos pos = SignPos.valueOf(rs.getString("Pos").toUpperCase());
				SignType t = null;
				if(!rs.getBoolean("Multi"))
					t = SignType.valueOf(rs.getString("Type").toUpperCase());
				BlockReichTools.severe("Invalid Sign in DB: SignType '" + rs.getString("Type") + "' unknown.");
				Tools.addSign(new Location(world, x, y, z), pos, t);
				BlockReichTools.info("Added Sign");
			}
		}catch (SQLException e) {
			BlockReichTools.severe("MySQL ResultSet Exception @ load Signs\n" + e.toString());
			e.printStackTrace();
		}
	}
	public static void removeSign(Location loc){
		BlockReichTools.info("Requested Sign Remove");
		String query = String.format("Delete from %s where LocX=%d AND LocY=%d AND LocZ=%d AND LocWorldUUID='%s'",
				Config.SignsTable(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getUID().toString());
		Connection con = BlockReichTools.getConnection();
		try{
			con.createStatement().executeUpdate(query);
			con.close();
		} catch(SQLException e){
			BlockReichTools.severe("Failed to remove Sign! Error:\n" + e.toString() + "\n @ Query" + query);
		}
		BlockReichTools.info("Removed Sign.");
	}
}
