package com.pedrojm96.serversecureconnect;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;



public class Util {

	public static int m = 0;
	
	public static String rColor(String nonColoredText) {
        String coloredText = ChatColor.translateAlternateColorCodes('&', nonColoredText);
        return coloredText;
    }
	
	public static boolean isdouble(String s){
		try{
			@SuppressWarnings("unused")
			double i = Double.parseDouble(s);
			return true;
		}
		catch(NumberFormatException er){
			return false;
		}
		
	}
	
	//Da Formato de colores a los Textos
	public static List<String> rColorList(List<String> ss) {
			List<String> s = new ArrayList<String>();
			s.addAll(ss);
			for (int i = 0; i < s.size(); i++) {
	             String p = ChatColor.translateAlternateColorCodes('&', s.get(i));
	             s.set(i, p);
	        }
	        return s;
		}
	
	
}
