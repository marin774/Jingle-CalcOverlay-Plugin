package me.marin.calcoverlay.ninjabrainapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import me.marin.calcoverlay.util.CalcOverlayUtil;
import org.apache.logging.log4j.Level;
import xyz.duncanruns.jingle.util.ExceptionUtil;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static me.marin.calcoverlay.CalcOverlay.log;

@RequiredArgsConstructor
public class SSEClient {

    private final String baseURL;
    private final Gson gson = new Gson();

    public boolean tryPing() {
        try {
            URL url = new URL(baseURL + "/ping");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(1000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                return true;
            } else {
                log(Level.DEBUG, "Ping failed with response code: " + responseCode);
                return false;
            }
        } catch (SocketTimeoutException e) {
            return false;
        } catch (Exception e) {
            log(Level.ERROR, "Error while trying to ping Ninjabrain Bot API:\n" + ExceptionUtil.toDetailedString(e));
            return false;
        }
    }

    public void get(String endpoint, Consumer<JsonObject> dataConsumer) {
        CalcOverlayUtil.runAsync(endpoint + "-endpoint", () -> {
            try {
                URL url = new URL(baseURL + "/" + endpoint);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    log(Level.WARN, "Failed to get " + endpoint + " endpoint, response code: " + responseCode + ".");
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                StringBuilder response = new StringBuilder();

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                String responseString = response.toString().trim();

                try {
                    dataConsumer.accept(gson.fromJson(responseString, JsonObject.class));
                } catch (Exception e) {
                    log(Level.ERROR, "Consumer error while getting " + endpoint + " endpoint:\n" + ExceptionUtil.toDetailedString(e));
                }

            } catch (Exception e) {

            }
        });
    }

    /**
     * @return AtomicBoolean, set to false to disconnect
     */
    public AtomicBoolean subscribe(String endpoint, Consumer<JsonObject> eventDataConsumer) {
        AtomicBoolean enabled = new AtomicBoolean(true);
        CalcOverlayUtil.runAsync(endpoint + "-events", () -> {
            try {
                URL url = new URL(baseURL + "/" + endpoint + "/events");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "text/event-stream");

                int responseCode = connection.getResponseCode();
                if (responseCode != 200) {
                    log(Level.WARN, "Failed to connect to " + endpoint + " event endpoint, response code: " + responseCode + ".");
                    return;
                }

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder eventBuilder = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null && enabled.get()) {
                    if (line.startsWith("data:")) {
                        eventBuilder.append(line.substring(5));
                    } else if (line.isEmpty()) {
                        String eventString = eventBuilder.toString().trim();

                        try {
                            eventDataConsumer.accept(gson.fromJson(eventString, JsonObject.class));
                        } catch (Exception e) {
                            log(Level.ERROR, "Consumer error on " + endpoint + " event endpoint:\n" + ExceptionUtil.toDetailedString(e));
                        }

                        eventBuilder.setLength(0);
                    }
                }

            } catch (Exception e) {
                log(Level.ERROR, "Error while listening on " + endpoint + " endpoint:\n" + ExceptionUtil.toDetailedString(e));
            }
        });
        return enabled;
    }

    public void keepRequestingWithDelay(String endpoint, int delayMs, Consumer<JsonObject> eventDataConsumer) {
        CalcOverlayUtil.runAsync(endpoint + "-events", () -> {
            try {
                while (true) {
                    get(endpoint, eventDataConsumer);
                    Thread.sleep(delayMs);
                }
            } catch (Exception e) {
                log(Level.ERROR, "Error while listening on " + endpoint + " endpoint:\n" + ExceptionUtil.toDetailedString(e));
            }
        });
    }

}
