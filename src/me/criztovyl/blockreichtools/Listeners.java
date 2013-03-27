package me.criztovyl.blockreichtools;


import me.criztovyl.blockreichtools.tools.MySQL;
import me.criztovyl.blockreichtools.tools.SignPos;
import me.criztovyl.blockreichtools.tools.SignType;
import me.criztovyl.blockreichtools.tools.Tools;
import me.criztovyl.clickless.ClicklessPlugin;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
/**
 * Bukkit Listener and Event Stuff
 * @author criztovyl
 *
 */
public class Listeners implements Listener{
	/**
	 * Player Join
	 * Updates last Login.
	 * @param evt
	 */
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
	/**
	 * Checks if a new (click less) Sign is created.
	 * @param evt
	 */
	@EventHandler
	public void onSignChange(SignChangeEvent evt){
		if(evt.getLine(0).toLowerCase().equals("[brt]")){
			evt.setLine(0, ChatColor.GREEN + "[BRT]");
			if(evt.getLine(1).toLowerCase().equals("sign")){
				evt.setLine(1, ChatColor.GREEN + "Sign");
				evt.getPlayer().sendMessage("Its a Sign");
				if(evt.getLine(2).equals(SignType.UCP_PASS.toString())){
					if(evt.getLine(3).toUpperCase().equals("DOWN") || evt.getLine(3).toUpperCase().equals("UP")){
						Tools.addSign(evt.getBlock().getLocation(), SignPos.valueOf(evt.getLine(3).toUpperCase()), SignType.valueOf(evt.getLine(2)));
						evt.setLine(3, ChatColor.GREEN + evt.getLine(3));
					}
					else{
						Tools.msg(evt.getPlayer().getName(), "Auslöser ungültig.");
					}
				}
				else{
					Tools.msg(evt.getPlayer().getName(), "Typ ungültig.");
				}
			}
		}
	}
	/**
	 * Checks if a broken Block is/was a (click less) Sign
	 * @param evt
	 */
	@EventHandler
	public void onBlockBreak(BlockBreakEvent evt){
		if(evt.getBlock().getType().equals(Material.SIGN) || evt.getBlock().getType().equals(Material.WALL_SIGN)
				|| evt.getBlock().getType().equals(Material.SIGN_POST)  ){
			Sign sign = (Sign) evt.getBlock().getState();
			if(sign.getLine(0).toLowerCase().equals("[brt]")){
				if(sign.getLine(1).toLowerCase().equals("sign")){
					ClicklessPlugin.getClickless().removeClicklessSign(evt.getBlock().getLocation());
					MySQL.removeSign(evt.getBlock().getLocation());
				}
			}
		}
	}
}
