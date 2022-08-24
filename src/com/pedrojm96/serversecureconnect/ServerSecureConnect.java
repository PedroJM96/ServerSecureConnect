package com.pedrojm96.serversecureconnect;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pedrojm96.core.CoreColor;
import com.pedrojm96.core.CoreConfig;
import com.pedrojm96.core.CoreLog;
import com.pedrojm96.core.CorePlugin;
import com.pedrojm96.core.CoreSpigotUpdater;
import com.pedrojm96.core.CoreViaVersion;
import com.pedrojm96.core.command.CoreCommands;





public class ServerSecureConnect extends JavaPlugin implements CorePlugin, Listener, PluginMessageListener{
	
	public String[] servers = null;
	
	public CoreConfig config;
	public CoreConfig configMessages;
	public CoreLog log;
	public boolean setupserver;
	
	public boolean bungeeCord;
	
	public HashMap<Player, Integer> timers = new HashMap<Player, Integer>();
	
	
	public static ServerSecureConnect plugin;
	public  Map<Player, String> esperaServer =  new HashMap<Player, String>();
	
	public void onEnable() {
		
		plugin = this;
		this.log = new CoreLog(this,CoreLog.Color.YELLOW);
		this.log.line();
		this.log.info("&7Plugin Create by  PedroJM96.");
		this.log.info("&7Loading configuration...");
		this.config = new CoreConfig(this,"config",this.log,this.getResource("config.yml"),true);
		log.seDebug(this.config.getBoolean("debug"));
		this.loadMessages();
		AllString.load(this.config, this.configMessages);
		
		try {
			bungeeCord = Class.forName("org.spigotmc.SpigotConfig").getDeclaredField("bungee").getBoolean(null);
		} catch (Exception ex) {
			log.error("This Plugin work only on the spigot server with bungeecod set to true, please enabled bungeecord mode.");
			getServer().getPluginManager().disablePlugin(this);   
			return;
		}
		if(bungeeCord) {
			this.log.info("Registering channel BungeeCord");
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		}else {
			log.error("This Plugin work only on the spigot server with bungeecod set to true, please enabled bungeecord mode.");
			getServer().getPluginManager().disablePlugin(this);   
			return;
		}
		
		this.getServer().getPluginManager().registerEvents(this, this);
		
		if(CoreViaVersion.Setup()) {
			log.alert("Hooked ViaVersion");
		}
		@SuppressWarnings("unused")
		Metrics metrics = new Metrics(this,15983);	
		checkForUpdates();
		this.log.line();
	}
	
	
	
	
	@Override
    public void onDisable() {
        this.getServer().getMessenger().unregisterIncomingPluginChannel(this, "BungeeCord");
       
        for(Player player : timers.keySet()) {
        	Integer timerID = (Integer)this.timers.remove(player);
		    if (timerID != null) {
		    	Bukkit.getScheduler().cancelTask(timerID.intValue());
		    }
        }
        
        for(Player player : esperaServer.keySet()) {
        	this.esperaServer.remove(player); 
        }
    }
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player player = e.getPlayer();
		new BukkitRunnable()
	    {
	      public void run()
	      {
	    	  if(player!=null && player.isOnline()) {
	  			if(!setupserver) {
	  				ByteArrayDataOutput out = ByteStreams.newDataOutput();
	  				out.writeUTF("GetServers");
	  				e.getPlayer().sendPluginMessage(getInstance(), "BungeeCord", out.toByteArray());
	  				log.debug("Solicitando datos de los servidores del servidor bungee.");
	  			}				
	  		} 
	       
	      }
	    }.runTaskLater(this, 100L);
		
		
		  
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		this.log.debug("Canal: "+channel);
		if (!channel.equals("BungeeCord")) {
			return;
	    }
		
