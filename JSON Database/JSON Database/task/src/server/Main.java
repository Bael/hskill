package server;
import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException {
        String address = "127.0.0.1";
        int port = 23456;
        JsonDBServer server = JsonDBServer.createServer(address, port);
        server.accept();
    }
}
