package com.sitrica.bungeecord.core.placeholders;

import java.util.Arrays;

import com.google.common.reflect.TypeToken;
import com.sitrica.bungeecord.core.messaging.Formatting;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class Placeholder<T> {

	private final String[] syntaxes;
	private final int priority;

	public Placeholder(String... syntaxes) {
		this.syntaxes = syntaxes;
		this.priority = 99;
	}

	public Placeholder(int priority, String... syntaxes) {
		this.syntaxes = syntaxes;
		this.priority = priority;
	}

	public int getPriority() {
		return priority;
	}

	public String[] getSyntaxes() {
		return syntaxes;
	}
	
	@SuppressWarnings("serial")
	public Class<? super T> getType() {
		return new TypeToken<T>(getClass()){}.getRawType();
	}
	
	/**
	 * Replace a placeholder from the given object.
	 * 
	 * @param object The object to get the placeholder replacement from.
	 * @return The final replaced placeholder.
	 */
	public abstract Object replace(T object);
	
	@SuppressWarnings("unchecked")
	public String replace_i(Object object) {
		Object replacement = null;
		try {
			replacement = replace((T) object);
			if (replacement == null)
				return null;
		} catch (ClassCastException e) {
			ProxyServer.getInstance().getConsole().sendMessage(TextComponent.fromLegacyText(Formatting.color("&cThere was an issue with class casting being incorrect in the placeholders: " + Arrays.toString(syntaxes))));
			return null;
		}
		return replacement.toString();
	}
	
}
