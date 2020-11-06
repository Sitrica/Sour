package com.sitrica.core.bukkit.messaging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sitrica.core.bukkit.SourBukkitPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

public class Formatting {

	public static String messagesPrefixed(SourBukkitPlugin instance, ConfigurationSection section, String... nodes) {
		FileConfiguration messages = instance.getConfiguration("messages").orElse(instance.getConfig());
		String complete = messages.getString("messages.prefix", instance.getPrefix());
		return Formatting.color(complete + messages(section, Arrays.copyOfRange(nodes, 0, nodes.length)));
	}

	public static String messages(ConfigurationSection section, String... nodes) {
		String complete = "";
		List<String> list = Arrays.asList(nodes);
		Collections.reverse(list);
		int i = 0;
		for (String node : list) {
			if (i == 0)
				complete = section.getString(node, "Error " + section.getCurrentPath() + "." + node) + complete;
			else
				complete = section.getString(node, "Error " + section.getCurrentPath() + "." + node) + " " + complete;
			i++;
		}
		return Formatting.color(complete);
	}

	public static String colorHex(String input) {
		if (input == null) return "";

		Matcher matcher = Pattern.compile("&#([A-Fa-f0-9]{6})").matcher(input);

		while(matcher.find()) {
			input = input.replace(matcher.group(), net.md_5.bungee.api.ChatColor.of(matcher.group()).toString());
		}

		return input;
	}

	public static String color(String input) {
		if (input == null) return "";
		return colorHex(ChatColor.translateAlternateColorCodes('&', input));
	}

	public static String colorAndStrip(String input) {
		if (input == null) return "";
		return stripColor(color(input));
	}

	public static String stripColor(String input) {
		if (input == null) return "";
		return ChatColor.stripColor(input);
	}

}
