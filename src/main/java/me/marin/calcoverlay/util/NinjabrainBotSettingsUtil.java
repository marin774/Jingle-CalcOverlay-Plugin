package me.marin.calcoverlay.util;

import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.Jingle;
import xyz.duncanruns.jingle.util.ExceptionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

public class NinjabrainBotSettingsUtil {

    public static Map<String, Object> getSettings() {
        Map<String, Object> map = new HashMap<>();
        Preferences pref = Preferences.userRoot().node("ninjabrainbot");

        try {
            for (String key : pref.keys()) {
                String value;
                try {
                    value = pref.get(key, null);
                } catch (Exception ignored) {
                    continue;
                }

                try {
                    int intValue = pref.getInt(key, Integer.MIN_VALUE);
                    if (intValue != Integer.MIN_VALUE || value.equals(String.valueOf(Integer.MIN_VALUE))) {
                        map.put(key, intValue);
                        continue;
                    }
                } catch (Exception ignored) {}

                try {
                    double floatValue = pref.getDouble(key, Double.NaN);
                    if (!Double.isNaN(floatValue) || value.equals("NaN")) {
                        map.put(key, floatValue);
                        continue;
                    }
                } catch (Exception ignored) {}

                // Default to string
                map.put(key, value);
            }
        } catch (Exception e) {
            Jingle.log(Level.ERROR, "Failed to get Ninjabrain Bot settings:\n" + ExceptionUtil.toDetailedString(e));
        }
        return map;
    }

}