		if(setupserver) {
			return;
		}
		this.log.debug("Reciviendo datos de los servidores del servidor bungee.");
	    ByteArrayDataInput in = ByteStreams.newDataInput(message);
	    String subchannel = in.readUTF();
	    if (subchannel.equals("GetServers")) {
	    	String[] servers = in.readUTF().split(", ");
	    	if(servers!=null) {
	    		setupserver = true;
		    	this.log.info("Register server commands...");
		    	for(String servercommand : servers) {
		    		this.config.add("server."+servercommand+".version-check", true);
		    		this.config.add("server."+servercommand+".version-list", Arrays.asList("1.8.8","1.8.9","1.9","1.9.1","1.9.2","1.9.3","1.9.4","1.10","1.10.1","1.10.2","1.11","1.11.1","1.11.2","1.12","1.12.1","1.12.2","1.13","1.13.1","1.13.2","1.14","1.14.1","1.14.2","1.14.3","1.14.4","1.15.2","1.15.1","1.15.1","1.15.2","1.16","1.16.1","1.16.2","1.16.3","1.16.4","1.16.5","1.17","1.17.1","1.18","1.18.1","1.18.2","1.19"));
		    		this.config.silenSave();
		    		ServerCommands cm = new ServerCommands(this,servercommand);
		    		CoreCommands.registerCommand(cm, this);
		    	}
	    	}	
	    }
	}
	
	
	
	
	@EventHandler
	public void onMenuClick(InventoryClickEvent e) {
		if(e.isCancelled()){
			return;
	    }
		Player player = (Player)e.getWhoClicked();
		
		if(!esperaServer.containsKey(player)) {
			return;
		}
		
		
		if(e.getView().getTitle().equals( CoreColor.colorCodes(this.config.getString("menu-title").replaceAll("<server>", esperaServer.get(player))))) {
			ItemStack itemInHand = e.getCurrentItem();
			if(itemInHand == null){
					return;
			}
			if(itemInHand.getItemMeta() == null){
				return;
			}

			e.setCancelled(true);
			
			if(itemInHand.getItemMeta().getDisplayName().equals( CoreColor.colorCodes(this.config.getString("item-accept.name")) )){
				e.getWhoClicked().closeInventory();
				if(this.config.getBoolean("countdown-enable")) {
					this.timers.put(player,new Countdown(this.config.getInt("countdown"),player, this).runTaskTimer(this, 0L, 20L).getTaskId());
					CoreColor.message(player, AllString.prefix+AllString.accept_message.replaceAll("<countdown>", String.valueOf(this.config.getInt("countdown")))  );
				}else {
					CoreColor.message(player, AllString.prefix+AllString.connec_message.replaceAll("<server>", esperaServer.get(player)));
					ByteArrayDataOutput out = ByteStreams.newDataOutput();
					out.writeUTF("Connect");
					out.writeUTF(esperaServer.get(player));
					esperaServer.remove(player);
					player.sendPluginMessage(this, "BungeeCord", out.toByteArray());
					
				}
			}
			if(itemInHand.getItemMeta().getDisplayName().equals(CoreColor.colorCodes(this.config.getString("item-cancel.name")))){
				e.getWhoClicked().closeInventory();
				CoreColor.message(player, AllString.prefix + AllString.cancel_message);
				esperaServer.remove(player);
			}
		}
	}
	
	@EventHandler
	public void onMove(PlayerMoveEvent e) {
		if(e.isCancelled()){
			return;
		}
		if(!this.getConfig().getBoolean("countdown-enable")) {
			return;
		}
		
		Player player = e.getPlayer();
		
		if(this.timers.containsKey(player)) {
			Integer timerID = (Integer)this.timers.remove(player);
		    if (timerID != null) {
		    	Bukkit.getScheduler().cancelTask(timerID.intValue());
		    }
			
			esperaServer.remove(player);
			CoreColor.message(player, AllString.prefix + AllString.cancel_move_message.replaceAll("<countdown>", String.valueOf(this.config.getInt("countdown"))));
		}
	}
	
	@EventHandler
	public void onBlockCmds(PlayerCommandPreprocessEvent e) {
		
		
		if(!this.getConfig().getBoolean("countdown-enable")) {
			return;
		}
		
		Player player = e.getPlayer();
		
		if(this.timers.containsKey(player)) {
			Integer timerID = (Integer)this.timers.remove(player);
		    if (timerID != null) {
		    	Bukkit.getScheduler().cancelTask(timerID.intValue());
		    }
		    this.esperaServer.remove(player);
		    CoreColor.message(player, AllString.prefix + AllString.cancel_commands_message.replaceAll("<countdown>", String.valueOf(this.config.getInt("countdown"))));
		}	
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e)
	{
		Player player = e.getPlayer();
		if(this.timers.containsKey(player)) {
			Integer timerID = (Integer)this.timers.remove(player);
		    if (timerID != null) {
		    	Bukkit.getScheduler().cancelTask(timerID.intValue());
		    }
		}
		if(this.esperaServer.containsKey(player)) {
			esperaServer.remove(player);
		}   
	}
	

	public void checkForUpdates() {
		if(config.getBoolean("update-check")){
			new BukkitRunnable() {
				@Override
				public void run() {
					CoreSpigotUpdater updater = new CoreSpigotUpdater(plugin, 53710);
		        	try {
		                if (updater.checkForUpdates()) {
		                	log.alert("An update was found! for PixelLogin. Please update to recieve latest version. download: " + updater.getResourceURL());
		                }	
		            } catch (Exception e) {
		            	
		            	log.error("Failed to check for a update on spigot.");
		            }
				}
        		
        	}.runTask(this);
        	
        	
        } 
    }
	
	
	public void loadMessages(){
		String m = this.config.getString("messages");
		switch(m.toUpperCase()){
		case "EN":
			this.configMessages = new CoreConfig(this,"messages_EN",this.log,this.getResource("messages_EN.yml"),true);
			break;
		case "ES":
			this.configMessages = new CoreConfig(this,"messages_ES",this.log,this.getResource("messages_ES.yml"),true);
			break;
		default:
			this.configMessages = new CoreConfig(this,"messages_EN",this.log,this.getResource("messages_EN.yml"),true);
			break;
		}
	}
	
	

	@Override
	public CoreLog getLog() {
		// TODO Auto-generated method stub
		return this.log;
	}


	@Override
	public JavaPlugin getInstance() {
		// TODO Auto-generated method stub
		return this;
	}
	
	
}
