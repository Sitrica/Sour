package com.sitrica.core.bungee.manager;

import com.sitrica.core.bungee.SourBungeePlugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;

public abstract class ExternalManager implements Listener {

	protected final String name;

	protected ExternalManager(SourBungeePlugin instance, String name, boolean listener) {
		this.name = name;
		if (listener)
			ProxyServer.getInstance().getPluginManager().registerListener(instance, this);
	}

	public String getName() {
		return name;
	}

	public abstract boolean isEnabled();

}
