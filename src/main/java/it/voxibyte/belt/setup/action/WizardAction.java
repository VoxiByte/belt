package it.voxibyte.belt.setup.action;

import org.bukkit.event.Event;

public class WizardAction<T extends Event> {
    private final Class<T> actionType;

    private T result;

    public static <T extends Event> WizardAction<T> event(Class<T> actionType) {
        return new WizardAction<>(actionType);
    }

    private WizardAction(Class<T> actionType) {
        this.actionType = actionType;
    }

    public boolean isComplete() {
        return this.result != null;
    }

    public T getResult() {
        return this.result;
    }

    public Class<? extends Event> getType() {
        return this.actionType;
    }

    @SuppressWarnings("unchecked")
    public void setResult(Event event) {
        this.result = (T) event;
    }
}
