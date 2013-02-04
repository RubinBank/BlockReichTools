package me.criztovyl.blockreichtools.tools;

import me.criztovyl.blockreichtools.timeshift.TimeShiftType;
import me.criztovyl.clicklesssigns.ClicklessSign;
import me.criztovyl.clicklesssigns.ClicklessSigns;
import me.criztovyl.clicklesssigns.ClicklessSigns.SignPos;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class Tools {
	public static void msg(String p_n, String msg){
		Bukkit.getPlayer(p_n).sendMessage(msg);
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
}
