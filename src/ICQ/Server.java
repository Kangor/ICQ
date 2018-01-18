package ICQ;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap();

    public Server() {
    }

    public static void sendBroadcastMessage(Message message) {
        Iterator var1 = connectionMap.entrySet().iterator();

        while(var1.hasNext()) {
            Entry entry = (Entry)var1.next();

            try {
                ((Connection)entry.getValue()).send(message);
            } catch (IOException var4) {
                ConsoleHelper.writeMessage("Exception send message!");
            }
        }

    }

    public static void main(String[] args) {
        ConsoleHelper.writeMessage("Input server port: ");

        try {
            ServerSocket serSoc = new ServerSocket(ConsoleHelper.readInt());
            Throwable var2 = null;

            try {
                ConsoleHelper.writeMessage("Server started!");

                while(true) {
                    (new Server.Handler(serSoc.accept())).start();
                }
            } catch (Throwable var11) {
                var2 = var11;
                throw var11;
            } finally {
                if (serSoc != null) {
                    if (var2 != null) {
                        try {
                            serSoc.close();
                        } catch (Throwable var10) {
                            var2.addSuppressed(var10);
                        }
                    } else {
                        serSoc.close();
                    }
                }

            }
        } catch (Exception var13) {
            ConsoleHelper.writeMessage("Exception! Server was stopped");
        }
    }

    private static class Handler extends Thread {
        private Socket socket;

        public Handler(Socket socket) {
            this.socket = socket;
        }

        private String serverHandshake(Connection connection) throws IOException, ClassNotFoundException {
            Message messageWithName;
            do {
                connection.send(new Message(MessageType.NAME_REQUEST));
                messageWithName = connection.receive();
            } while(!messageWithName.getType().equals(MessageType.USER_NAME) || messageWithName.getData().isEmpty() || Server.connectionMap.containsKey(messageWithName.getData()));

            Server.connectionMap.put(messageWithName.getData(), connection);
            connection.send(new Message(MessageType.NAME_ACCEPTED));
            return messageWithName.getData();
        }

        private void sendListOfUsers(Connection connection, String userName) throws IOException {
            Iterator var3 = Server.connectionMap.entrySet().iterator();

            while(var3.hasNext()) {
                Entry<String, Connection> entry = (Entry)var3.next();
                if (!((String)entry.getKey()).equals(userName)) {
                    connection.send(new Message(MessageType.USER_ADDED, (String)entry.getKey()));
                }
            }

        }

        private void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException {
            while(true) {
                Message newMessage = connection.receive();
                if (newMessage != null && newMessage.getType() == MessageType.TEXT) {
                    Server.sendBroadcastMessage(new Message(newMessage.getType(), userName + ": " + newMessage.getData()));
                } else {
                    ConsoleHelper.writeMessage("Exception! Strange type of message!");
                }
            }
        }

        public void run() {
            if (this.socket != null && this.socket.getRemoteSocketAddress() != null) {
                ConsoleHelper.writeMessage("New connect: " + this.socket.getRemoteSocketAddress());
            }

            String userName = null;

            try {
                Connection connection = new Connection(this.socket);
                Throwable var3 = null;

                try {
                    userName = this.serverHandshake(connection);
                    Server.sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                    this.sendListOfUsers(connection, userName);
                    this.serverMainLoop(connection, userName);
                } catch (Throwable var21) {
                    var3 = var21;
                    throw var21;
                } finally {
                    if (connection != null) {
                        if (var3 != null) {
                            try {
                                connection.close();
                            } catch (Throwable var20) {
                                var3.addSuppressed(var20);
                            }
                        } else {
                            connection.close();
                        }
                    }

                }
            } catch (ClassNotFoundException | IOException var23) {
                ConsoleHelper.writeMessage("Произошла ошибка при обмене данными с удаленным адресом.");
            } finally {
                if (userName != null) {
                    Server.connectionMap.remove(userName);
                    Server.sendBroadcastMessage(new Message(MessageType.USER_REMOVED, userName));
                }

                ConsoleHelper.writeMessage("Cоединение с удаленным адресом закрыто.");
            }

        }
    }
}
