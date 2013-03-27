package me.criztovyl.blockreichtools.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import me.criztovyl.blockreichtools.BlockReichTools;
import me.criztovyl.clickless.ClicklessSign;
import me.criztovyl.clickless.ClicklessPlugin;
import me.criztovyl.timeshift.TimeShifter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Tools {
	/**
	 * @see org.bukkit.command.CommandSender.sendMessage()
	 */
	public static void msg(String p_n, String msg){
		if(Bukkit.getServer().getPlayer(p_n) != null)
		Bukkit.getServer().getPlayer(p_n).sendMessage(msg);
	}
	public static void addSign(final Location loc, SignPos pos, SignType t){
		Location trigger_ = null;
		switch(pos){
		case DOWN:
			trigger_ = new Location(loc.getWorld(), loc.getBlockX(), loc.getY()-1, loc.getBlockZ());
			break;
		case UP:
			trigger_ = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY()+2, loc.getBlockZ());
		}
		final Location trigger = trigger_;
		switch(t){
		case UCP_PASS:
			ClicklessPlugin.getClickless().addClicklessSign(new ClicklessSign() {
				TimeShifter shifter = new TimeShifter() {
					ArrayList<String> shifteds = new ArrayList<String>();
					@Override
					public void removePlayer(String playername) {
						shifteds.remove(playername);
					}
					
					@Override
					public void preChatAction(String playername) {
						msg(playername, ChatColor.YELLOW + "Chat deaktiviert.\n " + ChatColor.GREEN + "Bitte gebe jetzt dein neues Passwort in den Chat ein!");
					}
					
					@Override
					public void onChatAction(AsyncPlayerChatEvent evt) {
						if(shifteds.contains(evt.getPlayer().getName())){
							MySQL.setPassword(evt.getPlayer().getName(), evt.getMessage());
							evt.setMessage("[PASSWORD]");
							evt.setCancelled(true);
						}
						
					}
					
					@Override
					public boolean hasPlayer(String playername) {
						return shifteds.contains(playername);
					}
					
					@Override
					public boolean getSuccess(String playername) {
						return !shifteds.contains(playername);
					}
					
					@Override
					public void afterChatAction(String playername) {
						msg(playername, ChatColor.GREEN + "Passwort gesetzt ;)");
					}
					
					@Override
					public void addPlayer(String playername) {
						shifteds.add(playername);
						preChatAction(playername);
					}
				};
				@Override
				public TimeShifter getTimeShifter() {
					return shifter;
				}
				
				@Override
				public void action(String p_n) {}
				void msg(String playername, String msg){
					Player p = Bukkit.getServer().getPlayer(playername);
					if(p != null)
						p.sendMessage(msg);
				}
				@Override
				public Location getLocation() {
					return loc;
				}

				@Override
				public Location getTrigger() {
					return trigger;
				}
			});
			break;
		}
	}
	public static String hash(String str){
		try {
			MessageDigest mg = MessageDigest.getInstance("SHA-1");
			byte[] result = mg.digest(str.getBytes());
			StringBuffer sb = new StringBuffer();
			for(int i = 0; i < result.length; i++){
				sb.append(Integer.toString((result[i] & 0xff) + 0x100, 16).substring(1));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException e) {
			BlockReichTools.severe("SHA-1 Not found.");
			return "";
		}
	}
}
