package me.criztovyl.blockreichtools;


import me.criztovyl.blockreichtools.timeshift.TimeShift;
import me.criztovyl.blockreichtools.tools.MySQL;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class Listeners implements Listener{
	@EventHandler
	public void onChat(AsyncPlayerChatEvent evt){
		TimeShift.TimeShiftChatEvent(evt);
	}
	@EventHandler
	public void onJoin(PlayerJoinEvent evt){
		BlockReichTools.info("PlayerJoin " + evt.getPlayer().getName());
		if(MySQL.inDB(evt.getPlayer().getName())){
			MySQL.updateUserLogin(evt.getPlayer().getName());
		}
		else{
			MySQL.addUser(evt.getPlayer().getName());
		}
	}
}
