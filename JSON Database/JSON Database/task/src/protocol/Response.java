package protocol;

import com.google.gson.JsonElement;
import server.Main;

public class Response {
    private String response;
    private JsonElement value;
    private String reason;

    public static Response ok() {
        return new Response();
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public void setValue(JsonElement value) {
        this.value = value;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

}
