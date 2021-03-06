package com.sitrica.core.bungee.messaging;

import com.google.common.collect.Sets;
import com.sitrica.core.bungee.SourBungeePlugin;
import com.sitrica.core.common.messaging.Formatting;
import com.sitrica.core.common.placeholders.Placeholder;
import com.sitrica.core.common.placeholders.Placeholders;
import com.sitrica.core.common.placeholders.SimplePlaceholder;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.config.Configuration;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class ListMessageBuilder {

	private final Map<Placeholder<?>, Object> placeholders = new HashMap<>();
	private final Map<Integer, ClickEvent> clickEvents = new HashMap<>();
	private final Map<Integer, HoverEvent> hoverEvents = new HashMap<>();
	private final List<CommandSender> senders = new ArrayList<>();
	private Object defaultPlaceholderObject;
	private Configuration configuration;
	private final SourBungeePlugin instance;
	private final boolean prefix;
	private final String node;

	/**
	 * Creates a ListMessageBuilder with the defined node.
	 * 
	 * @param node The configuration nodes from the messages.yml
	 */
	public ListMessageBuilder(SourBungeePlugin instance, String node) {
		this(instance, true, node);
	}

	/**
	 * Creates a ListMessageBuilder with the defined ConfigurationSection.
	 * 
	 * @param node The configuration nodes from the ConfigurationSection
	 * @param section The ConfigurationSection to read from.
	 */
	public ListMessageBuilder(SourBungeePlugin instance, boolean prefix, String node, Configuration section) {
		this(instance, prefix, node);
		this.configuration = section;
	}

	/**
	 * Creates a ListMessageBuilder with the defined nodes, and if it should contain the prefix.
	 * 
	 * @param prefix The boolean to enable or disable prefixing this message.
	 * @param node The configuration nodes from the messages.yml
	 */
	public ListMessageBuilder(SourBungeePlugin instance, boolean prefix, String node) {
		this.instance = instance;
		this.prefix = prefix;
		this.node = node;
	}

	/**
	 * Set the senders to send this message to.
	 *
	 * @param senders The CommandSenders to send the message to.
	 * @return The ListMessageBuilder for chaining.
	 */
	public ListMessageBuilder toSenders(CommandSender... senders) {
		this.senders.addAll(Sets.newHashSet(senders));
		return this;
	}

	/**
	 * Set the players to send this message to.
	 *
	 * @param players The ProxiedPlayer... to send the message to.
	 * @return The ListMessageBuilder for chaining.
	 */
	public ListMessageBuilder toPlayers(ProxiedPlayer... players) {
		this.senders.addAll(Sets.newHashSet(players));
		return this;
	}

	/**
	 * Set the players to send this message to.
	 *
	 * @param players The Collection<ProxiedPlayer> to send the message to.
	 * @return The ListMessageBuilder for chaining.
	 */
	public ListMessageBuilder toPlayers(Collection<? extends ProxiedPlayer> players) {
		this.senders.addAll(players);
		return this;
	}

	/**
	 * Add a placeholder to the MessageBuilder.
	 * 
	 * @param placeholderObject The object to be determined in the placeholder.
	 * @param placeholder The actual instance of the Placeholder.
	 * @return The ListMessageBuilder for chaining.
	 */
	public ListMessageBuilder withPlaceholder(Object placeholderObject, Placeholder<?> placeholder) {
		placeholders.put(placeholder, placeholderObject);
		return this;
	}

	/**
	 * Set the configuration to read from, by default is the messages.yml
	 * 
	 * @param configuration The FileConfiguration to read from.
	 * @return The ListMessageBuilder for chaining.
	 */
	public ListMessageBuilder fromConfiguration(Configuration configuration) {
		this.configuration = configuration;
		return this;
	}

	/**
	 * Created a single replacement and ignores the placeholder object.
	 * 
	 * @param syntax The syntax to check within the messages e.g: %command%
	 * @param replacement The replacement e.g: the command.
	 * @return The ListMessageBuilder for chaining.
	 */
	public ListMessageBuilder replace(String syntax, Object replacement) {
		placeholders.put(new SimplePlaceholder(syntax) {
			@Override
			public String get() {
				return replacement.toString();
			}
		}, replacement.toString());
		return this;
	}

	/**
	 * Created a single replacement and ignores the placeholder object.
	 * 
	 * @param priority The priority of the placeholder replacement.
	 * @param syntax The syntax to check within the messages e.g: %command%
	 * @param replacement The replacement e.g: the command.
	 * @return The ListMessageBuilder for chaining.
	 */
	public ListMessageBuilder replace(int priority, String syntax, Object replacement) {
		placeholders.put(new SimplePlaceholder(priority, syntax) {
			@Override
			public String get() {
				return replacement.toString();
			}
		}, replacement.toString());
		return this;
	}

	/**
	 * Set the placeholder object, good if you want to allow multiple placeholders.
	 * 
	 * @param object The object to set
	 * @return The ListMessageBuilder for chaining.
	 */
	public ListMessageBuilder setPlaceholderObject(Object object) {
		this.defaultPlaceholderObject = object;
		return this;
	}

	/**
	 * Sends the final product of the builder.
	 */
	public void send(CommandSender... senders) {
		toSenders(senders).send();
	}

	/**
	 * Completes and returns the final product of the builder.
	 */
	public List<TextComponent> get() {
		List<TextComponent> list = new ArrayList<>();
		if (configuration == null) {
			try {
				configuration = instance.getConfiguration("messages")
						.orElse(instance.getConfig()
								.orElseThrow(() -> new IllegalAccessException("Plugin had no messages.yml")));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}
		boolean usedPrefix = false;
		for (String string : configuration.getStringList(node)) {
			if (prefix && !usedPrefix) {
				string = instance.getPrefix() + " " + Formatting.color(string);
				usedPrefix = true;
			} else {
				string = Formatting.color(string);
			}
			TextComponent component = new TextComponent(applyPlaceholders(string));
			if (hoverEvents.containsKey(list.size() + 1)) component.setHoverEvent(hoverEvents.get(list.size() + 1));
			if (clickEvents.containsKey(list.size() + 1)) component.setClickEvent(clickEvents.get(list.size() + 1));
			list.add(component);
		}
		return list;
	}

	private String applyPlaceholders(String input) {
		// Registered Placeholders
		for (Entry<Placeholder<?>, Object> entry : placeholders.entrySet()) {
			Placeholder<?> placeholder = entry.getKey();
			for (String syntax : placeholder.getSyntaxes()) {
				if (!input.toLowerCase().contains(syntax.toLowerCase()))
					continue;
				if (placeholder instanceof SimplePlaceholder) {
					SimplePlaceholder simple = (SimplePlaceholder) placeholder;
					input = input.replaceAll(Pattern.quote(syntax), simple.get());
				} else {
					String replacement = placeholder.replace_i(entry.getValue());
					if (replacement != null)
						input = input.replaceAll(Pattern.quote(syntax), replacement);
				}
			}
		}
		// Default Placeholders
		for (Placeholder<?> placeholder : Placeholders.getPlaceholders()) {
			for (String syntax : placeholder.getSyntaxes()) {
				if (!input.toLowerCase().contains(syntax.toLowerCase()))
					continue;
				if (placeholder instanceof SimplePlaceholder) {
					SimplePlaceholder simple = (SimplePlaceholder) placeholder;
					input = input.replaceAll(Pattern.quote(syntax), simple.get());
				} else if (defaultPlaceholderObject != null && placeholder.getType().isAssignableFrom(defaultPlaceholderObject.getClass())) {
					String replacement = placeholder.replace_i(defaultPlaceholderObject);
					if (replacement != null)
						input = input.replaceAll(Pattern.quote(syntax), replacement);
				}
			}
		}
		return input;
	}

	/**
	 * Adds a hover event to the message
	 * @param line The line to add the hover event to.
	 * @param hoverEvent The hover event.
	 * @return The MessageBuilder for chaining.
	 */
	public ListMessageBuilder setHoverEvent(int line, HoverEvent hoverEvent) {
		this.hoverEvents.put(line, hoverEvent);
		return this;
	}

	/**
	 * Adds a click event to the message
	 * @param line The line to add the hover event to.
	 * @param clickEvent The hover event.
	 * @return The MessageBuilder for chaining.
	 */
	public ListMessageBuilder setClickEvent(int line, ClickEvent clickEvent) {
		this.clickEvents.put(line, clickEvent);
		return this;
	}

	/**
	 * Sends the final product of the builder if the senders are set.
	 */
	public void send() {
		List<TextComponent> list = get();
		if (senders.isEmpty())
			return;
		for (CommandSender sender : senders)
			list.forEach(sender::sendMessage);
	}

}
