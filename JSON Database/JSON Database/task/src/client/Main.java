package client;

import com.beust.jcommander.JCommander;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import protocol.Request;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.BiConsumer;

public class Main {
    private static final String ADDRESS = "127.0.0.1";
    private static final int PORT = 23456;

    private final static Gson gson = new GsonBuilder().create();

    public static void main(String[] args) throws IOException {
        Args parseArgs = new Args();
        JCommander.newBuilder()
                .addObject(parseArgs)
                .build()
                .parse(args);
        String request = createRequest(parseArgs);

        String address = ADDRESS;
        int port = PORT;
        ClientDBConnection.connect(address, port, ClientDBConnection.exchange(request));
    }

    private static Request createRequestFromArgs(Args arguments) {
        String requestType = arguments.getType();
        String key = arguments.getKey();
        String value = arguments.getValue();

        Request request = new Request();
        request.setType(requestType);
        if (key != null) {
            request.setKey(new JsonPrimitive(key));
        }
        if (value != null) {
            request.setValue(new JsonPrimitive(value));
        }

        return request;
    }

    private static final String INPUT_FILE_DIR = "src/client/data/";
    public static String createRequest(Args arguments) throws IOException {
        String inputFile = arguments.getFilename();

        Request request;
        if (inputFile == null) {
            request = createRequestFromArgs(arguments);
        } else {
            request = createRequestFromFile(inputFile);
        }

        return gson.toJson(request);
    }

    private static Request createRequestFromFile(String inputFile) throws IOException {
        String fileContent = new String(Files.readAllBytes(Path.of(INPUT_FILE_DIR + inputFile)));
        System.out.println(fileContent);
        return gson.fromJson(fileContent, Request.class);
    }

    public static class ClientDBConnection {
        public static void connect(String address, int port, BiConsumer<DataInputStream, DataOutputStream> consumer) throws IOException {
            System.out.println("Client started!");
            try (Socket socket = new Socket(InetAddress.getByName(address), port);
                 DataInputStream input = new DataInputStream(socket.getInputStream());
                 DataOutputStream output = new DataOutputStream(socket.getOutputStream());) {
                consumer.accept(input, output);
            }
        }

        public static BiConsumer<DataInputStream, DataOutputStream> exchange(String command) throws IOException {
            return (dataInputStream1, dataOutputStream1) -> {
                try {
                    System.out.println("Sent: " + command);
                    dataOutputStream1.writeUTF(command);
                    String response = dataInputStream1.readUTF();
                    System.out.println("Received: " + response);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            };
        }
    }
}