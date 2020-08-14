package com.sitrica.bungeecord.core.messaging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.sitrica.bungeecord.core.SourPlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class Formatting {

	public static String messagesPrefixed(SourPlugin instance, Configuration section, String... nodes) {
		Configuration messages;
		try {
			messages = instance.getConfiguration("messages")
					.orElse(instance.getConfig()
							.orElseThrow(() -> new IllegalAccessException("Plugin did not have a messages.yml")));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			return null;
		}
		String complete = messages.getString("messages.prefix", instance.getPrefix());
		return Formatting.color(complete + messages(section, Arrays.copyOfRange(nodes, 0, nodes.length)));
	}

	public static String messages(Configuration section, String... nodes) {
		String complete = "";
		List<String> list = Arrays.asList(nodes);
		Collections.reverse(list);
		int i = 0;
		for (String node : list) {
			if (i == 0)
				complete = section.getString(node, "Error " + section + "." + node) + complete;
			else
				complete = section.getString(node, "Error " + section + "." + node) + " " + complete;
			i++;
		}
		return Formatting.color(complete);
	}

	public static String color(String input) {
		if (input == null) return "";
		return ChatColor.translateAlternateColorCodes('&', input);
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
