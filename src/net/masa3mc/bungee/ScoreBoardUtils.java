package net.masa3mc.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.ScoreboardObjective.HealthDisplay;

public class ScoreBoardUtils {

	// Objective
	/**
	 * 0 to create, 1 to remove, 2 to update display text.
	 */
	public static void objective(String unique, String display, Byte byt, ProxiedPlayer p) {
		ScoreboardObjective obj = new ScoreboardObjective(unique, display, HealthDisplay.INTEGER, byt);
		sendPacket(p, obj);
	}

	public static void createObject(String unique, String display, ProxiedPlayer player) {
		objective(unique, display, (byte) 0, player);
		// ScoreboardObjective obj = new ScoreboardObjective(unique, display,
		// HealthDisplay.INTEGER, (byte) 0);
		// sendPacket(player, obj);
	}

	public static void removeObject(String unique, String display, ProxiedPlayer player) {
		// ScoreboardObjective obj = new
		// ScoreboardObjective(unique,display,HealthDisplay.INTEGER,(byte)1);
		// sendPacket(player, obj);
		objective(unique, display, (byte) 1, player);
	}

	// Score
	public static void updateScore(String name, String object, int value, ProxiedPlayer player) {
		ScoreboardScore score = new ScoreboardScore(name, (byte) 0, object, value);
		sendPacket(player, score);
	}

	public static void removeScore(String name, String object, ProxiedPlayer player) {
		ScoreboardScore score = new ScoreboardScore(name, (byte) 1, object, 0);
		sendPacket(player, score);
	}

	// Display
	/**
	 * 0:list, 1:sidebar, 2:below name
	 */
	public static void display(int position, String unique, ProxiedPlayer player) {
		ScoreboardDisplay disp = new ScoreboardDisplay((byte) position, unique);
		sendPacket(player, disp);
	}

	// Others
	public static boolean sendPacket(ProxiedPlayer player, DefinedPacket packet) {
		if (player != null) {
			player.unsafe().sendPacket(packet);
		}
		return player != null;
	}

}
