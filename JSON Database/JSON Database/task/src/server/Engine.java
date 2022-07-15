package server;

import com.google.gson.JsonElement;
import protocol.Request;
import protocol.Response;

public class Engine {
    private final Memory memory = new ConcurrentHandlerDecorator(new FileMemory());

    public Response set(JsonElement key, JsonElement value) {
        memory.put(key, value);
        Response r = new Response();
        r.setResponse("OK");
        return r;
    }

    public Response get(JsonElement key) {
        Response r = new Response();
        if (!memory.contains(key)) {
            r.setResponse("ERROR");
            r.setReason("No such key");
            return r;
        }
        r.setValue(memory.get(key));
        r.setResponse("OK");
        return r;
    }

    public Response execute(Request request) {
        switch (request.getType().toUpperCase()) {
            case "SET":
                return set(request.getKey(), request.getValue());
            case "GET":
                return get(request.getKey());
            case "DELETE":
                return delete(request.getKey());
            case "EXIT":
                Response r = new Response();
                r.setResponse("OK");
                return r;
        }
        throw new RuntimeException("Unsupported request! " + request);

    }

    private Response delete(JsonElement key) {
        Response r = new Response();
        if (!memory.delete(key)) {
            r.setResponse("ERROR");
            r.setReason("No such key");
            return r;
        }
        r.setResponse("OK");
        return r;
    }
}