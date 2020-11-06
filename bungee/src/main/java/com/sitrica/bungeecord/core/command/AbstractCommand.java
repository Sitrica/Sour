package com.sitrica.bungeecord.core.command;

import com.sitrica.bungeecord.core.SourPlugin;
import com.sitrica.bungeecord.core.messaging.Formatting;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.config.Configuration;

public abstract class AbstractCommand {

	private final Configuration messages;
	protected final SourPlugin instance;
	private final String[] aliases;
	private final boolean console;

	protected AbstractCommand(SourPlugin instance, boolean console, String... aliases) {
		this.messages = instance.getConfiguration("messages").get();
		this.instance = instance;
		this.aliases = aliases;
		this.console = console;
	}

	protected enum ReturnType {
		SUCCESS,
		FAILURE,
		SYNTAX_ERROR
	}

	public boolean containsCommand(String input) {
		for (String command : aliases) {
			if (command.equalsIgnoreCase(input))
				return true;
		}
		return false;
	}

	protected boolean isConsoleAllowed() {
		return console;
	}

	protected String[] getAliases() {
		return aliases;
	}

	protected abstract ReturnType runCommand(String command, CommandSender sender, String... arguments);

	public abstract String getConfigurationNode();

	public abstract String[] getPermissionNodes();

	public String getDescription(CommandSender sender) {
		String description = messages.getString("commands." + getConfigurationNode() + ".description");
		return Formatting.color(description);
	}

	public String getSyntax(CommandSender sender) {
		String syntax = messages.getString("commands." + getConfigurationNode() + ".syntax");
		return Formatting.color(syntax);
	}

}
