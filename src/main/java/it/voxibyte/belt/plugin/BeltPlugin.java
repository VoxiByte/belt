package it.voxibyte.belt.plugin;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPIConfig;
import it.voxibyte.belt.Belt;
import it.voxibyte.belt.chat.Messenger;
import it.voxibyte.belt.i18n.Language;
import it.voxibyte.belt.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

/**
 * Entry point for plugins using Belt.
 * This entry point is used to make sure all the Belt requirements are setup
 *
 * @see JavaPlugin for reference
 */
public abstract class BeltPlugin extends JavaPlugin {
    @Override
    public final void onEnable() {
        Belt.init(this);

        initCommandManager();
        onPluginEnable();
    }

    @Override
    public final void onDisable() {
        onPluginDisable();
    }

    /**
     * Called when the plugin is being enabled
     *
     * @see JavaPlugin#onEnable()
     */
    protected void onPluginEnable() {
        Logger.warning("Plugin enable method has not been overwritten, make sure to handle all the enable logics correctly");
    }

    /**
     * Called when the plugin is being disabled
     *
     * @see JavaPlugin#onDisable()
     */
    protected void onPluginDisable() {
        Logger.warning("Plugin disable method has not been overwritten, make sure to handle all the disable logics correctly");
    }

    private void initCommandManager() {
        CommandAPIConfig<?> commandAPIConfig = new CommandAPIBukkitConfig(this);
        commandAPIConfig.silentLogs(true);

        CommandAPI.onLoad(commandAPIConfig);
    }

    protected void enableMessenger(String fileName) {
        try {
            Language.init(this, fileName.replace(".yml", ""));
            Messenger.init();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
