package it.voxibyte.belt.task;

import it.voxibyte.belt.Belt;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

public class BeltTask implements BukkitTask {
    private final int taskId;
    private final boolean sync;

    private boolean cancelled;

    /**
     * Converts a BukkitTask object to a BeltTask object.
     *
     * @param task the BukkitTask object to be converted
     * @return a BeltTask object representing the converted task
     */
    public static BeltTask fromBukkit(BukkitTask task) {
        return new BeltTask(task.getTaskId(), task.isSync());
    }

    /**
     * Represents a BeltTask, which is a task that can be scheduled and run by the Bukkit scheduler.
     */
    private BeltTask(final int taskId, final boolean sync) {
        this.taskId = taskId;
        this.sync = sync;
    }

    @Override
    public int getTaskId() {
        return this.taskId;
    }

    @Override
    public Plugin getOwner() {
        return Belt.getInstance();
    }

    @Override
    public boolean isSync() {
        return this.sync;
    }

    @Override
    public boolean isCancelled() {
        return this.cancelled;
    }

    @Override
    public void cancel() {
        Bukkit.getScheduler().cancelTask(this.taskId);
    }
}
