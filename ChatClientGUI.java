import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClientGUI {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField inputField;
    private PrintWriter out;

    public ChatClientGUI(String serverIP, String name) {
        try {
            Socket socket = new Socket(serverIP, 12345);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // send name to server
            out.println(name);

            // GUI setup
            frame = new JFrame("Chat - " + name);
            chatArea = new JTextArea(20, 40);
            chatArea.setEditable(false);
            inputField = new JTextField();

            frame.add(new JScrollPane(chatArea), BorderLayout.CENTER);
            frame.add(inputField, BorderLayout.SOUTH);

            frame.pack();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            // input handler
            inputField.addActionListener(e -> {
                String msg = inputField.getText();
                out.println(msg);
                inputField.setText("");
            });

            // thread to listen for messages
            new Thread(() -> {
                try {
                    String msg;
                    while ((msg = in.readLine()) != null) {
                        chatArea.append(msg + "\n");
                    }
                } catch (IOException ex) {
                    chatArea.append("Disconnected from server.\n");
                }
            }).start();

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Unable to connect: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // ask for IP and Name at startup
        String serverIP = JOptionPane.showInputDialog("Enter Server IP:", "localhost");
        String name = JOptionPane.showInputDialog("Enter your name:");

        if (serverIP != null && name != null && !name.trim().isEmpty()) {
            new ChatClientGUI(serverIP, name);
        } else {
            JOptionPane.showMessageDialog(null, "IP and name are required!");
        }
    }
}
