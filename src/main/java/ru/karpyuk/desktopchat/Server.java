package ru.karpyuk.desktopchat;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            ConsoleHelper.writeMessage("Connecting with : " + socket.getRemoteSocketAddress());
            String userName = "";
            try (Connection connection = new Connection(socket))
            {
                userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
            } catch(IOException | ClassNotFoundException e) {
                ConsoleHelper.writeMessage("Error");
            }
            if(!userName.equals("")) {
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
            }
            ConsoleHelper.writeMessage("Connecting interrupted");
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            while(true) {
                connection.send(new Message(MessageType.NAME_REQUEST, "Enter your nickname"));
                Message message = connection.receive();
                if (message.getType() == MessageType.USER_NAME && !connectionMap.containsKey(message.getData())
                        && !message.getData().equals("")) {
                    connectionMap.put(message.getData(), connection);
                    connection.send(new Message(MessageType.NAME_ACCEPTED, "Nickname accepted"));
                    return message.getData();
                }
            }
        }

        private void notifyUsers(Connection connection, String userName) {
            for(Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
                try {
                    if (!userName.equals(pair.getKey()))
                        connection.send(new Message(MessageType.USER_ADDED, pair.getKey()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + message.getData()));
                }
                else {
                    ConsoleHelper.writeMessage("Error");
                }
            }
        }
    }

    public static void main(String[] args) {
        int port = ConsoleHelper.readInt();
        try (ServerSocket server = new ServerSocket(port);)
        {
            System.out.println("Server start");
            while(true) {
                new Handler(server.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendBroadcastMessage(Message message) {
        for(Map.Entry<String, Connection> pair : connectionMap.entrySet()) {
            try {
                Connection connection = pair.getValue();
                connection.send(message);
            } catch (IOException e) {
                System.out.println("Message failed to send");
            }
        }

    }
}
