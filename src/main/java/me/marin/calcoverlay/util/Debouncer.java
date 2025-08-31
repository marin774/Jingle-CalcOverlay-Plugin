package me.marin.calcoverlay.util;

import java.util.concurrent.*;

/**
 * Executes the runnable after runTask method hasn't been called in delayMs milliseconds.
 */
public class Debouncer {

    private final int delayMs;
    private final ScheduledExecutorService service;
    private ScheduledFuture<?> task = null;

    public Debouncer(int delayMs) {
        this.delayMs = delayMs;
        this.service = Executors.newScheduledThreadPool(1);
    }

    public void runTask(Runnable runnable) {
        this.cancel();
        task = service.schedule(runnable, delayMs, TimeUnit.MILLISECONDS);
    }

    public void cancel() {
        if (task != null && !task.isDone()) {
            task.cancel(false);
        }
    }


}
