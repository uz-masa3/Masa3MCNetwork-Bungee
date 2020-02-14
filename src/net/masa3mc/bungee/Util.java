package net.masa3mc.bungee;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;

import net.masa3mc.bungee.gson.CountryJson;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Util {

	private final BungeeCord bungee = BungeeCord.getInstance();
	private final static Masa3MC ins = Masa3MC.instance;
	private final String unique = "plists";
	private final String disp = color("&eMasa3MCNetwork");

	public String color(String str) {
		return ChatColor.translateAlternateColorCodes('&', str);
	}

	public static Configuration getYml(String name) {
		File yml = new File(ins.getDataFolder(), name + ".yml");
		try {
			if (yml.exists()) {
				return ConfigurationProvider.getProvider(YamlConfiguration.class).load(yml);
			} else {
				yml.createNewFile();
				return ConfigurationProvider.getProvider(YamlConfiguration.class).load(yml);

			}
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static CountryJson getCountry(String ip) {
		try {
			URL url = new URL("https://ipapi.co/" + ip + "/json/");
			HttpURLConnection http = (HttpURLConnection) url.openConnection();
			http.setRequestMethod("GET");
			http.setRequestProperty("User-Agent", "Masa3MCNetwork");
			if (http.getResponseCode() == HttpsURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(http.getInputStream(), "UTF-8"));
				CountryJson json = new Gson().fromJson(reader.readLine(), CountryJson.class);
				reader.close();
				return json;
			} else {
				return null;
			}
		} catch (IOException ex) {
			return null;
		}
	}

	public void updateDelay(int delay, TimeUnit unit) {
		bungee.getScheduler().schedule(Masa3MC.instance, new Runnable() {
			@Override
			public void run() {
				update();
			}
		}, delay, unit);
	}

	public void updateRepeat(int repeat, TimeUnit unit) {
		bungee.getScheduler().schedule(Masa3MC.instance, new Runnable() {
			@Override
			public void run() {
				update();
			}
		}, 3, repeat, unit);
	}

	private void update() {
		if (bungee.getPlayers().size() >= 1) {
			for (ProxiedPlayer players : bungee.getPlayers()) {
				String srv = "NULL";
				if (players.getServer() != null && players.getServer().getInfo() != null) {
					srv = players.getServer().getInfo().getName();
				}
				ScoreBoardUtils.removeObject(unique, disp, players);
				if (srv.equalsIgnoreCase("pvp") || srv.equalsIgnoreCase("null")
						|| players.getPendingConnection().getVersion() >= 341) {// 1.12.2を超える
					continue;
				}
				double money = Masa3MC.getMySQL().getPlayersMoney(players.getUniqueId());
				ScoreBoardUtils.createObject(unique, disp, players);

				ScoreBoardUtils.updateScore(color(" &6Server&b:"), unique, 12, players);
				ScoreBoardUtils.updateScore(color(" &e" + srv), unique, 11, players);
				ScoreBoardUtils.updateScore(color(""), unique, 10, players); // 0

				ScoreBoardUtils.updateScore(color(" &6Players&b:"), unique, 9, players);
				ScoreBoardUtils.updateScore(color(" &e" + bungee.getPlayers().size() + "&a/&e100"), unique, 8, players);
				ScoreBoardUtils.updateScore(color(" "), unique, 7, players); // 1

				ScoreBoardUtils.updateScore(color(" &6Ping&b:"), unique, 6, players);
				ScoreBoardUtils.updateScore(color(" " + ping(players)), unique, 5, players);
				ScoreBoardUtils.updateScore(color("  "), unique, 4, players); // 2

				ScoreBoardUtils.updateScore(color(" &6Money&b:"), unique, 3, players);
				ScoreBoardUtils.updateScore(color(" &e" + money + "Ms"), unique, 2, players);
				ScoreBoardUtils.updateScore(color("&b--------------"), unique, 0, players);

				ScoreBoardUtils.display(1, unique, players);

			}
		}

	}

	private String ping(ProxiedPlayer p) {
		int ping = p.getPing();
		return color((ping >= 50 ? (ping >= 100 ? "&c" : "&e") : "&b") + ping + "ms");
	}
}
