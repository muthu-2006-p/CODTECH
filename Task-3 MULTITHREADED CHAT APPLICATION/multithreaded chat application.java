import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import java.util.*;

public class Main {

    // ------------- SERVER SECTION ----------------
    static class Server {
        private ServerSocket serverSocket;
        private final ArrayList<ClientHandler> clients = new ArrayList<>();

        public Server(int port) {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Server started on port " + port);
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                    clients.add(clientHandler);
                    new Thread(clientHandler).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void broadcast(String message, ClientHandler excludeUser) {
            for (ClientHandler client : clients) {
                if (client != excludeUser) {
                    client.sendMessage(message);
                }
            }
        }
    }

    static class ClientHandler implements Runnable {
        private Socket socket;
        private Server server;
        private PrintWriter out;
        private BufferedReader in;

        public ClientHandler(Socket socket, Server server) {
            this.socket = socket;
            this.server = server;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                String message;
                while ((message = in.readLine()) != null) {
                    server.broadcast(message, this);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMessage(String message) {
            out.println(message);
        }
    }

    // ------------- CLIENT SECTION ----------------
    static class ClientGUI {
        private JFrame frame;
        private JTextArea chatArea;
        private JTextField inputField;
        private PrintWriter out;
        private BufferedReader in;
        private Socket socket;
        private String username;

        public ClientGUI(String address, int port, String username) {
            this.username = username;
            createGUI();
            connectToServer(address, port);
            new Thread(this::listenForMessages).start();
        }

        private void createGUI() {
            frame = new JFrame("Chat - " + username);
            frame.setSize(400, 500);
            frame.setLayout(new BorderLayout());

            chatArea = new JTextArea();
            chatArea.setEditable(false);
            JScrollPane scrollPane = new JScrollPane(chatArea);
            frame.add(scrollPane, BorderLayout.CENTER);

            inputField = new JTextField();
            inputField.addActionListener(e -> {
                String message = username + ": " + inputField.getText();
                out.println(message);
                inputField.setText("");
            });
            frame.add(inputField, BorderLayout.SOUTH);

            frame.setVisible(true);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }

        private void connectToServer(String address, int port) {
            try {
                socket = new Socket(address, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void listenForMessages() {
            String message;
            try {
                while ((message = in.readLine()) != null) {
                    chatArea.append(message + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // ------------- MAIN TO START SERVER OR CLIENT ----------------
    public static void main(String[] args) {
        String[] options = {"Start Server", "Start Client"};
        int choice = JOptionPane.showOptionDialog(null, "Choose Mode", "Chat App",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        if (choice == 0) {
            String portStr = JOptionPane.showInputDialog("Enter Port Number (e.g., 1234):");
            int port = Integer.parseInt(portStr);
            new Thread(() -> new Server(port)).start();
        } else if (choice == 1) {
            String address = JOptionPane.showInputDialog("Enter Server IP (e.g., localhost):");
            String portStr = JOptionPane.showInputDialog("Enter Port Number (e.g., 1234):");
            String username = JOptionPane.showInputDialog("Enter Your Name:");
            int port = Integer.parseInt(portStr);
            SwingUtilities.invokeLater(() -> new ClientGUI(address, port, username));
        }
    }
}
