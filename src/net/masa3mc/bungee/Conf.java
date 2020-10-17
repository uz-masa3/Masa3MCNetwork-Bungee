package net.masa3mc.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class Conf {

	public static String CONNECT = c("&b[Connect]&e: &a%player%&7さんが参加しました");
	public static String DISCONNECT = c("&b[Disconnect]&e: &a%player%&7さんが退出しました");
	public static String SWITCH = c("&b[Switch]&e: &a%player%&7さんが&a%server%サーバー&7に移動しました");
	public static String MOTD_OK = c("&6Masa3MCNetwork &b| &224時間あいてます\\n　　&3HP&c>> &emasa3mc.xyz");

	public static String replace(String string, String player, String server) {
		return string.replace("%player%", player).replace("%server%", server);
	}

	public static void reload() {
		Masa3MC.loadConfig();
		CONNECT = c(conf().getString("message.CONNECT"));
		DISCONNECT = c(conf().getString("message.DISCONNECT"));
		SWITCH = c(conf().getString("message.SWITCH"));
		MOTD_OK = c(conf().getString("message.MOTD_OK"));
	}

	private static Configuration conf() {
		return Masa3MC.getConfig();
	}

	private static String c(String string) {
		return ChatColor.translateAlternateColorCodes('&', string);
	}

}