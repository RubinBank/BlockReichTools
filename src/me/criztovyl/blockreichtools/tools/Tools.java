package me.criztovyl.blockreichtools.tools;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import me.criztovyl.blockreichtools.BlockReichTools;
import me.criztovyl.blockreichtools.timeshift.TimeShiftType;
import me.criztovyl.clicklesssigns.ClicklessSign;
import me.criztovyl.clicklesssigns.ClicklessSigns;
import me.criztovyl.clicklesssigns.ClicklessSigns.SignPos;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Tools {
	/**
	 * @see org.bukkit.command.CommandSender.sendMessage()
	 */
	public static void msg(String p_n, String msg){
		if(Bukkit.getServer().getPlayer(p_n) != null)
		Bukkit.getServer().getPlayer(p_n).sendMessage(msg);
	}
	public static void addSign(Location loc, SignPos pos, TimeShiftType t){
		MySQL.addSignToDB(loc, pos, t);
		switch(t){
		case CHOOSING:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				
				@Override
				public void action(String arg0) {
					me.criztovyl.blockreichtools.timeshift.TimeShift.addShifted(arg0, me.criztovyl.blockreichtools.timeshift.TimeShiftType.CHOOSING);
				}
			});
			break;
		case UCP_PASS:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				
				@Override
				public void action(String arg0) {
					me.criztovyl.blockreichtools.timeshift.TimeShift.addShifted(arg0, me.criztovyl.blockreichtools.timeshift.TimeShiftType.UCP_PASS);
				}
			});
			break;
		case DEFAULT:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				
				@Override
				public void action(String arg0) {
					me.criztovyl.blockreichtools.timeshift.TimeShift.addShifted(arg0, me.criztovyl.blockreichtools.timeshift.TimeShiftType.DEFAULT);
				}
			});
			break;
		default:
			ClicklessSigns.addSign(loc, pos, new ClicklessSign() {
				
				@Override
				public void action(String arg0) {
					me.criztovyl.blockreichtools.timeshift.TimeShift.addShifted(arg0, me.criztovyl.blockreichtools.timeshift.TimeShiftType.DEFAULT);
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
