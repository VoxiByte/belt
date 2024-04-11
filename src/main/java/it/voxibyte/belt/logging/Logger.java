package it.voxibyte.belt.logging;

import it.voxibyte.belt.Belt;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.logging.Level;

/**
 * Utility class for handling plugin logging
 */
public class Logger {
    private static final CommandSender loggingOutput;

    private static String logPrefix = "&8[&6%s &7(&e%s&7)&8] &f";

    static {
        loggingOutput = Bukkit.getConsoleSender();
    }

    public static void setLogPrefix(String logPrefix) {
        Logger.logPrefix = logPrefix;
    }

    public static void info(String message) {
        String prefix = compilePrefix(Level.INFO);
        loggingOutput.sendMessage(prefix + message);
    }

    public static void warning(String message) {
        String prefix = compilePrefix(Level.WARNING);
        loggingOutput.sendMessage(prefix + message);
    }

    public static void severe(String message) {
        String prefix = compilePrefix(Level.SEVERE);
        loggingOutput.sendMessage(prefix + message);
    }

    private static String compilePrefix(Level level) {
        String compiledPrefix = String.format(logPrefix, Belt.getInstance().getName(), level.getName());

        return ChatColor.translateAlternateColorCodes('&', compiledPrefix);
    }
}
