package me.criztovyl.blockreichtools.timeshift;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.criztovyl.blockreichtools.tools.MySQL;
import me.criztovyl.blockreichtools.tools.Tools;

import org.bukkit.ChatColor;
import org.bukkit.event.player.AsyncPlayerChatEvent;
/**
 * This is the TimeShift class. TimeShift disables the Chat and asks the Player a question. Then TimeShift saves the answer and executes the proper method. Later will be moved to ClicklessSigns
 * @author criztovyl
 *
 */
public class TimeShift {
	private static ArrayList<String> shiftedPlayers = new ArrayList<String>();
	private static Map<String, TimeShiftType> playerType = new HashMap<String, TimeShiftType>();
	/**
	 * Adds a Player to TimeShift
	 * @param p_n - The Player Name.
	 * @param type - The TimeShift Type.
	 */
	public static void addShifted(String p_n, TimeShiftType type) {
		shiftedPlayers.add(p_n);
		playerType.put(p_n, type);
		msg(p_n, ChatColor.ITALIC + "" + ChatColor.YELLOW + "Dein Chat ist jetzt deaktiviert.");
		switch(type){
		case CHOOSING:
			msg(p_n, "Aktuell nicht implementiert.");
			removeShifted(p_n);
			break;
		case DEFAULT:
			removeShiftedNoMsg(p_n);
			addShifted(p_n, TimeShiftType.CHOOSING);
			break;
		case SIGN_POS:
			msg(p_n, ChatColor.LIGHT_PURPLE + "Noch nicht Implementiert.");
			removeShifted(p_n);
			break;
		case UCP_PASS:
			msg(p_n, ChatColor.LIGHT_PURPLE + "Bitte gebe jetzt dein Passwort ein:");
			break;
		default:
			msg(p_n, ChatColor.LIGHT_PURPLE + "Noch nicht Implementiert.");
			removeShifted(p_n);
			break;
		}
	}
	/**
	 * Disables the Chat and get the answer.
	 * @param evt
	 */
	public static void TimeShiftChatEvent(AsyncPlayerChatEvent evt){
		//TODO Implement DEFAULT
		String msg = evt.getMessage();
		String p_n = evt.getPlayer().getName();
		if(shiftedPlayers.contains(p_n)){
			if(msg.toLowerCase().equals("exit") || msg.toLowerCase().equals("end") || msg.toLowerCase().equals("ende")){
				removeShifted(p_n);
			}
			if(playerType.containsKey(p_n))
				switch(playerType.get(p_n)){
				case UCP_PASS:
					MySQL.setPassword(p_n, msg);
					msg(p_n, ChatColor.GREEN + "Passwort gesetzt.");
					removeShifted(p_n);
					break;
				}
			evt.setCancelled(true);
		}
	}
	/**
	 * Send a Player a Message
	 * @param p_n the Name
	 * @param msg the Message
	 */
	private static void msg(String p_n, String msg){
		Tools.msg(p_n, msg);
	}
	public static void removeShifted(String p_n){
		shiftedPlayers.remove(p_n);
		if(playerType.containsKey(p_n))
			playerType.remove(p_n);
		Tools.msg(p_n, ChatColor.YELLOW + "Chat reaktiviert.");
	}
	public static void removeShiftedNoMsg(String p_n){
		shiftedPlayers.remove(p_n);
		if(playerType.containsKey(p_n))
			playerType.remove(p_n);
	}
}
