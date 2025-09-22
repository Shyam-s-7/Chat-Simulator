import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Set<PrintWriter> clientWriters = new HashSet<>();
    private static Map<Socket, String> clientNames = new HashMap<>();

    public static void main(String[] args) {
        try (ServerSocket server = new ServerSocket(12345)) {
            System.out.println("Server started... waiting for clients.");

            while (true) {
                Socket client = server.accept();
                new ClientHandler(client).start(); // handle each client in a new thread
            }
        } catch (IOException e) {
            System.out.println("Server error: " + e.getMessage());
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String name;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                // First message from client = their name
                name = in.readLine();
                synchronized (clientWriters) {
                    clientWriters.add(out);
                    clientNames.put(socket, name);
                }

                broadcast( name + " joined the chat!");

                String msg;
                while ((msg = in.readLine()) != null) {
                    broadcast(name + ": " + msg);
                }
            } catch (IOException e) {
                System.out.println("Connection lost with " + name);
            } finally {
                // Client disconnected
                if (name != null) {
                    synchronized (clientWriters) {
                        clientWriters.remove(out);
                        clientNames.remove(socket);
                    }
                    broadcast( name + " left the chat.");
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }

        // Send message to all connected clients
        private void broadcast(String msg) {
            System.out.println(msg);
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(msg);
                }
            }
        }
    }
}
