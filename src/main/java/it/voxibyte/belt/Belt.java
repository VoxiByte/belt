package it.voxibyte.belt;

import it.voxibyte.belt.chat.Messenger;
import it.voxibyte.belt.i18n.Language;
import it.voxibyte.belt.plugin.BeltPlugin;

import java.io.IOException;

/**
 * The Belt class is a utility class for handling BeltPlugin instances.
 */
public class Belt {
    private static BeltPlugin pluginInstance;

    /**
     * Returns the instance of the BeltPlugin.
     *
     * @return the instance of the BeltPlugin
     * @throws UnsupportedOperationException if the BeltPlugin instance is not yet initialized
     */
    public static BeltPlugin getInstance() {
        if(pluginInstance == null)
            throw new UnsupportedOperationException("this belt plugin is not yet initialized");
        return pluginInstance;
    }

    /**
     * Initializes the BeltPlugin instance for the Belt utility class.
     *
     * @param beltPlugin the BeltPlugin instance to be initialized
     */
    public static void init(BeltPlugin beltPlugin) {
        Belt.pluginInstance = beltPlugin;
    }
}
