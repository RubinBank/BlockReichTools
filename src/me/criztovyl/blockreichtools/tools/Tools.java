package me.criztovyl.blockreichtools.tools;

import org.bukkit.Bukkit;

public class Tools {
	public static void msg(String p_n, String msg){
		Bukkit.getPlayer(p_n).sendMessage(msg);
	}
}
