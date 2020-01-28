package com.sitrica.bungeecord.core;

import java.util.Optional;

import com.sitrica.bungeecord.core.command.CommandHandler;
import com.sitrica.bungeecord.core.manager.Manager;
import com.sitrica.bungeecord.core.messaging.Formatting;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;

public abstract class SourPlugin extends Plugin {

	private final String prefix, name;
	private String[] managerPackages;

	/**
	 * @param packageName Name used for the sour plugin packaging, like com.sitrica.example
	 * @param prefix Define the default prefix of the plugin.
	 * @param managerPackages packages containing the managers.
	 */
	public SourPlugin(String name, String packageName, String prefix, String... managerPackages) {
		this.managerPackages = managerPackages;
		if (managerPackages == null)
			this.managerPackages = new String[] {packageName + ".managers"};
		this.prefix = prefix;
		this.name = name;
	}

	/**
	 * Grabs the configuration defined by the String from the plugin.
	 * 
	 * @param name The file name without it's file extension.
	 * @return Configuration if the plugin has registered such configuration.
	 */
	public abstract Optional<Configuration> getConfiguration(String name);

	public void consoleMessage(String string) {
		getProxy().getConsole().sendMessage(TextComponent.fromLegacyText(Formatting.color(prefix + string)));
	}

	public void debugMessage(String string) {
		getConfiguration("config").ifPresent(configuration -> {
			if (configuration.getBoolean("debug", false))
				consoleMessage("&b" + string);
		});
	}

	/**
	 * Grab a Manager by it's class and create it if not present.
	 * 
	 * @param <T> <T extends Manager>
	 * @param expected The expected Class that extends Manager.
	 * @return The Manager that matches the defined class.
	 */
	public abstract <T extends Manager> T getManager(Class<T> expected);

	public Optional<Configuration> getConfig() {
		return getConfiguration("config");
	}

	/**
	 * @return The CommandManager allocated to the plugin.
	 */
	public abstract CommandHandler getCommandHandler();

	/**
	 * @return The package names where managers exist to be registered.
	 */
	public String[] getManagerPackages() {
		return managerPackages;
	}

	/** 
	 * @return The default string prefix of the plugin.
	 */
	public String getPrefix() {
		return prefix;
	}

	public String getName() {
		return name;
	}

}
