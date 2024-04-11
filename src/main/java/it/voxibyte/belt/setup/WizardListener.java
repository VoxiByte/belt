package it.voxibyte.belt.setup;

import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;
import java.util.UUID;

public class WizardListener implements Listener {
    private final SetupHandler setupHandler;
    private final Map<UUID, SetupWizard<?>> activeWizards;

    public WizardListener(final SetupHandler setupHandler, final Map<UUID, SetupWizard<?>> activeWizards) {
        this.setupHandler = setupHandler;
        this.activeWizards = activeWizards;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        supply(event.getPlayer(), event);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        supply(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        supply(event.getPlayer(), event);
    }

    @EventHandler
    public void onBlockDestroy(BlockBreakEvent event) {
        supply(event.getPlayer(), event);
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractAtEntityEvent event) {
        supply(event.getPlayer(), event);
    }

    private void supply(Entity entity, Event event) {
        for(Map.Entry<UUID, SetupWizard<?>> activeWizard : this.activeWizards.entrySet()) {
            SetupWizard<?> setupWizard = activeWizard.getValue();
            if(setupWizard.getPlayer().getUniqueId() != entity.getUniqueId()) {
                continue;
            }

            setupWizard.handleEvent(event);
            if(setupWizard.isCompleted()) {
                setupHandler.removeWizard(activeWizard.getKey());
            }
        }
    }
}
