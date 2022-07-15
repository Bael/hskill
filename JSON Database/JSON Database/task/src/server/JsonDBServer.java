package server;

import com.google.gson.Gson;
import protocol.Request;
import protocol.Response;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JsonDBServer {
    private static final Gson gson = new Gson();

    private final int port;
    private final String address;
    private final Engine engine;
    private final ExecutorService executorService;
    private boolean isStopped = false;
    private ServerSocket serverSocket;

    public JsonDBServer(String address, int port) {
        this.address = address;
        this.port = port;
        engine = new Engine();
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);
    }

    public static JsonDBServer createServer(String address, int port) throws IOException {
        return new JsonDBServer(address, port);
    }

    public void acceptInThread(ServerSocket server, Socket socket) throws IOException {
        try (DataInputStream input = new DataInputStream(socket.getInputStream());
             DataOutputStream output = new DataOutputStream(socket.getOutputStream())) {
            String commandString = input.readUTF(); // reading a message
            System.out.println("Received: " + commandString);
            Request request = gson.fromJson(commandString, Request.class);
            Response response = engine.execute(request);
            System.out.println("Sent:" + gson.toJson(response));
            output.writeUTF(gson.toJson(response)); // resend it to the client
            if (request.getType().toUpperCase().equals("EXIT")) {
                stop();
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
            executorService.shutdown();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }
    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(port, 50, InetAddress.getByName(address));
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port 8080", e);
        }
    }
    public void accept() throws IOException {
        openServerSocket();
        System.out.println("Server started!");
        while (!isStopped()) {
            try {
                Socket socket = serverSocket.accept(); // accepting a new client
                executorService.execute(() -> {
                    try {
                        acceptInThread(serverSocket, socket);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

            } catch (Exception runtimeException) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    System.out.println(runtimeException.getMessage());
                    return;
                }
            }
        }

    }
}
