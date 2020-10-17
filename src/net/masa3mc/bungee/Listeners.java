package net.masa3mc.bungee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.Favicon;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.ServerPing.PlayerInfo;
import net.md_5.bungee.api.ServerPing.Players;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

public class Listeners implements Listener {

    private final BungeeCord bungee = BungeeCord.getInstance();
    private final HashMap<String, Integer> server_players = new HashMap<>();
    private final HashMap<String, String> target = new HashMap<>();
    private final List<String> login = new ArrayList<>();

    @SuppressWarnings("deprecation")
    @EventHandler
    public void onProxyPing(ProxyPingEvent event) {
        try {
            for (ServerInfo server : bungee.getServers().values()) {
                server_players.put(server.getName(), server.getPlayers().size());
            }
            Configuration c = Masa3MC.getConfig();
            ArrayList<PlayerInfo> aa = new ArrayList<>();
            for (String strs : c.getStringList("MOTD.PlayerList")) {
                strs = strs.replaceAll("%lobby%", "" + ps("Lobby")).replaceAll("%minigame%", "" + ps("Minigame"))
                        .replaceAll("%creative%", "" + ps("Creative")).replaceAll("%survival%", "" + ps("Survival"))
                        .replaceAll("%pvp%", "" + ps("PvP")).replaceAll("%athletic%", "" + ps("Athletic"))
                //.replaceAll("%BungeeCordに登録しているサーバー名%", "" + ps("BungeeCordに登録しているサーバー名"))
                ;
                aa.add(new PlayerInfo(color(strs), "0"));
            }
            //PlayerInfo[] playerinfo = aa.toArray(new PlayerInfo[aa.size()]);
            PlayerInfo[] playerinfo = aa.toArray(new PlayerInfo[0]);
            ServerPing sp = event.getResponse();
            sp.setDescription(Conf.MOTD_OK);
            sp.setPlayers(new Players(c.getInt("MOTD.maxplayers", 100), bungee.getOnlineCount(), playerinfo));
            sp.getModinfo().setType(c.getString("MOTD.ModInfo"));
            Favicon favicon = bungee.config.getFaviconObject();
            if (favicon != null) sp.setFavicon(favicon.getEncoded());
            event.setResponse(sp);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    public void a(ServerConnectedEvent event) {
        ProxiedPlayer player = event.getPlayer();
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

    @EventHandler
    public void onConnect(ServerConnectEvent event) {
        target.put(event.getPlayer().getName(), event.getTarget().getName());
    }

    @EventHandler
    public void onLogin(PostLoginEvent event) {
        login.add(event.getPlayer().getName());
        bungee.getScheduler().schedule(Masa3MC.instance, () -> login.remove(event.getPlayer().getName()), 4, TimeUnit.SECONDS);
    }

    private int ps(String server) {
        return server_players.get(server) == null ? 0 : server_players.get(server);
    }

    private String color(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
