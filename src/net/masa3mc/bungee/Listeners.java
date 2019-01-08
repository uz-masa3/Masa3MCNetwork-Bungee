package net.masa3mc.bungee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.masa3mc.bungee.gson.CountryJson;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerDisconnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class Listeners implements Listener {

	private final BungeeCord bungee = BungeeCord.getInstance();
	private HashMap<String, Integer> server_players = new HashMap<>();
	private HashMap<String, String> target = new HashMap<>();
	private List<String> login = new ArrayList<>();
	private Util util = new Util();
	// Country check
	private HashMap<String, String> CountryCache = new HashMap<>();
	private List<String> ignoreCheck = new ArrayList<>();

	public Listeners() {
		ignore();
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onProxyPing(ProxyPingEvent event) {
		for (ServerInfo server : bungee.getServers().values()) {
			server_players.put(server.getName(), server.getPlayers().size());
		}
		PlayerInfo[] playerinfo = new PlayerInfo[] { new PlayerInfo(color("&7--------------------------"), "0"),
				new PlayerInfo(color("&bWelcome to Masa3MCNetwork!"), "0"),
				new PlayerInfo(color("&eVer&9: &e1.8 - 1.12.2"), "0"),
				new PlayerInfo(color("&7--------------------------"), "0"), new PlayerInfo(color("&5Servers&9:"), "0"),
				new PlayerInfo(color("&7  - Lobby(&6" + ps("Lobby") + "&7)"), "0"),
				new PlayerInfo(color("&7  - Minigame(&6" + ps("Minigame") + "&7)"), "0"),
				new PlayerInfo(color("&7  - Creative(&6" + ps("Creative") + "&7)"), "0"),
				new PlayerInfo(color("&7  - Survival(&6" + ps("Survival") + "&7)"), "0"),
				new PlayerInfo(color("&7  - PvP(&6" + ps("PvP") + "&7)"), "0"), };
		ServerPing sp = event.getResponse();
		String virtual = event.getConnection().getVirtualHost() + "".toLowerCase();
		if (virtual.equalsIgnoreCase("play.masa3mcnetwork.com:25565")) {
			sp.setDescription(Conf.MOTD_OK);
		} else if (virtual.equalsIgnoreCase("play.masa3mcnet.work:25565")) {
			sp.setDescription(Conf.MOTD_OK);
		} else {
			sp.setDescription(Conf.MOTD_BAD);
		}
		sp.setPlayers(new Players(100, bungee.getOnlineCount(), playerinfo));
		sp.getModinfo().setType("VANILLA");
		// sp.setFavicon(bungee.config.getFaviconObject().getEncoded());
		event.setResponse(sp);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void a(ServerConnectedEvent event) {
		ProxiedPlayer player = event.getPlayer();
		util.updateDelay(750, TimeUnit.MILLISECONDS);
		if (login.contains(player.getName())) {
			login.remove(player.getName());
			for (ProxiedPlayer players : bungee.getPlayers()) {
				players.sendMessage(Conf.replace(Conf.CONNECT, player.getName(), ""));
			}
		} else {
			for (ProxiedPlayer players : bungee.getPlayers()) {
				players.sendMessage(Conf.replace(Conf.SWITCH, player.getName(), target.get(player.getName())));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onConnect(ServerConnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		String target_ = event.getTarget().getName();
		if (target_.equalsIgnoreCase("Survival") || target_.equalsIgnoreCase("Event")) {
			if (player.getPendingConnection().getVersion() >= 340) {
				target.put(player.getName(), target_);
			} else {
				player.sendMessage(color("&c" + target_ + "サーバーには1.12.2以上でないと入れません"));
				event.setCancelled(true);
			}
		} else {
			target.put(player.getName(), target_);
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onDisconnect(ServerDisconnectEvent event) {
		ProxiedPlayer player = event.getPlayer();
		if (player.getServer() == null && event.getTarget() != null) {
			util.updateDelay(500, TimeUnit.MILLISECONDS);
			for (ProxiedPlayer players : bungee.getPlayers()) {
				players.sendMessage(Conf.replace(Conf.DISCONNECT, player.getName(), ""));
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onLogin(PostLoginEvent event) {
		ProxiedPlayer player = event.getPlayer();
		if (Masa3MC.isCheckCountry()) {
			String ip = player.getAddress().toString().split("/")[1].split(":")[0];
			if (!(ip.equals("127.0.0.1") || ip.startsWith("192.168.") || ip.startsWith("10.")
					|| ip.startsWith("172.31.")) && !ignoreCheck.contains(player.getUUID())) {
				String country = "";
				if (CountryCache.size() > 30) {
					CountryCache.clear();
				}
				if (CountryCache.containsKey(player.getName())) {
					country = CountryCache.get(player.getName());
				} else {
					country = ((CountryJson) Util.getCountry(ip)).country_name;
					CountryCache.put(player.getName(), country);
				}
				if (!country.equalsIgnoreCase("japan")) {
					player.disconnect(new Util().color("&cYour country is not whitelisted!!"));
				}
			}
		}
		int protocol = player.getPendingConnection().getVersion();
		if (protocol < 47) {
			player.disconnect(new Util().color("&c対応バージョンは1.8.8〜となっております。"));
			return;
		}
		login.add(event.getPlayer().getName());
		bungee.getScheduler().schedule(Masa3MC.instance, new Runnable() {
			@Override
			public void run() {
				login.remove(event.getPlayer().getName());
			}
		}, 4, TimeUnit.SECONDS);
	}

	private int ps(String server) {
		return server_players.get(server) == null ? 0 : server_players.get(server);
	}

	private String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	private void ignore() {
		ignoreCheck.add("b6974f90-17a5-4563-828f-bc4b76072c34");
		ignoreCheck.add("c659d7eb-0be7-439d-b870-0ec9789caac6");
		ignoreCheck.add("4c49f95c-d6b4-4b6b-b6a7-12323b876309");
		ignoreCheck.add("2b23d76b-adba-4e31-a76a-6fdc4440155d");
		ignoreCheck.add("f7483356-e688-4097-bdad-a0ca8c302b66");
		ignoreCheck.add("e1570024-966d-4447-a8d5-09e2fea927ed");
		ignoreCheck.add("32a93b11-dbdc-417d-b779-a880a5af30a3");
		ignoreCheck.add("bd5739b8-bf8f-4201-811e-0678abc5bdf9");
		ignoreCheck.add("74b095e5-4a74-4750-9d57-ff375851f4b0");
		ignoreCheck.add("de466327-bc25-4baf-8e70-245756e13246");
		ignoreCheck.add("001f8a59-9f83-4857-97f2-8ca86e4146dd");
		ignoreCheck.add("e6de603f-06d4-40b2-877c-7b1a3edd49f9");
		ignoreCheck.add("3ec8a319-0a6d-411d-b14b-8f7be22630b2");
		ignoreCheck.add("0ff24519-199c-491f-a547-163cd734459d");
		ignoreCheck.add("d52e8b32-32c1-45f8-b8f8-a332e261b2e2");
		ignoreCheck.add("d4271b36-70b3-4d02-955b-c141dc3d617b");
		ignoreCheck.add("4f1e0b0c-4dd6-466e-ac0a-053e65ddf38e");
		ignoreCheck.add("3ed40110-5aab-4d1d-a7f5-cc28312d85f7");
		ignoreCheck.add("535cd86a-372c-40e2-a5ef-d30cf7a619e3");
	}

}
