package com.sitrica.core.common.messaging;

import com.sitrica.core.common.SourPlugin;
import com.sitrica.core.common.objects.StringList;
import com.sitrica.core.common.placeholders.SimplePlaceholder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.title.Title;
import org.spongepowered.configurate.ConfigurationNode;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Function;

// TODO Null checks
public final class MessageBuilder extends MessageBuilderImpl<MessageBuilder> {
	private final SourPlugin instance;
	private TextComponent complete;

	private static MessageBuilder of(SourPlugin instance) {
		return new MessageBuilder(instance);
	}

	public static MessageBuilder of(SourPlugin instance, String... nodes) {
		return of(instance).prefix(true).nodes(nodes);
	}

	public static MessageBuilder of(SourPlugin instance, boolean prefix, String... nodes) {
		return of(instance).prefix(prefix).nodes(nodes);
	}

	public static MessageBuilder of(SourPlugin instance, boolean prefix, String node, ConfigurationNode section) {
		return of(instance).prefix(prefix).nodes(node).config(section);
	}

	/**
	 * Creates a MessageBuilder with the defined nodes, and if it should contain the prefix.
	 */
	private MessageBuilder(SourPlugin instance) {
		this.instance = instance;
	}

	/**
	 * Created a list replacement and ignores the placeholder object.
	 *
	 * @param syntax The syntax to check within the messages e.g: %command%
	 * @param collection The replacement e.g: the command.
	 * @return The MessageBuilder for chaining.
	 */
	public <T> MessageBuilder replace(String syntax, Collection<T> collection, Function<T, String> mapper) {
		replace(syntax, new StringList(collection, mapper).toString());
		return this;
	}

	/**
	 * Created a single replacement and ignores the placeholder object.
	 *
	 * @param syntax The syntax to check within the messages e.g: %command%
	 * @param replacement The replacement e.g: the command.
	 * @return The MessageBuilder for chaining.
	 */
	public MessageBuilder replace(String syntax, Object replacement) {
		placeholders(replacement.toString(), new SimplePlaceholder(syntax) {
			@Override
			public String get() {
				return replacement.toString();
			}
		});
		return this;
	}

	/**
	 * Created a single replacement and ignores the placeholder object with priority.
	 *
	 * @param priority The priority of the placeholder.
	 * @param syntax The syntax to check within the messages e.g: %command%
	 * @param replacement The replacement e.g: the command.
	 * @return The MessageBuilder for chaining.
	 */
	public MessageBuilder replace(int priority, String syntax, Object replacement) {
		placeholders(replacement.toString(), new SimplePlaceholder(priority, syntax) {
			@Override
			public String get() {
				return replacement.toString();
			}
		});
		return this;
	}

	/**
	 * Sends the message as an actionbar to the defined players.
	 *
	 * @param players the players to send to
	 */
	public void sendActionbar(Audience... players) {
		audience(players).sendActionbar();
	}

	/**
	 * Sends the message as a title to the defined players.
	 *
	 * @param players the players to send to
	 */
	public void sendTitle(Audience... players) {
		audience(players).sendTitle();
	}

	/**
	 * Completes and returns the final product of the builder.
	 * @return TextComponent
	 */
	public TextComponent get() {
		if (config() == null) {
			try {
				config(instance.getConfiguration("messages")
						.orElse(instance.getConfiguration()
								.orElseThrow(() -> new IllegalAccessException("Plugin had no messages.yml"))));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return null;
			}
		}

		complete = prefix() ? messagesPrefixed(config(), nodes()) : messages(config(), nodes());
		complete = applyPlaceholders(complete);
		return complete;
	}

	private TextComponent messagesPrefixed(ConfigurationNode section, String... nodes) {
		String prefix = section.node("messages", "prefix").getString(instance.getPrefix());
		return Formatting.color(prefix).append(messages(section, Arrays.copyOfRange(nodes, 0, nodes.length)));
	}

	private TextComponent messages(ConfigurationNode section, String... nodes) {
		TextComponent complete = Component.empty();

		int i = 0;
		for (String node : nodes) {
			complete = complete.append(Formatting.color((i == 0 ? "" : " ") + section.node(Arrays.asList(node.split("\\."))).getString("Missing " + node)));
			i++;
		}
		return complete;
	}

	/**
	 * Sends the final product of the builder as a title if the players using toPlayers are set.
	 *
	 * WARNING: The title method needs to have the following as a configuration, this is special.
	 * title:
	 * 	  enabled: false
	 * 	  title: "&2Example"
	 * 	  subtitle: "&5&lColors work too."
	 * 	  fadeOut: 20
	 * 	  fadeIn: 20
	 * 	  stay: 200
	 */
	public void sendTitle() {
		if (config() == null) {
			try {
				config(instance.getConfiguration("messages")
						.orElse(instance.getConfiguration()
								.orElseThrow(() -> new IllegalAccessException("Plugin had no messages.yml"))));
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
		}
		if (nodes().length != 1)
			return;
		if (!config().node(Arrays.asList(nodes()[0].split("\\.")), "enabled").getBoolean(false))
			return;
		TextComponent subtitle = applyPlaceholders(Formatting.color(config().node(Arrays.asList(nodes()[0].split("\\.")), "subtitle").getString("")));
		TextComponent title = applyPlaceholders(Formatting.color(config().node(Arrays.asList(nodes()[0].split("\\.")), "title").getString("")));
		int fadeOut = config().node(Arrays.asList(nodes()[0].split("\\.")), "fadeOut").getInt(20);
		int fadeIn = config().node(Arrays.asList(nodes()[0].split("\\.")), "fadeIn").getInt(20);
		int stay = config().node(Arrays.asList(nodes()[0].split("\\.")), "stay").getInt(200);
		if (audience() != null && !audience().isEmpty()) {
			audience().parallelStream()
					.filter(sender -> sender.get(Identity.UUID).isPresent())
					.forEach(player -> {
						final Title.Times times = Title.Times.of(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut));
						// Using the times object this title will use 500ms to fade in, stay on screen for 3000ms and then fade out for 1000ms
						player.showTitle(Title.title(title, subtitle, times));
					});
		}
	}

	/**
	 * Sends the final product of the builder as an action bar if the players using toPlayers are set.
	 */
	public void sendActionbar() {
		get();
		complete = Component.text(complete.content().replaceAll("\n", ""));
		if (audience() != null && !audience().isEmpty()) {
			audience().parallelStream()
					.filter(sender -> sender.get(Identity.UUID).isPresent())
					.forEach(sender -> sender.sendActionBar(complete));
		}
	}

	/**
	 * Adds a hover event to the message
	 * @param hoverEvent The hover event.
	 * @return The MessageBuilder for chaining.
	 */
	public MessageBuilder hoverEvent(HoverEvent<?> hoverEvent) {
		this.hoverEvent(0, hoverEvent);
		return this;
	}

	/**
	 * Adds a click event to the message
	 * @param clickEvent The hover event.
	 * @return The MessageBuilder for chaining.
	 */
	public MessageBuilder clickEvent(ClickEvent clickEvent) {
		this.clickEvent(0, clickEvent);
		return this;
	}

	/**
	 * Sends the final product of the builder if the senders are set.
	 */
	@Override
	public void send() {
		get();
		if (clickEvent().get(0) != null) complete = complete.clickEvent(clickEvent().get(0));
		if (hoverEvent().get(0) != null) complete = complete.hoverEvent(hoverEvent().get(0));
		if (!audience().isEmpty()) {
			for (Audience sender : audience()) {
				sender.sendMessage(complete);
			}
		}
	}

	@Override
	public String toString() {
		return String.valueOf(get());
	}

}
