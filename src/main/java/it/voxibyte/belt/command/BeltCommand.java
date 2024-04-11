package it.voxibyte.belt.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.commandsenders.BukkitPlayer;
import dev.jorel.commandapi.executors.ExecutionInfo;
import org.bukkit.entity.Player;

public abstract class BeltCommand extends CommandAPICommand {
    public BeltCommand(String commandName) {
        super(commandName);

        executesPlayer(this::execute);
    }

    protected abstract void execute(ExecutionInfo<Player, BukkitPlayer> executionInfo);
}
