package com.sitrica.core.bukkit.command;

import com.sitrica.core.bukkit.SourBukkitPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

import com.sitrica.core.bukkit.messaging.Formatting;

public abstract class AbstractCommand {

	private final FileConfiguration messages;
	protected final SourBukkitPlugin instance;
	private final String[] commands;
	private final boolean console;
	private String aliases;

	protected AbstractCommand(SourBukkitPlugin instance, boolean console, String... commands) {
		this.messages = instance.getConfiguration("messages").get();
		this.instance = instance;
		this.commands = commands;
		this.console = console;
	}

	protected AbstractCommand(SourBukkitPlugin instance, String aliases, boolean console, String... commands) {
		this(instance, console, commands);
		this.aliases = aliases;
	}

	public String getAliases() {
		return aliases;
	}

	protected enum ReturnType {
		SUCCESS,
		FAILURE,
		SYNTAX_ERROR
	}

	public boolean containsCommand(String input) {
		for (String command : commands) {
			if (command.equalsIgnoreCase(input))
				return true;
		}
		return false;
	}

	protected boolean isConsoleAllowed() {
		return console;
	}

	protected String[] getCommands() {
		return commands;
	}

	public abstract ReturnType runCommand(String label, CommandSender sender, String... arguments);

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
