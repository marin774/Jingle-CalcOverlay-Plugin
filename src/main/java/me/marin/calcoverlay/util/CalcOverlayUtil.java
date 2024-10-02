package me.marin.calcoverlay.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CalcOverlayUtil {

    public static void runAsync(String threadName, Runnable runnable) {
        new Thread(runnable, threadName).start();
    }

    public static void runTimerAsync(Runnable runnable, int delayMs) {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(runnable, 0, delayMs, TimeUnit.MILLISECONDS);
    }

}
