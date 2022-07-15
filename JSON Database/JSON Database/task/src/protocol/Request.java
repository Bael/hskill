package protocol;

import com.google.gson.JsonElement;

public class Request {
    private String type;
    private JsonElement key;
    private JsonElement value;

    public String getType() {
        return type;
    }

    public JsonElement getKey() {
        return key;
    }

    public JsonElement getValue() {
        return value;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setKey(JsonElement key) {
        this.key = key;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }

}