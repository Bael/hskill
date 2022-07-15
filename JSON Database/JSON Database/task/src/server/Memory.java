package server;

import com.google.gson.JsonElement;

public interface Memory {
    void put(JsonElement key, JsonElement value);

    JsonElement get(JsonElement key);

    boolean delete(JsonElement key);

    boolean contains(JsonElement key);
}