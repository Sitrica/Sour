package com.sitrica.core.common.messaging;

import com.google.common.collect.Sets;
import com.sitrica.core.common.placeholders.Placeholder;
import com.sitrica.core.common.placeholders.Placeholders;
import com.sitrica.core.common.placeholders.SimplePlaceholder;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEventSource;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.*;
import java.util.regex.Pattern;

abstract class MessageBuilderImpl<T> {
    private final Map<Placeholder<?>, Object> placeholders = new TreeMap<>(Comparator.comparing(Placeholder::getPriority));
    private final List<Audience> senders = new ArrayList<>();

    private final Map<Integer, ClickEvent> clickEvents = new HashMap<>();
    private final Map<Integer, HoverEventSource<?>> hoverEvents = new HashMap<>();

    private Object defaultPlaceholderObject;
    private ConfigurationNode section;
    private String[] nodes;
    private boolean prefix;

    private Map<Placeholder<?>, Object> placeholders() {
        return placeholders;
    }

    /**
     * Add a placeholder to the MessageBuilder.
     *
     * @param placeholderObject The object to be determined in the placeholder.
     * @param placeholder The actual instance of the Placeholder.
     * @return The MessageBuilder for chaining.
     */
    @SuppressWarnings("unchecked")
    public T placeholders(String placeholderObject, Placeholder<?> placeholder) {
        placeholders.put(placeholder, placeholderObject);
        return (T) this;
    }

    public List<Audience> audience() {
        return senders;
    }

    /**
     * Set the senders to send this message to.
     *
     * @param senders The CommandSenders to send the message to.
     */
    @SuppressWarnings("unchecked")
    public T audience(Collection<Audience> senders) {
        this.senders.addAll(senders);
        return (T) this;
    }

    /**
     * Set the senders to send this message to.
     *
     * @param senders The CommandSenders to send the message to.
     */
    @SuppressWarnings("unchecked")
    public T audience(Audience... senders) {
        audience(Sets.newHashSet(senders));
        return (T) this;
    }

    /**
     * Sends the final product of the builder.
     */
    public void send(Collection<Audience> senders) {
        audience(senders);
        send();
    }

    /**
     * Sends the final product of the builder.
     */
    public void send(Audience... senders) {
        audience(senders);
        send();
    }

    public Map<Integer, ClickEvent> clickEvent() {
        return this.clickEvents;
    }

    /**
     * Adds a click event to the message
     * @param line The line to add the hover event to.
     * @param clickEvent The hover event.
     */
    @SuppressWarnings("unchecked")
    public T clickEvent(int line, ClickEvent clickEvent) {
        this.clickEvents.put(line, clickEvent);
        return (T) this;
    }

    public Map<Integer, HoverEventSource<?>> hoverEvent() {
        return this.hoverEvents;
    }

    /**
     * Adds a hover event to the message
     * @param line The line to add the hover event to.
     * @param hoverEvent The hover event.
     */
    @SuppressWarnings("unchecked")
    public T hoverEvent(int line, HoverEventSource<?> hoverEvent) {
        this.hoverEvents.put(line, hoverEvent);
        return (T) this;
    }

    public Object placeholderObject() {
        return defaultPlaceholderObject;
    }

    /**
     * Set the placeholder object, good if you want to allow multiple placeholders.
     *
     * @param object The object to set
     * @return The MessageBuilder for chaining.
     */
    @SuppressWarnings("unchecked")
    public T placeholderObject(Object object) {
        this.defaultPlaceholderObject = object;
        return (T) this;
    }

    public boolean prefix() {
        return prefix;
    }

    @SuppressWarnings("unchecked")
    public T prefix(boolean prefix) {
        this.prefix = prefix;
        return (T) this;
    }

    public ConfigurationNode config() {
        return section;
    }

    @SuppressWarnings("unchecked")
    public T config(ConfigurationNode section) {
        this.section = section;
        return (T) this;
    }

    public String[] nodes() {
        return nodes;
    }

    /**
     * Set the configuration nodes from messages.yml
     *
     * @param nodes The nodes to use.
     */
    @SuppressWarnings("unchecked")
    public T nodes(String... nodes) {
        this.nodes = nodes;
        return (T) this;
    }

    protected TextComponent applyPlaceholders(TextComponent input) {
        // Registered Placeholders
        for (Map.Entry<Placeholder<?>, Object> entry : placeholders().entrySet()) {
            Placeholder<?> placeholder = entry.getKey();
            for (String syntax : placeholder.getSyntaxes()) {
                if (!Formatting.stripColor(input).toLowerCase().contains(syntax.toLowerCase()))
                    continue;
                if (placeholder instanceof SimplePlaceholder) {
                    SimplePlaceholder simple = (SimplePlaceholder) placeholder;
                    input = (TextComponent) input.replaceText(builder -> builder.match(Pattern.quote(syntax)).replacement(simple.get()));
                } else {
                    String replacement = placeholder.replace_i(entry.getValue());
                    if (replacement != null)
                        input = (TextComponent) input.replaceText(builder -> builder.match(Pattern.quote(syntax)).replacement(replacement));
                }
            }
        }
        // Default Placeholders
        for (Placeholder<?> placeholder : Placeholders.getPlaceholders()) {
            for (String syntax : placeholder.getSyntaxes()) {
                if (!Formatting.stripColor(input).toLowerCase().contains(syntax.toLowerCase()))
                    continue;
                if (placeholder instanceof SimplePlaceholder) {
                    SimplePlaceholder simple = (SimplePlaceholder) placeholder;
                    input = (TextComponent) input.replaceText(builder -> builder.match(Pattern.quote(syntax)).replacement(simple.get()));
                } else if (placeholderObject() != null && placeholder.getType().isAssignableFrom(placeholderObject().getClass())) {
                    String replacement = placeholder.replace_i(placeholderObject());
                    if (replacement != null)
                        input = (TextComponent) input.replaceText(builder -> builder.match(Pattern.quote(syntax)).replacement(replacement));
                }
            }
        }
        return input;
    }

    public abstract void send();
}
