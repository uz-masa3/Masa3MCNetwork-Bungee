package net.masa3mc.bungee.cmd;

import java.sql.SQLException;

import net.masa3mc.bungee.Conf;
import net.masa3mc.bungee.Masa3MC;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class C_Masa3MC extends Command {

	public C_Masa3MC() {
		super("gmasa3mc");
	}

	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(c("&c----------Masa3MC----------"));
			sender.sendMessage(c("&7 - /gmasa3mc reload"));
			sender.sendMessage(c("&7 - /gmasa3mc force_configsave"));
		} else {
			if (args[0].equalsIgnoreCase("reload")) {
				Conf.reload();
				sender.sendMessage(c("&7[Masa3MC] &a再読込が完了しました。"));
			} else if (args[0].equalsIgnoreCase("force_configsave")) {
				Masa3MC.saveDefaultConfig(true);
				sender.sendMessage(c("&7[Masa3MC] &aConfigを強制的に初期状態に戻しました。"));
			} else {
				sender.sendMessage(c("&c----------Masa3MC----------"));
				sender.sendMessage(c("&7 - /gmasa3mc reload"));
				sender.sendMessage(c("&7 - /gmasa3mc force_configsave"));
			}
		}
	}

	private TextComponent c(String string) {
		return new TextComponent(ChatColor.translateAlternateColorCodes('&', string));
	}

}
