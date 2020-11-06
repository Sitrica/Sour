package com.sitrica.core.bukkit.command;

import com.sitrica.core.bukkit.SourBukkitPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class AliasesProcessor implements CommandExecutor {

	private final AbstractCommand command;
	private final SourBukkitPlugin instance;

	public AliasesProcessor(SourBukkitPlugin instance, AbstractCommand command) {
		this.instance = instance;
		this.command = command;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		instance.getCommandHandler().processRequirements(this.command, label, sender, args);
		return true;
	}

}
