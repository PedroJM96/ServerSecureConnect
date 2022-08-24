package com.pedrojm96.serversecureconnect;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.pedrojm96.core.CoreUtils;



public class SimpleGUI {

	private String title;
	private int slot;
	private Inventory menu;
	
	public SimpleGUI(String title, int row) {
		this.title = title;
		this.slot = getSlot(row);
		this.menu = Bukkit.createInventory(null,this.slot, this.title);
	}
	public SimpleGUI(String title, int row,int glasscolor) {
		this.title = title;
		this.slot = getSlot(row);
		this.menu = Bukkit.createInventory(null,this.slot, this.title);
		int color = glasscolor;
		
		
		
		
		for (int i = 0; i < slot; i++)
		{
			if(CoreUtils.Version.getVersion().esMayorIgual(CoreUtils.Version.v1_13)) {
				String material = "WHITE_STAINED_GLASS_PANE";
				switch(color) {
				case 0:
					material = "WHITE_STAINED_GLASS_PANE";
					break;
				case 1:
					material = "ORANGE_STAINED_GLASS_PANE";
					break;
				case 2:
					material = "MAGENTA_STAINED_GLASS_PANE";
					break;
				case 3:
					material = "LIGHT_BLUE_STAINED_GLASS_PANE";
					break;
				case 4:
					material = "YELLOW_STAINED_GLASS_PANE";
					break;
				case 5:
					material = "LIME_STAINED_GLASS_PANE";
					break;
				case 6:
					material = "PINK_STAINED_GLASS_PANE";
					break;
				case 7:
					material = "GRAY_STAINED_GLASS_PANE";
					break;
				case 8:
					material = "LIGHT_GRAY_STAINED_GLASS_PANE";
					break;
				case 9:
					material = "CYAN_STAINED_GLASS_PANE";
					break;
				case 10:
					material = "PURPLE_STAINED_GLASS_PANE";
					break;
				case 11:
					material = "BLUE_STAINED_GLASS_PANE";
					break;
				case 12:
					material = "BROWN_STAINED_GLASS_PANE";
					break;
				case 13:
					material = "GREEN_STAINED_GLASS_PANE";
					break;
				case 14:
					material = "RED_STAINED_GLASS_PANE";
					break;
				case 15:
					material = "BLACK_STAINED_GLASS_PANE";
					break;
				default:
					material = "WHITE_STAINED_GLASS_PANE";
					  break;
				}
				
				ItemStack it = createItem(" ",material,color);
			    menu.setItem(i, it);
			}else {
				ItemStack it = createItem(" ","STAINED_GLASS_PANE",color);
			    menu.setItem(i, it);
			}	
		}
		
		
	}
	
	public void open(Player p) {
		p.openInventory(menu);
	}
	
	public void setIcon(String name,List<String> lore,String material,int data,int slot) {
		ItemStack it = createItem(name,lore,material,data);
		this.menu.setItem(slot, it);
	}
	
	public void setIcon(String name,String material,int data,int slot) {
		ItemStack it = createItem(name,material,data);
		this.menu.setItem(slot, it);
	}
	
	
	@SuppressWarnings("deprecation")
	public static ItemStack createItem(String name,List<String> lore,String mate,int shrt) {
		ItemStack i = new ItemStack(Material.getMaterial(mate),1,(short)shrt);
		ItemMeta im = i.getItemMeta();
		name = Util.rColor(name);
		im.setDisplayName(name);
		lore = Util.rColorList(lore);
		im.setLore(lore);
		i.setItemMeta(im);
		return i;
	}
	@SuppressWarnings("deprecation")
	public static ItemStack createItem(String name,String mate,int shrt) {
		Material material = Material.valueOf(mate);
		if(material==null) {
			System.out.print("This material "+mate+" is not valid in the "+CoreUtils.Version.getVersion().toString());
		}
		ItemStack i = new ItemStack(material,1,(short)shrt);
		ItemMeta im = i.getItemMeta();
		String n = Util.rColor(name);
		im.setDisplayName(n);
		i.setItemMeta(im);
		return i;
	}
	
	
	public int getSlot(int rows){
		if (rows <= 0) {
	        int s = 9;
	        return s;
	     }else if(rows > 6){
	    	 int s = 54;
	    	return s;
	     }else{
	    	 int s = rows * 9;
	    	 return s;
	     }
	}
	
}
