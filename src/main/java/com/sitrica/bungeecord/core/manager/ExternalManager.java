package com.sitrica.bungeecord.core.manager;

import com.sitrica.bungeecord.core.SourPlugin;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;

public abstract class ExternalManager implements Listener {

	protected final String name;

	protected ExternalManager(SourPlugin instance, String name, boolean listener) {
		this.name = name;
		if (listener)
			ProxyServer.getInstance().getPluginManager().registerListener(instance, this);
	}

	public String getName() {
		return name;
	}

	public abstract boolean isEnabled();

}
