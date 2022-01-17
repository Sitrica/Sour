package com.sitrica.core.common.messaging;

import com.sitrica.core.common.SourPlugin;
import com.sitrica.core.common.placeholders.SimplePlaceholder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ListMessageBuilder extends MessageBuilderImpl<ListMessageBuilder> {
    private final SourPlugin instance;

    private static ListMessageBuilder of(SourPlugin instance) {
        return new ListMessageBuilder(instance);
    }

    public static ListMessageBuilder of(SourPlugin instance, String node) {
        return of(instance).prefix(true).nodes(node);
    }

    public static ListMessageBuilder of(SourPlugin instance, boolean prefix, String node) {
        return of(instance).prefix(prefix).nodes(node);
    }

    public static ListMessageBuilder of(SourPlugin instance, boolean prefix, String node, ConfigurationNode section) {
        return of(instance).prefix(prefix).nodes(node).config(section);
    }

    /**
     * Creates a MessageBuilder with the defined nodes, and if it should contain the prefix.
     */
    private ListMessageBuilder(SourPlugin instance) {
        this.instance = instance;
    }

    /**
     * Created a single replacement and ignores the placeholder object.
     *
     * @param syntax The syntax to check within the messages e.g: %command%
     * @param replacement The replacement e.g: the command.
     * @return The ListMessageBuilder for chaining.
     */
    public ListMessageBuilder replace(String syntax, Object replacement) {
        placeholders(replacement.toString(), new SimplePlaceholder(syntax) {
            @Override
            public String get() {
                return replacement.toString();
            }
        });
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
        placeholders(replacement.toString(), new SimplePlaceholder(priority, syntax) {
            @Override
            public String get() {
                return replacement.toString();
            }
        });
        return this;
    }

    /**
     * Completes and returns the final product of the builder.
     */
    public TextComponent get() {
        TextComponent main = Component.empty();

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

        boolean usedPrefix = false;
        List<String> nodes = new ArrayList<>();

        try {
            nodes = config().node(Arrays.asList(nodes()[0].split("\\."))).getList(String.class);
        } catch (SerializationException e) {
            e.printStackTrace();
        }

        if (nodes.isEmpty()) return Component.text("Missing " + nodes()[0]);

        for (String string : nodes) {
            TextComponent component;
            component = Formatting.color((prefix() && !usedPrefix ? instance.getPrefix() + " " : "") + string);
            usedPrefix = true;

            component = applyPlaceholders(component);

            if (hoverEvent().containsKey(nodes.size() + 1)) component = component.hoverEvent(hoverEvent().get(nodes.size() + 1));
            if (clickEvent().containsKey(nodes.size() + 1)) component = component.clickEvent(clickEvent().get(nodes.size() + 1));

            main = main.append(component).append(Component.newline());
        }

        return main;
    }

    /**
     * Adds a hover event to the message
     * @param hoverEvent The hover event.
     * @return The MessageBuilder for chaining.
     */
    public ListMessageBuilder hoverEvent(int line, HoverEvent<?> hoverEvent) {
        return super.hoverEvent(line, hoverEvent);
    }

    /**
     * Adds a click event to the message
     * @param clickEvent The hover event.
     * @return The MessageBuilder for chaining.
     */
    public ListMessageBuilder clickEvent(int line, ClickEvent clickEvent) {
        return super.clickEvent(line, clickEvent);
    }

    @Override
    public void send() {
        if (!audience().isEmpty()) {
            for (Audience sender : audience()) {
                sender.sendMessage(get());
            }
        }
    }
}
