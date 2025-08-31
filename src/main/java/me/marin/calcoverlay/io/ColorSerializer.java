package me.marin.calcoverlay.io;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class ColorSerializer implements JsonSerializer<Color>, JsonDeserializer<Color> {

    @Override
    public JsonElement serialize(Color color, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(String.format("%06X", (color.getRed() << 16) + (color.getGreen() << 8) + color.getBlue()));
    }

    @Override
    public Color deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        int rgb = Integer.parseInt(jsonElement.getAsString(), 16);
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF);
    }

}
