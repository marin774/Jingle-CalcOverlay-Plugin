package me.marin.calcoverlay.ninjabrainapi;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.Getter;
import me.marin.calcoverlay.io.CalcOverlaySettings;
import me.marin.calcoverlay.util.*;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.marin.calcoverlay.CalcOverlay.log;

public class NinjabrainBotEventSubscriber {

    @Getter
    private final SSEClient sseClient;
    private final AtomicBoolean isConnected = new AtomicBoolean(false);
    private final List<AtomicBoolean> list = new ArrayList<>();

    public NinjabrainBotEventSubscriber() {
        this.sseClient = new SSEClient("http://localhost:52533/api/v1");
    }

    public boolean ping() {
        return sseClient.tryPing();
    }

    public void startConnectJob() {
        CalcOverlayUtil.runTimerAsync(() -> {
            if (!CalcOverlaySettings.getInstance().calcOverlayEnabled) {
                return;
            }
            if (isConnected.get()) {
                return;
            }
            if (ping()) {
                subscribeToEvents();
                isConnected.set(true);
                log(Level.INFO, "Connected to Ninjabrain Bot API.");
            }
        }, 500);
    }
    public void disconnect() {
        for (AtomicBoolean atomicBoolean : list) {
            atomicBoolean.set(false);
        }
        list.clear();
        isConnected.set(false);
        log(Level.INFO, "Disconnected from Ninjabrain Bot API.");
    }

    public static final Gson GSON = new Gson();
    @Getter
    private JsonObject latestResponse;

    public void subscribeToEvents() {
        list.add(sseClient.subscribe("stronghold", response -> {
            latestResponse = response;
            OverlayUtil.writeImage(OverlayUtil.getPanelForStronghold(response));
        }));
        //sseClient.keepRequestingWithDelay("stronghold", 5000, this::getPanelForStronghold); // if you change angles too fast, ninb api will miss an update...
    }

}
