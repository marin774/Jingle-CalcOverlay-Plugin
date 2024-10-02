package me.marin.calcoverlay.util;

public class CalcOverlayUtil {

    public static void runAsync(String threadName, Runnable runnable) {
        new Thread(runnable, threadName).start();
    }

}
