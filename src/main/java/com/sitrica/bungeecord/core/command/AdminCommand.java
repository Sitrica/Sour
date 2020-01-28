package com.sitrica.bungeecord.core.command;

import com.sitrica.bungeecord.core.SourPlugin;

public abstract class AdminCommand extends AbstractCommand {

	protected AdminCommand(SourPlugin instance, boolean console, String... commands) {
		super(instance, console, commands);
	}

}
