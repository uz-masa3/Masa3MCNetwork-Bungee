package net.masa3mc.bungee;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

public class MySQL {

	private HashMap<UUID, Double> datamap = new HashMap<>();
	private Connection connection = null;
	private String host, database, username, password;
	private int port;

	public MySQL() {
		Conf.reload();
		host = Conf.MYSQL_HOST;
		database = Conf.MYSQL_USE_DB;
		username = Conf.MYSQL_USERNAME;
		password = Conf.MYSQL_PASSWORD;
		port = Conf.MYSQL_PORT;
	}

	public void openConnection() throws SQLException, ClassNotFoundException {
		if (connection != null && !connection.isClosed()) {
			return;
		}
		synchronized (this) {
			if (connection != null && !connection.isClosed()) {
				return;
			}
			Class.forName("com.mysql.jdbc.Driver");
			String s = "jdbc:mysql://" + host + ":" + port + "/" + database;
			connection = DriverManager.getConnection(s, username, password);
		}
	}

	public void dataUpdate() throws SQLException, ClassNotFoundException {
		datamap.clear();
		openConnection();
		Statement statement = connection.createStatement();
		statement.executeQuery("use " + database);

		HashMap<String, Integer> d1 = new HashMap<>();
		HashMap<Integer, Double> d2 = new HashMap<>();
		ResultSet uuid_id = statement.executeQuery("select * from jecon_account");
		while (uuid_id.next()) {
			d1.put(uuid_id.getString("uuid"), uuid_id.getInt("id"));
		}
		uuid_id.close();
		ResultSet id_balance = statement.executeQuery("select * from jecon_balance");
		while (id_balance.next()) {
			d2.put(id_balance.getInt("id"), id_balance.getDouble("balance"));
		}
		id_balance.close();

		d1.forEach((uuid, id) -> {
			d2.forEach((id2, balance) -> {
				if (id == id2) {
					datamap.put(UUID.fromString(uuid), balance);
				}
			});
		});
	}

	public double getPlayersMoney(UUID uuid) {
		return datamap.getOrDefault(uuid, -1.0);
	}

}
