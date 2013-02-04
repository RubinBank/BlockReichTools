package me.criztovyl.blockreichtools;


import me.criztovyl.blockreichtools.timeshift.TimeShift;
import me.criztovyl.blockreichtools.timeshift.TimeShiftType;
import me.criztovyl.blockreichtools.tools.MySQL;
import me.criztovyl.blockreichtools.tools.Tools;
import me.criztovyl.clicklesssigns.ClicklessSigns;
import me.criztovyl.clicklesssigns.ClicklessSigns.SignPos;
import me.criztovyl.clicklesssigns.tools.LocationTools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
/**
 * Bukkit Listener and Event Stuff
 * @author criztovyl
 *
 */
public class Listeners implements Listener{
	/**
	 * The Chat Event for TimeShift
	 * @param evt
	 */
	@EventHandler
	public void onChat(AsyncPlayerChatEvent evt){
		TimeShift.TimeShiftChatEvent(evt);
	}
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
			if(evt.getLine(1).toLowerCase().equals("sign")){
				evt.getPlayer().sendMessage("Its a Sign");
				if(evt.getLine(2).equals(TimeShiftType.CHOOSING.toString()) || evt.getLine(2).equals(TimeShiftType.UCP_PASS.toString()) ||
						evt.getLine(2).equals(TimeShiftType.SIGN_POS.toString())){
					if(evt.getLine(3).toUpperCase().equals(SignPos.DOWN.toString()) || evt.getLine(3).toUpperCase().equals(SignPos.UP.toString()))
						Tools.addSign(evt.getBlock().getLocation(), SignPos.valueOf(evt.getLine(3).toUpperCase()), TimeShiftType.valueOf(evt.getLine(2)));
					
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
					ClicklessSigns.removeSign(evt.getBlock().getLocation());
					MySQL.removeSign(evt.getBlock().getLocation());
				}
			}
		}
	}
	/**
	 * If a Player is in TimeShift, drop if went away from a (click less) Sign
	 * @param evt
	 */
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent evt){
		Location from = LocationTools.simplify(evt.getFrom());
		Location to = LocationTools.simplify(evt.getTo());
		if(!from.equals(to)){
			if(ClicklessSigns.isClicklessSignTrigger(from)){
				TimeShift.removeShifted(evt.getPlayer().getName());
			}
		}
		
	}
}
