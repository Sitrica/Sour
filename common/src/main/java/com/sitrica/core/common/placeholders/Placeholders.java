package com.sitrica.core.common.placeholders;

import com.google.common.collect.Lists;

import java.util.*;
import java.util.function.Function;

public class Placeholders {

	private static final List<Placeholder<?>> placeholders = new ArrayList<>();

	public static void registerPlaceholder(Placeholder<?> placeholder) {
		placeholders.add(placeholder);
	}

	public static <T> void registerPlaceholder(Collection<String> syntaxes, Function<T, String> function) {
		placeholders.add(new Placeholder<T>(syntaxes.toArray(new String[0])) {
			@Override
			public String replace(T object) {
				return function.apply(object);
			}
		});
	}

	/**
	 * Grab a placeholder by it's syntax.
	 * Example: %command% to be replaced by a String command.
	 *
	 * @param syntax The syntax to grab e.g: %player%
	 * @return The placeholder if the syntax was found.
	 */
	public static Optional<Placeholder<?>> getPlaceholder(String syntax) {
		for (Placeholder<?> placeholder : placeholders) {
			for (String s : placeholder.getSyntaxes()) {
				if (s.equals(syntax)) {
					return Optional.of(placeholder);
				}
			}
		}
		return Optional.empty();
	}

	public static List<Placeholder<?>> getPlaceholders() {
		List<Placeholder<?>> alternative = Lists.newArrayList(placeholders);
		alternative.sort(Comparator.comparing(Placeholder::getPriority));
		return alternative;
	}

}
