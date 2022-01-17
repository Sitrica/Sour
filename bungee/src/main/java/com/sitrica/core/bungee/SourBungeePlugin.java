package com.sitrica.core.bungee;

import com.sitrica.core.bungee.manager.Manager;
import com.sitrica.core.common.SourPlugin;
import net.md_5.bungee.api.plugin.Plugin;
import org.spongepowered.configurate.ConfigurationNode;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;

public abstract class SourBungeePlugin extends Plugin implements SourPlugin {

	private final String prefix;
	private String[] managerPackages;

	/**
	 * @param packageName Name used for the sour plugin packaging, like com.sitrica.example
	 * @param prefix Define the default prefix of the plugin.
	 * @param managerPackages packages containing the managers.
	 */
	public SourBungeePlugin(String packageName, String prefix, String... managerPackages) {
		this.managerPackages = managerPackages;
		if (managerPackages.length == 0)
			this.managerPackages = new String[] {packageName + ".managers"};
		this.prefix = prefix;
	}

	/**
	 * Grabs the configuration defined by the String from the plugin.
	 *
	 * @param name The file name without its file extension.
	 * @return Configuration if the plugin has registered such configuration.
	 */
	public abstract Optional<ConfigurationNode> getConfiguration(String name);

	/**
	 * Grab a Manager by its class and create it if not present.
	 *
	 * @param <T> <T extends Manager>
	 * @param expected The expected Class that extends Manager.
	 * @return The Manager that matches the defined class.
	 */
	public abstract <T extends Manager> T getManager(Class<T> expected);

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
	public String getName() {
		return getDescription().getName();
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
