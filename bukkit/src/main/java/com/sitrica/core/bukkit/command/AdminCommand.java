package com.sitrica.core.bukkit.command;

import com.sitrica.core.bukkit.SourBukkitPlugin;

public abstract class AdminCommand extends AbstractCommand {

	protected AdminCommand(SourBukkitPlugin instance, boolean console, String... commands) {
		super(instance, console, commands);
	}

	protected AdminCommand(SourBukkitPlugin instance, String aliases, boolean console, String... commands) {
		super(instance, aliases, console, commands);
	}

}
