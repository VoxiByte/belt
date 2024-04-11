package it.voxibyte.belt.task;

import it.voxibyte.belt.Belt;
import org.bukkit.Bukkit;

/**
 * The Tasker class provides methods to schedule tasks to run on server ticks, after specified delays, asynchronously and synchronously.
 */
public class Tasker {

    /**
     * This method is used to schedule a task to run on the next server tick.
     * It takes a Runnable object as a parameter which represents the task to be executed.
     * The method returns a BeltTask object that can be used to access information about the scheduled task and control its execution.
     *
     * @param runnable the task to be executed on the next server tick
     * @return a BeltTask object representing the scheduled task
     */
    public static BeltTask runLater(final Runnable runnable) {
        return runLater(runnable, 1L);
    }

    /**
     * This method is used to schedule a task to run after a specified delay.
     * It takes a Runnable object as a parameter which represents the task to be executed.
     * The method returns a BeltTask object that can be used to access information about the scheduled task and control its execution.
     *
     * @param runnable the task to be executed after the specified delay
     * @param after the delay in milliseconds
     * @return a BeltTask object representing the scheduled task
     */
    public static BeltTask runLater(final Runnable runnable, final long after) {
        return BeltTask.fromBukkit(Bukkit.getScheduler().runTaskLater(Belt.getInstance(), runnable, 1));
    }

    /**
     * This method is used to schedule a task to run asynchronously.
     * It takes a Runnable object as a parameter which represents the task to be executed.
     * The method returns a BeltTask object that can be used to access information about the scheduled task and control its execution.
     *
     * @param runnable the task to be executed asynchronously
     * @return a BeltTask object representing the scheduled task
     */
    public static BeltTask runAsync(final Runnable runnable) {
        return BeltTask.fromBukkit(Bukkit.getScheduler().runTaskAsynchronously(Belt.getInstance(), runnable));
    }

    /**
     * This method is used to schedule a task to run asynchronously after a specified delay.
     * It takes a Runnable object as a parameter which represents the task to be executed.
     * The method returns a BeltTask object that can be used to access information about the scheduled task and control its execution.
     *
     * @param runnable the task to be executed after the specified delay
     * @param after the delay in milliseconds
     * @return a BeltTask object representing the scheduled task
     */
    public static BeltTask runLaterAsync(final Runnable runnable, final long after) {
        return BeltTask.fromBukkit(Bukkit.getScheduler().runTaskLaterAsynchronously(Belt.getInstance(), runnable, after));
    }

    /**
     * This method is used to schedule a task to run synchronously.
     * It takes a Runnable object as a parameter which represents the task to be executed.
     * The method returns a BeltTask object that can be used to access information about the scheduled task and control its execution.
     *
     * @param runnable the task to be executed synchronously
     * @return a BeltTask object representing the scheduled task
     */
    public static BeltTask sync(final Runnable runnable) {
        return BeltTask.fromBukkit(Bukkit.getScheduler().runTask(Belt.getInstance(), runnable));
    }

    public static BeltTask runTimer(final Runnable runnable, final long delay, final long period) {
        return BeltTask.fromBukkit(Bukkit.getScheduler().runTaskTimer(Belt.getInstance(), runnable, delay, period));
    }

    public static BeltTask runTimerAsync(final Runnable runnable, final long delay, final long period) {
        return BeltTask.fromBukkit(Bukkit.getScheduler().runTaskTimerAsynchronously(Belt.getInstance(), runnable, delay, period));
    }

}
