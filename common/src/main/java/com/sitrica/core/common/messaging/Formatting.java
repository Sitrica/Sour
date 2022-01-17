package com.sitrica.core.common.messaging;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class Formatting {

	public static TextComponent color(String input) {
		if (input == null) return Component.empty();
		return LegacyComponentSerializer.legacyAmpersand().deserialize(input);
	}

	public static TextComponent colorAndStrip(String input) {
		if (input == null) return Component.empty();
		return Component.text(stripColor(color(input)));
	}

	public static String stripColor(Component input) {
		if (input == null) return "";
		return PlainTextComponentSerializer.plainText().serialize(input);
	}
}
