package me.criztovyl.blockreichtools.timeshift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.criztovyl.blockreichtools.tools.MySQL;
import me.criztovyl.blockreichtools.tools.Tools;

import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class TimeShift {
	private static ArrayList<String> shiftedPlayers = new ArrayList<String>();
	private static Map<String, TimeShiftType> playerType = new HashMap<String, TimeShiftType>();

	public static void addShiftedBankomat(String p_n, TimeShiftType type) {
		shiftedPlayers.add(p_n);
		playerType.put(p_n, type);
		if(type.equals(TimeShiftType.UCP_PASS))
			msg(p_n, ChatColor.LIGHT_PURPLE + "Bitte gebe jetzt dein Passwort ein: ");
	}
	public static void TimeShiftChatEvent(AsyncPlayerChatEvent evt){
		String msg = evt.getMessage();
		String p_n = evt.getPlayer().getName();
		if(shiftedPlayers.contains(p_n)){
			if(msg.toLowerCase().equals("exit") || msg.toLowerCase().equals("end") || msg.toLowerCase().equals("ende")){
				removeShifted(p_n);
			}
			if(playerType.containsKey(p_n))
				if(playerType.get(p_n).equals(TimeShiftType.UCP_PASS)){
					evt.setCancelled(true);
					MySQL.setPassword(p_n, msg);
					removeShifted(p_n);
				}
		}
	}
	private static void msg(String p_n, String msg){
		Tools.msg(p_n, msg);
	}
	public static void removeShifted(String p_n){
		shiftedPlayers.remove(p_n);
		if(playerType.containsKey(p_n))
			playerType.remove(p_n);
		Tools.msg(p_n, ChatColor.YELLOW + "Char reaktivier.");
	}
}
