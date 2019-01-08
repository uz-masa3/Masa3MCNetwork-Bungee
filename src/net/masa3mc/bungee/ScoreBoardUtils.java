package net.masa3mc.bungee;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.protocol.DefinedPacket;
import net.md_5.bungee.protocol.packet.ScoreboardDisplay;
import net.md_5.bungee.protocol.packet.ScoreboardObjective;
import net.md_5.bungee.protocol.packet.ScoreboardScore;
import net.md_5.bungee.protocol.packet.ScoreboardObjective.HealthDisplay;

public class ScoreBoardUtils {

	// Objective
	public static ScoreboardObjective createObject(String unique, String display, ProxiedPlayer player) {
		// new ScoreboardObjective(unique, display, "integer", (byte) 0);
		ScoreboardObjective obj = new ScoreboardObjective();
		obj.setName(unique);
		obj.setValue(display);
		obj.setType(HealthDisplay.INTEGER);
		obj.setAction((byte) 0);
		sendPacket(player, obj);
		return obj;
	}

	public static ScoreboardObjective removeObject(String unique, String display, ProxiedPlayer player) {
		ScoreboardObjective obj = new ScoreboardObjective();
		obj.setName(unique);
		obj.setValue(display);
		obj.setType(HealthDisplay.INTEGER);
		obj.setAction((byte) 1);
		sendPacket(player, obj);
		return obj;
	}

	// Score
	public static ScoreboardScore updateScore(String name, String object, ProxiedPlayer player) {
		ScoreboardScore score = updateScore(name, object, 0, player);
		return score;
	}

	public static ScoreboardScore updateScore(String name, String object, int entry, ProxiedPlayer player) {
		ScoreboardScore score = new ScoreboardScore(name, (byte) 0, object, entry);
		sendPacket(player, score);
		return score;
	}

	public static ScoreboardScore removeScore(String name, String object, ProxiedPlayer player) {
		ScoreboardScore score = new ScoreboardScore(name, (byte) 1, object, 0);
		sendPacket(player, score);
		return score;
	}

	// Display
	/**
	 * 0:list, 1:sidebar, 2:below name
	 */
	public static ScoreboardDisplay getDisplay(int position, String unique, ProxiedPlayer player) {
		ScoreboardDisplay disp = new ScoreboardDisplay((byte) position, unique);
		sendPacket(player, disp);
		return disp;
	}

	// Others
	public static boolean sendPacket(ProxiedPlayer player, DefinedPacket packet) {
		if (player != null) {
			player.unsafe().sendPacket(packet);
		}
		return player != null;
	}

}
