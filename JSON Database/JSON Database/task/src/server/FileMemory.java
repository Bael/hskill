package server;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Storing first key as key of map for "optimization".
 */
public class FileMemory implements Memory {
    private static final Gson gson = new Gson();

    Path path = Paths.get("src/server/data/db.json");
//        String FILENAME_LOCAL_ENVIRONMENT = System.getProperty("user.dir") + "/JSON Database/task/src/server/data/db.json";
//        Path path = Paths.get(FILENAME_LOCAL_ENVIRONMENT);

    private static final Type KEY_VALUE_TYPE = new TypeToken<Map<String, JsonElement>>() {}.getType();

    @Override
    public void put(JsonElement key, JsonElement value) {
        Map<String, JsonElement> data = readMemory();
        String[] keyArray = getAsArrayKey(key);
        String rootKey = keyArray[0];
        JsonElement prevValue = data.get(rootKey);

        if (keyArray.length == 1) {
            data.put(rootKey, value);
        } else {
            if (prevValue == null) {
                prevValue = new JsonObject();
            }
            JsonElement newValue = injectValue(keyArray, prevValue, value);
            data.put(rootKey, newValue);
        }
        writeMemory(data);
    }

    private JsonElement injectValue(String[] keyArray, JsonElement prevValue, JsonElement value) {
        if (!prevValue.isJsonObject()) {
            throw new RuntimeException("Wrong value! " + prevValue);

        }
        JsonObject obj = prevValue.getAsJsonObject();

        for(int i=1; i<keyArray.length - 1; i++) {
            String key = keyArray[i];
            if (!obj.has(key)) {
                obj.add(key, new JsonObject());
            }
            obj = obj.getAsJsonObject(key);
        }
        obj.addProperty(keyArray[keyArray.length - 1], value.getAsString());
        return prevValue;
    }

    /**
     * nested read
     * @param keyArray
     * @param value
     * @return
     */
    private JsonElement readValue(String[] keyArray, JsonElement value) {
        if (!value.isJsonObject()) {
            throw new RuntimeException("Wrong value! " + value);
        }
        JsonObject obj = value.getAsJsonObject();
        for(int i=1; i<keyArray.length - 1; i++) {
            String key = keyArray[i];
            if (!obj.has(key)) {
                return null;
            }
            obj = obj.getAsJsonObject(key);
        }
        return obj.get(keyArray[keyArray.length - 1]);
    }

    private boolean deleteValue(String[] keyArray, JsonElement value) {
        if (!value.isJsonObject()) {
            throw new RuntimeException("Wrong value! " + value);

        }
        JsonObject obj = value.getAsJsonObject();
        for(int i=1; i<keyArray.length - 1; i++) {
            String key = keyArray[i];
            if (!obj.has(key)) {
                return false;
            }
            obj = obj.getAsJsonObject(key);
        }
        if (obj.has(keyArray[keyArray.length - 1])) {
            obj.remove(keyArray[keyArray.length - 1]);
            return true;
        }
        return false;
    }

    private String[] getAsArrayKey(JsonElement key) {
        if (Objects.isNull(key))
            return null;
        if (key.isJsonNull())
            return null;
        if (key.isJsonPrimitive())
            return new String[] {key.getAsString()};
        if (key.isJsonArray()) {
            JsonArray asJsonArray = key.getAsJsonArray();
            String[] array = new String[asJsonArray.size()];
            for(int i = 0; i< array.length; i++) {
                array[i] = asJsonArray.get(i).getAsString();
            }
            return array;
        }
        throw new RuntimeException("Unsupported key type!" + key);
    }

    private void writeMemory(Map<String, JsonElement> data) {
        String result = gson.toJson(data);
        try {
            Files.writeString(path, result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Map<String, JsonElement> readMemory() {
        Map<String, JsonElement> data;
        try (JsonReader reader = new JsonReader(new FileReader(path.toFile()))) {
            data = gson.fromJson(reader, KEY_VALUE_TYPE);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (data == null) {
            data = new HashMap<>();
        }
        return data;
    }

    @Override
    public JsonElement get(JsonElement key) {
        Map<String, JsonElement> data = readMemory();
        String[] keyArray = getAsArrayKey(key);
        String rootKey = keyArray[0];
        JsonElement value = data.get(rootKey);
        if (value == null) {
            return null;
        }
        if (keyArray.length == 1) {
            return value;
        } else {
            return readValue(keyArray, value);
        }
    }

    @Override
    public boolean delete(JsonElement key) {
        Map<String, JsonElement> data = readMemory();
        String[] keyArray = getAsArrayKey(key);
        String rootKey = keyArray[0];
        if (!data.containsKey(rootKey)) {
            return false;
        }

        if (keyArray.length == 1) {
            data.remove(rootKey);
            writeMemory(data);
            return true;
        } else {
            JsonObject value = data.get(rootKey).getAsJsonObject();
            boolean result = deleteValue(keyArray, value);
            if (result) {
                data.put(rootKey, value);
                writeMemory(data);
            }
            return result;
        }
    }

    @Override
    public boolean contains(JsonElement key) {
        Map<String, JsonElement> data = readMemory();
        String[] keyArray = getAsArrayKey(key);
        String rootKey = keyArray[0];
        if (!data.containsKey(rootKey)) {
            return false;
        }

        if (keyArray.length > 1) {
            return containsKey(keyArray, data.get(rootKey));
        }
        return true;
    }

    private boolean containsKey(String[] keyArray, JsonElement value) {
        if (!value.isJsonObject()) {
            throw new RuntimeException("Wrong value! " + value);
        }
        JsonObject obj = value.getAsJsonObject();
        for(int i=1; i<keyArray.length - 1; i++) {
            String key = keyArray[i];
            if (!obj.has(key)) {
                return false;
            }
            obj = obj.getAsJsonObject(key);
        }
        if (obj.has(keyArray[keyArray.length - 1])) {
            return true;
        }
        return false;
    }
}

