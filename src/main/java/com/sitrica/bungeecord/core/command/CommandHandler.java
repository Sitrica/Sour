package com.sitrica.bungeecord.core.command;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.sitrica.bungeecord.core.SourPlugin;
import com.sitrica.bungeecord.core.command.AbstractCommand.ReturnType;
import com.sitrica.bungeecord.core.messaging.MessageBuilder;
import com.sitrica.bungeecord.core.utils.Utils;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class CommandHandler extends Command {

	private final List<AbstractCommand> subcommands = new ArrayList<>();
	private final AbstractCommand command;
	private final SourPlugin instance;

	public CommandHandler(SourPlugin instance, String main, String permission, AbstractCommand command, String... commandPackages) {
		super(main, permission, command.getAliases());
		this.instance = instance;
		this.command = command;
		Utils.getClassesOf(instance, AbstractCommand.class, commandPackages).forEach(clazz -> {
			try {
				subcommands.add(clazz.getConstructor(SourPlugin.class).newInstance(instance));
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException	| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void execute(CommandSender sender, String[] arguments) {
		for (AbstractCommand abstractCommand : subcommands) {
			// It's the main command
			if (arguments.length <= 0 && abstractCommand.getClass().equals(command.getClass())) {
				processRequirements(abstractCommand, sender, arguments);
				return;
			} else if (arguments.length > 0 && abstractCommand.containsCommand(arguments[0])) {
				processRequirements(abstractCommand, sender, arguments);
				return;
			}
		}
		new MessageBuilder(instance, "messages.command-doesnt-exist").send(sender);
	}

	private void processRequirements(AbstractCommand command, CommandSender sender, String[] arguments) {
		if (!(sender instanceof ProxiedPlayer) && !command.isConsoleAllowed()) {
			 new MessageBuilder(instance, "messages.must-be-player")
			 		.replace("%command%", command.getSyntax(sender))
			 		.setPlaceholderObject(sender)
			 		.send(sender);
			return;
		}
		if (command.getPermissionNodes() == null || Arrays.stream(command.getPermissionNodes()).parallel().anyMatch(permission -> sender.hasPermission(permission))) {
			if (command instanceof AdminCommand) {
				if (sender instanceof ProxiedPlayer && !sender.hasPermission(instance.getName().toLowerCase() + ".admin")) {
					new MessageBuilder(instance, "messages.no-permission").send(sender);
					return;
				}
			}
			String[] array = arguments;
			String entered = instance.getName().toLowerCase();
			if (arguments.length > 0) {
				entered = array[0];
				array = Arrays.copyOfRange(arguments, 1, arguments.length);
			}
			ReturnType returnType = command.runCommand(entered, sender, array);
			if (returnType == ReturnType.SYNTAX_ERROR) {
				 new MessageBuilder(instance, "messages.invalid-command", "messages.invalid-command-correction")
				 		.replace("%command%", command.getSyntax(sender))
				 		.setPlaceholderObject(sender)
				 		.send(sender);
			}
			return;
		}
		new MessageBuilder(instance, "messages.no-permission").send(sender);
	}

}
