package com.sitrica.core.common.messaging;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Formatting {

	public static String colorHex(String input) {
		if (input == null) return "";

		Matcher matcher = Pattern.compile("&#([A-Fa-f0-9]{6})").matcher(input);

		while(matcher.find()) {
			input = input.replace(matcher.group(), ChatColor.of(matcher.group().replace("&", "")).toString());
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
