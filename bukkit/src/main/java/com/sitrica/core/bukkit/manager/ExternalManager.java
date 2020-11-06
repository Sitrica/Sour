package com.sitrica.core.bukkit.manager;

import com.sitrica.core.bukkit.SourBukkitPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class ExternalManager implements Listener {

	protected final SourBukkitPlugin instance;
	protected final String name;

	protected ExternalManager(SourBukkitPlugin instance, String name, boolean listener) {
		this.instance = instance;
		this.name = name;
		if (listener)
			Bukkit.getPluginManager().registerEvents(this, instance);
	}

	public String getName() {
		return name;
	}

	public abstract boolean isEnabled();

}
