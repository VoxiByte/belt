package it.voxibyte.belt.command;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandTree;

/**
 * The Commands class provides a utility method for creating instances of CommandAPICommand.
 */
public class Commands {

    /**
     * Creates a new instance of CommandAPICommand with the specified label
     *
     * @param label the label to be assigned to the CommandAPICommand
     * @return a new instance of CommandAPICommand with the specified label
     */
    public static CommandAPICommand createCommand(String label) {
        return new CommandAPICommand(label);
    }

    /**
     * Creates a new instance of CommandTree with the specified label.
     *
     * @param label the label to be assigned to the CommandTree
     * @return a new instance of CommandTree with the specified label
     */
    public static CommandTree createCommandTree(String label) {
        return new CommandTree(label);
    }

}
