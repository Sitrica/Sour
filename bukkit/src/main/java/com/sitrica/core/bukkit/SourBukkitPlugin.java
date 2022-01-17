package com.sitrica.core.bukkit;

import com.sitrica.core.bungee.manager.Manager;
import com.sitrica.core.common.SourPlugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public abstract class SourBukkitPlugin extends JavaPlugin implements SourPlugin {

	private String[] managerPackages;
	private final String prefix;

	/**
	 * @param prefix Define the default prefix of the plugin.
	 */
	public SourBukkitPlugin(String prefix, String... managerPackages) {
		this.managerPackages = managerPackages;
		if (managerPackages == null)
			this.managerPackages = new String[] {"com.sitrica." + getName().toLowerCase() + ".managers"};
		this.prefix = prefix;
	}

	/**
	 * Grabs the configuration defined by the String from the plugin.
	 *
	 * @param name The file name without it's file extension.
	 * @return FileConfiguration if the plugin has registered such configuration.
	 */
	public abstract Optional<ConfigurationNode> getConfiguration(String name);

	/**
	 * Grab a Manager by it's class and create it if not present.
	 *
	 * @param <T> <T extends Manager>
	 * @param expected The expected Class that extends Manager.
	 * @return The Manager that matches the defined class.
	 */
	public abstract <T extends Manager> T getManager(Class<T> expected);

	/**
	 * @return The CommandManager allocated to the plugin.
	 */
//	public abstract CommandHandler getCommandHandler();

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


	@Override
	public Path getDataDirectory() {
		return getDataFolder().toPath().toAbsolutePath();
	}

	@Override
	public File getJar() {
		return getFile();
	}
}
