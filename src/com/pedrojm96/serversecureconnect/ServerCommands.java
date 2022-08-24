package com.pedrojm96.serversecureconnect;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pedrojm96.core.CoreColor;
import com.pedrojm96.core.CoreUtils;
import com.pedrojm96.core.CoreViaVersion;
import com.pedrojm96.core.command.CorePluginCommand;


public class ServerCommands extends CorePluginCommand{

	private ServerSecureConnect plugin;
	
	private String command = null;
	
	public ServerCommands(ServerSecureConnect plugin,String cmd){
		this.plugin = plugin;
		this.command = cmd;
		plugin.log.info("Register command /"+cmd);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, String command, String[] args) {
		// TODO Auto-generated method stub
		if (!(sender instanceof Player)) {
			CoreColor.message(sender,AllString.prefix + AllString.no_console);
       	 	return true;
		}
		Player player = (Player)sender;
		
		if(this.plugin.timers.containsKey(player)) {
			Integer timerID = (Integer)this.plugin.timers.remove(player);
		    if (timerID != null) {
		    	Bukkit.getScheduler().cancelTask(timerID.intValue());
		    }
		}
		
		if(this.plugin.esperaServer.containsKey(player)) {
			this.plugin.esperaServer.remove(player);
		}
		
		if(  this.plugin.getConfig().getBoolean("server."+this.command+".version-check") && CoreViaVersion.Setup() ) {
			int playerversion = CoreViaVersion.getPlayerClientVersion(player);
			boolean vali=false;
			List<String> soporte = this.plugin.config.getStringList("server."+this.command+".version-list");
			for(String realversion : soporte) {
				 int protocolversion = CoreViaVersion.getProtocolVersion(realversion);
				 if(protocolversion==playerversion) {
					 this.plugin.log.debug(protocolversion+"="+playerversion);
					 vali = true;
					 break;
				 }
			 }
			
			if(vali) {
				
				if(this.plugin.config.getBoolean("confirmation-menu")) {
					this.plugin.esperaServer.put(player, this.command);
					SimpleGUI menu = new SimpleGUI(CoreColor.colorCodes(this.plugin.config.getString("menu-title").replaceAll("<server>", this.command)) ,3,15);
					String mate_data = this.getMaterialData("item-accept");
					menu.setIcon(CoreColor.colorCodes(this.plugin.config.getString("item-accept.name")),  this.getMaterial(mate_data), this.getData(mate_data), 12);
					mate_data = this.getMaterialData("item-confirm");
					menu.setIcon(CoreColor.colorCodes(this.plugin.config.getString("item-confirm.name")), this.getMaterial(mate_data), this.getData(mate_data), 13);
					mate_data = this.getMaterialData("item-cancel");
					menu.setIcon(CoreColor.colorCodes(this.plugin.config.getString("item-cancel.name")),  this.getMaterial(mate_data), this.getData(mate_data), 14);
					menu.open(player);
				}else {
					CoreColor.message(player, AllString.prefix+AllString.connec_message.replaceAll("<server>", this.command));
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Connect");
					out.writeUTF(this.command);
					
					player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray());
				}
				
			}else {
				String lisversion = "(";
				for(String version : soporte) {
					if(lisversion=="(") {
						lisversion = lisversion+version;
					}else {
						lisversion = lisversion+", "+version;
					}	
				}
				lisversion = lisversion+")";
				CoreColor.message(player, AllString.prefix +AllString.no_version_message.replaceAll("<version>", lisversion));
			}
		}else if(this.plugin.config.getBoolean("confirmation-menu")) {
				this.plugin.esperaServer.put(player, this.command);
				SimpleGUI menu = new SimpleGUI(CoreColor.colorCodes(this.plugin.config.getString("menu-title").replaceAll("<server>", this.command)) ,3,15);
				String mate_data = this.getMaterialData("item-accept");
				menu.setIcon(CoreColor.colorCodes(this.plugin.config.getString("item-accept.name")),  this.getMaterial(mate_data), this.getData(mate_data), 12);
				mate_data = this.getMaterialData("item-confirm");
				menu.setIcon(CoreColor.colorCodes(this.plugin.config.getString("item-confirm.name")), this.getMaterial(mate_data), this.getData(mate_data), 13);
				mate_data = this.getMaterialData("item-cancel");
				menu.setIcon(CoreColor.colorCodes(this.plugin.config.getString("item-cancel.name")),  this.getMaterial(mate_data), this.getData(mate_data), 14);
				menu.open(player);
		}else {	
				CoreColor.message(player, AllString.prefix+AllString.connec_message.replaceAll("<server>", this.command));
				ByteArrayDataOutput out = ByteStreams.newDataOutput();
				out.writeUTF("Connect");
				out.writeUTF(this.command);
				player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray());
		}	
		return true;
	}
	
	
	private short getData(String materialData) {
		return Short.valueOf(materialData.contains(":") ? materialData.split(":")[1].trim() : "0" );
	}
	
	private String getMaterial(String materialData) {
		if(Material.getMaterial(materialData.contains(":") ? materialData.split(":")[0].trim() : materialData) ==null) {
			plugin.getLog().error(" The gui has an invalid item Material: " + (materialData.contains(":") ? materialData.split(":")[0].trim() : materialData) + ".");
			return "STONE";
		}
		return materialData.contains(":") ? materialData.split(":")[0].trim() : materialData;
	}
	
	
	private String getMaterialData(String path) {
		String mate_item_confirm;
		if(CoreUtils.Version.getVersion().esMayorIgual(CoreUtils.Version.v1_13)) {
			if(this.plugin.config.isSet(path+".material")) {
				mate_item_confirm = this.plugin.config.getString(path+".material");
			}else {
				mate_item_confirm = this.plugin.config.getString(path+".material-old");
			}
		}else {
			if(this.plugin.config.isSet(path+".material-old")) {
				mate_item_confirm = this.plugin.config.getString(path+".material-old");
			}else {
				mate_item_confirm = this.plugin.config.getString(path+".material");
			}
			
			
			
		}
		return mate_item_confirm;
	}
	
	
	@Override
	public String getPerm() {
		// TODO Auto-generated method stub
		return "server."+this.command;
	}


	@Override
	public String getErrorNoPermission() {
		// TODO Auto-generated method stub
		return AllString.prefix + AllString.no_permission;
	}

	@Override
	public List<String> onCustomTabComplete(CommandSender sender, List<String> list, String[] args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.command;
	}

	@Override
	public List<String> getAliases() {
		// TODO Auto-generated method stub
		return new ArrayList<String>();
	}

	@Override
	public String getUsage() {
		// TODO Auto-generated method stub
		return "/"+this.command;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return "Server Secure Connect /"+this.command+" commands";
	}

	

}
