package com.sitrica.core.bungee.command;

import com.sitrica.core.bungee.SourBungeePlugin;

public abstract class AdminCommand extends AbstractCommand {

	protected AdminCommand(SourBungeePlugin instance, boolean console, String... commands) {
		super(instance, console, commands);
	}

}
