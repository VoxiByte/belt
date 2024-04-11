package it.voxibyte.belt.setup;

import it.voxibyte.belt.setup.action.WizardAction;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class SetupWizard<T> {
    private static final List<Class<?>> SUPPORTED_ACTIONS = List.of(
            AsyncPlayerChatEvent.class, PlayerInteractEvent.class, BlockPlaceEvent.class,
            BlockBreakEvent.class, PlayerInteractAtEntityEvent.class
    );

    protected T result;

    private final Player player;
    private final CompletableFuture<T> completable;
    private final List<String> order;
    private final Map<String, WizardAction<?>> actionMap;

    private boolean completed;
    private int currentAction;

    public SetupWizard(Player player) {
        this.player = player;
        this.currentAction = 0;
        this.completable = new CompletableFuture<>();
        this.order = new ArrayList<>();
        this.actionMap = new HashMap<>();
    }

    public void initialize() {
        onInitialize();
        onActionStart(order.get(0));
    }

    protected abstract void onInitialize();

    protected abstract void onActionStart(String actionId);

    protected abstract boolean onActionSupply(String actionId, WizardAction<?> wizardAction);

    protected abstract void onActionComplete(String actionId, WizardAction<?> wizardAction);

    protected abstract void onComplete(T result);

    protected void addAction(String actionId, WizardAction<?> wizardAction) {
        if (!SUPPORTED_ACTIONS.contains(wizardAction.getType()))
            throw new UnsupportedOperationException("action type not supported");
        this.actionMap.put(actionId, wizardAction);
        this.order.add(actionId);
    }

    protected void complete() {
        completable.complete(result);
        this.onComplete(result);
        completed = true;
    }

    protected void abort() {
        completed = true;
    }

    public void supplyAction(String actionId, WizardAction<?> wizardAction) {
        boolean valid = this.onActionSupply(actionId, wizardAction);
        if (valid) {
            this.onActionComplete(actionId, wizardAction);
            if (finished()) return;
            this.currentAction += 1;
            this.onActionStart(order.get(currentAction));
        }
    }

    private boolean finished() {
        if (this.currentAction == (this.actionMap.size() - 1)) {
            this.complete();
            return true;
        }
        return false;
    }

    public void handleEvent(Event event) {
        Set<Map.Entry<String, WizardAction<?>>> actions = actionMap.entrySet();
        for (Map.Entry<String, WizardAction<?>> entry : actions) {
            if (!entry.getKey().equals(order.get(currentAction))) {
                continue;
            }

            if (entry.getValue().getType() != event.getClass()) {
                return;
            }

            if (event instanceof Cancellable) {
                ((Cancellable) event).setCancelled(true);
            }

            WizardAction<?> wizardAction = entry.getValue();
            wizardAction.setResult(event);
            this.supplyAction(entry.getKey(), wizardAction);
            return;
        }
    }

    public CompletableFuture<T> whenCompleted() {
        return this.completable;
    }

    public Player getPlayer() {
        return this.player;
    }

    public boolean isCompleted() {
        return this.completed;
    }
}
