package it.voxibyte.belt.setup;

import it.voxibyte.belt.Belt;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class SetupHandler {
    private final WizardListener wizardListener;
    private final Map<UUID, SetupWizard<?>> activeWizards;

    public SetupHandler() {
        this.activeWizards = new HashMap<>();
        this.wizardListener = new WizardListener(this, activeWizards);

        createEventListener();
    }

    public Optional<SetupWizard<?>> createWizard(final Player player, final SetupWizard<?> setupWizard) {
        if(this.activeWizards.containsKey(player.getUniqueId())) return Optional.empty();

        this.activeWizards.put(player.getUniqueId(), setupWizard);
        setupWizard.initialize();

        return Optional.of(setupWizard);
    }

    public void removeWizard(UUID wizardUuid) {
        this.activeWizards.remove(wizardUuid);
    }

    private void createEventListener() {
        Bukkit.getPluginManager().registerEvents(wizardListener, Belt.getInstance());
    }
}
