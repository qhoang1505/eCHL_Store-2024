package Controller.Chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server {
    private static final int PORT = 2005; //port locallhost la 2005
    public static Map<String, ObjectOutputStream> clients = new HashMap<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            System.out.println("Server is running on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ObjectOutputStream outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream inputStream = new ObjectInputStream(clientSocket.getInputStream());

                String username = (String) inputStream.readObject();
                clients.put(username, outputStream);

                Thread clientHandler = new Thread(new ClientHandler(clientSocket, inputStream, username));
                clientHandler.start();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {
        private Socket clientSocket;
        private ObjectInputStream inputStream;
        private String username; // nguoi nhan

        public ClientHandler(Socket clientSocket, ObjectInputStream inputStream, String username) {
            this.clientSocket = clientSocket;
            this.inputStream = inputStream;
            this.username = username;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String recipient = (String) inputStream.readObject();
                    String messageContent = (String) inputStream.readObject();

                    System.out.println("Received message from " + username + " to " + recipient + ": " + messageContent);

                    ObjectOutputStream recipientOutputStream = clients.get(recipient);
                    if (recipientOutputStream != null) {
                        recipientOutputStream.writeObject(username + ": " + messageContent);
                        recipientOutputStream.flush();
                        System.out.println("Message forwarded to " + recipient);
                    } else {
                        //đưa tin nhắn vô database nếu người dùng không online (kèm luôn cả username người nhắn và người nhận)
                        System.out.println("Recipient not found: " + recipient);
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    System.out.println(username + " is closed server!");
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
