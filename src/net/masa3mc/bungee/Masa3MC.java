package net.masa3mc.bungee;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.concurrent.TimeUnit;

import com.google.common.io.ByteStreams;

import net.masa3mc.bungee.cmd.C_Masa3MC;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class Masa3MC extends Plugin {

	public static Masa3MC instance;
	private static MySQL mysql;
	private static boolean checkCountry = false;
	private static Configuration configuration;

	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig(false);
		Conf.reload();
		getProxy().getPluginManager().registerListener(this, new Listeners());
		getProxy().getPluginManager().registerCommand(this, new C_Masa3MC());
		mysql = new MySQL();
		try {
			mysql.dataUpdate();
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		getProxy().getScheduler().schedule(this, new Runnable() {
			@Override
			public void run() {
				try {
					mysql.dataUpdate();
				} catch (SQLException | ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}, 20, 10, TimeUnit.SECONDS);
		new Util().updateRepeat(5, TimeUnit.SECONDS);
	}

	public static MySQL getMySQL() {
		return mysql;
	}

	public static boolean isCheckCountry() {
		return checkCountry;
	}

	public static Configuration getConfig() {
		return configuration;
	}

	public static void loadConfig() {
		try {
			configuration = ConfigurationProvider.getProvider(YamlConfiguration.class)
					.load(new File(instance.getDataFolder() + "/config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void saveDefaultConfig(boolean force) {
		File data = instance.getDataFolder();
		if (!data.exists()) {
			data.mkdirs();
		}
		File f = new File(data + "/config.yml");
		if (!f.exists() || force) {
			try {
				f.createNewFile();
				InputStream is = instance.getResourceAsStream("config.yml");
				try {
					OutputStream os = new FileOutputStream(f);
					ByteStreams.copy(is, os);
				} catch (Throwable throwable) {

				} finally {
					is.close();
				}
			} catch (IOException e) {
				instance.getLogger().severe("Unable to read configuration file!");
				e.printStackTrace();
			}
		}
	}

}
