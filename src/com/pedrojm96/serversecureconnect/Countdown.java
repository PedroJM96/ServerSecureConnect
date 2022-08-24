package com.pedrojm96.serversecureconnect;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.pedrojm96.core.CoreColor;

public class Countdown extends BukkitRunnable {

	private Player player;
	private ServerSecureConnect plugin;
	private int countdown;
	
	public Countdown(int countdown, Player player,ServerSecureConnect plugin) {
		this.player = player;
		this.plugin = plugin;
		this.countdown = countdown;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(this.player==null) {
			plugin.timers.remove(player);
			this.cancel();
		}
		
		if(!this.player.isOnline()) {
			plugin.timers.remove(player);
			this.cancel();
		}
		
		if(!this.plugin.esperaServer.containsKey(player)) {
			plugin.timers.remove(player);
			this.cancel();
		}
		
		if(countdown<=0) {
			plugin.timers.remove(player);
			CoreColor.message(player, AllString.prefix+AllString.connec_message.replaceAll("<server>", this.plugin.esperaServer.get(player)));
			ByteArrayDataOutput out = ByteStreams.newDataOutput();
			out.writeUTF("Connect");
			out.writeUTF(this.plugin.esperaServer.get(player));
			this.plugin.esperaServer.remove(player);
			player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray());
			this.cancel();
		}else {
			CoreColor.message(player, AllString.prefix+AllString.countdown_message.replaceAll("<server>", this.plugin.esperaServer.get(player)).replaceAll("<countdown>", String.valueOf(countdown))    );
			countdown--;
		}
		
	}

}
