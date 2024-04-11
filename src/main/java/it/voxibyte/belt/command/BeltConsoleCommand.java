package it.voxibyte.belt.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.commandsenders.AbstractConsoleCommandSender;
import dev.jorel.commandapi.commandsenders.BukkitConsoleCommandSender;
import dev.jorel.commandapi.commandsenders.BukkitPlayer;
import dev.jorel.commandapi.executors.ConsoleCommandExecutor;
import dev.jorel.commandapi.executors.ExecutionInfo;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public abstract class BeltConsoleCommand extends CommandAPICommand {
    public BeltConsoleCommand(String commandName) {
        super(commandName);


        executesConsole(this::execute);
    }

    protected abstract void execute(ExecutionInfo<ConsoleCommandSender, BukkitConsoleCommandSender> executionInfo);

}
