package com.pedrojm96.serversecureconnect;

import com.pedrojm96.core.CoreConfig;

public class AllString {
	public static String prefix;
	public static String no_console;
	public static String no_permission;
	
	public static String cancel_message;
	public static String cancel_move_message;
	public static String countdown_message;
	public static String accept_message;
	public static String connec_message;
	public static String no_version_message;
	
	
	public static String cancel_commands_message;

	
	
	public static void load(CoreConfig config,CoreConfig messages) {
		prefix = config.getString("prefix");
		no_console = messages.getString("no-console");
		no_permission = messages.getString("no-permissions");
		
		cancel_message = messages.getString("cancel-message");
		cancel_move_message = messages.getString("cancel-move-message");
		countdown_message = messages.getString("countdown-message");
		accept_message = messages.getString("accept-message");
		connec_message = messages.getString("connec-message");
		no_version_message = messages.getString("no-version-message");
		
		cancel_commands_message = messages.getString("cancel-commands-message");
		
	}
}
