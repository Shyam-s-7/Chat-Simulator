import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        try {
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            // Ask for server IP
            System.out.print("Enter server IP (or press Enter for localhost): ");
            String serverIp = keyboard.readLine();
            if (serverIp.trim().isEmpty()) {
                serverIp = "localhost"; // default
            }

            // Connect to server
            Socket socket = new Socket(serverIp, 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

            // Send name
            System.out.print("Enter your name: ");
            String name = keyboard.readLine();
            out.println(name);

            // Thread to keep listening for messages from server
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        System.out.println(msg);
                    }
                } catch (IOException e) {
                    // happens when socket closes
                }
                System.out.println("Disconnected from server.");
            }).start();

            // Main thread keeps sending messages
            String msg;
            while ((msg = keyboard.readLine()) != null) {
                if (msg.equalsIgnoreCase("exit")) {
                    out.println("exit"); // tell server you want to disconnect
                    System.out.println("You left the chat.");
                    socket.close();
                    break; // end loop
                }
                out.println(msg);
            }

        } catch (IOException e) {
            System.out.println("Could not connect to server: " + e.getMessage());
        }
    }
}
