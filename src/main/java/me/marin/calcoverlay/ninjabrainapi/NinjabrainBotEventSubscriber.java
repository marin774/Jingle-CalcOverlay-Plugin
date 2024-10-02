package me.marin.calcoverlay.ninjabrainapi;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.marin.calcoverlay.util.AngleToCoords;
import me.marin.calcoverlay.util.OverlayUtil;
import me.marin.calcoverlay.util.PlayerPosition;
import me.marin.calcoverlay.util.Prediction;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static me.marin.calcoverlay.CalcOverlay.log;

public class NinjabrainBotEventSubscriber {

    private final SSEClient sseClient;
    private boolean isConnected;
    private List<AtomicBoolean> list = new ArrayList<>();

    public NinjabrainBotEventSubscriber() {
        this.sseClient = new SSEClient("http://localhost:52533/api/v1");
    }

    public boolean ping() {
        return sseClient.tryPing();
    }

    public void connect() {
        if (!isConnected && ping()) {
            subscribeToEvents();
            log(Level.INFO, "Connected to api.");
        }
    }
    public void disconnect() {
        for (AtomicBoolean atomicBoolean : list) {
            atomicBoolean.set(false);
        }
        list.clear();
        isConnected = false;
        log(Level.INFO, "Disconnected from api.");
    }

    private static final Gson GSON = new Gson();

    public void subscribeToEvents() {
        list.add(sseClient.subscribe("stronghold", this::handleStronghold));
        //sseClient.keepRequestingWithDelay("stronghold", 5000, this::handleStronghold); // if you change angles too fast, ninb api will miss an update...

        isConnected = true;
    }

    private final JsonObject dummyMeasurement = GSON.fromJson(
            "{\"eyeThrows\":[{\"xInOverworld\":1199.63,\"angleWithoutCorrection\":-161.14926034190884,\"zInOverworld\":-139.09,\"angle\":-161.13926034190885,\"correction\":0.01,\"error\":0.0014111816929869292,\"type\":\"NORMAL\"}],\"resultType\":\"TRIANGULATION\",\"playerPosition\":{\"xInOverworld\":1199.63,\"isInOverworld\":true,\"isInNether\":false,\"horizontalAngle\":-161.15,\"zInOverworld\":-139.09},\"predictions\":[{\"overworldDistance\":523.3899550048701,\"certainty\":0.5147413124532876,\"chunkX\":85,\"chunkZ\":-40},{\"overworldDistance\":1216.5659558774444,\"certainty\":0.2674146623130985,\"chunkX\":99,\"chunkZ\":-81},{\"overworldDistance\":1859.1560464361241,\"certainty\":0.1252834863035146,\"chunkX\":112,\"chunkZ\":-119},{\"overworldDistance\":1909.7519223710706,\"certainty\":0.07908349382318092,\"chunkX\":113,\"chunkZ\":-122},{\"overworldDistance\":1165.9697787678717,\"certainty\":0.012493314849953712,\"chunkX\":98,\"chunkZ\":-78}]}",
            JsonObject.class
    );

    public void showDummyMeasurement() {
        handleStronghold(dummyMeasurement);
    }

    private void handleStronghold(JsonObject response) {
        String resultType = response.get("resultType").getAsString();

        switch (resultType) {
            case "NONE":
            case "FAILED":
                OverlayUtil.empty();
                break;
            case "BLIND":
                handleBlind();
                break;
            case "TRIANGULATION":
                JsonArray predictions = response.get("predictions").getAsJsonArray();

                PlayerPosition playerPosition = GSON.fromJson(response.get("playerPosition"), PlayerPosition.class);

                List<Pair<Prediction, AngleToCoords>> predictionsList = new ArrayList<>();
                for (JsonElement predictionJson : predictions) {
                    Prediction prediction = GSON.fromJson(predictionJson, Prediction.class);
                    AngleToCoords angleToCoords = getAngleToCoords(prediction, playerPosition);
                    predictionsList.add(Pair.of(prediction, angleToCoords));
                }


                OverlayUtil.measurements(predictionsList, playerPosition);
        }
    }

    private AngleToCoords getAngleToCoords(Prediction prediction, PlayerPosition playerPosition) {
        int multiplier = playerPosition.isInNether() ? 8 : 1;
        double x = playerPosition.getXInOverworld() * multiplier;
        double z = playerPosition.getZInOverworld() * multiplier;

        double strongholdX = prediction.getChunkX() * 16 + 4;
        double strongholdZ = prediction.getChunkZ() * 16 + 4;

        double actualAngle = Math.toDegrees(Math.atan2(strongholdX - x, z - strongholdZ)) + 180;
        if (actualAngle > 180) {
            actualAngle -= 360;
        }
        while (actualAngle < -180) {
            actualAngle += 360;
        }

        double playerAngle = playerPosition.getHorizontalAngle();
        while (playerAngle > 180) {
            playerAngle -= 360;
        }
        while (playerAngle < -180) {
            playerAngle += 360;
        }

        double neededAngleCorrection = actualAngle - playerAngle;
        while (neededAngleCorrection > 180) {
            neededAngleCorrection -= 360;
        }
        while (neededAngleCorrection < -180) {
            neededAngleCorrection += 360;
        }

        return new AngleToCoords(actualAngle, neededAngleCorrection);
    }

    private void handleBlind() {
        sseClient.get("blind", (response) -> {
            boolean isBlindModeEnabled = response.get("isBlindModeEnabled").getAsBoolean();
            if (!isBlindModeEnabled) {
                return;
            }
            JsonObject blindResult = response.get("blindResult").getAsJsonObject();

            double xNether = blindResult.get("xInNether").getAsDouble();
            double zNether = blindResult.get("zInNether").getAsDouble();
            String evaluation = blindResult.get("evaluation").getAsString();
            double probability = blindResult.get("highrollProbability").getAsDouble();

            OverlayUtil.homePortal(xNether, zNether, evaluation, probability);

            log(Level.INFO, "Blind result: " + xNether + ", " + zNether);
        });
    }

}
