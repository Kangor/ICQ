package ICQ.client;

import ICQ.Connection;
import ICQ.ConsoleHelper;
import ICQ.Message;
import ICQ.MessageType;
import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    public Client() {
    }

    protected String getServerAddress() {
        ConsoleHelper.writeMessage("Введите адрес сервера: ");
        return ConsoleHelper.readString();
    }

    protected int getServerPort() {
        ConsoleHelper.writeMessage("Введите порт: ");
        return ConsoleHelper.readInt();
    }

    protected String getUserName() {
        ConsoleHelper.writeMessage("Введите имя пользователя: ");
        return ConsoleHelper.readString();
    }

    protected boolean shouldSendTextFromConsole() {
        return true;
    }

    protected Client.SocketThread getSocketThread() {
        return new Client.SocketThread();
    }

    protected void sendTextMessage(String text) {
        try {
            this.connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException var3) {
            ConsoleHelper.writeMessage("Во время отправки произошло исключение.");
            this.clientConnected = false;
        }

    }

    public void run() {
        Client.SocketThread socketThread = this.getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();

        try {
            synchronized(this) {
                this.wait();
            }
        } catch (InterruptedException var5) {
            var5.printStackTrace();
            ConsoleHelper.writeMessage("Ошибка потока!");
            System.exit(1);
        }

        if (this.clientConnected) {
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду ‘exit’.");

            while(this.clientConnected) {
                String writenText = ConsoleHelper.readString();
                if (writenText.equals("exit")) {
                    break;
                }

                if (this.shouldSendTextFromConsole()) {
                    this.sendTextMessage(writenText);
                }
            }
        } else {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        }

    }

    public static void main(String[] arg) {
        Client client = new Client();
        client.run();
    }

    public class SocketThread extends Thread {
        public SocketThread() {
        }

        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName) {
            ConsoleHelper.writeMessage("Участник с именем " + userName + " присоединился к чату.");
        }

        protected void informAboutDeletingNewUser(String userName) {
            ConsoleHelper.writeMessage("Участник с именем " + userName + " покинул чат.");
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected) {
            Client var2 = Client.this;
            synchronized(Client.this) {
                Client.this.clientConnected = clientConnected;
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException {
            Message message;
            do {
                message = Client.this.connection.receive();
                if (message.getType() == MessageType.NAME_REQUEST) {
                    Client.this.connection.send(new Message(MessageType.USER_NAME, Client.this.getUserName()));
                }

                if (message.getType() == MessageType.NAME_ACCEPTED) {
                    this.notifyConnectionStatusChanged(true);
                    return;
                }
            } while(message.getType() == MessageType.NAME_REQUEST || message.getType() == MessageType.NAME_ACCEPTED);

            throw new IOException("Unexpected MessageType");
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            Message message;
            do {
                message = Client.this.connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    this.processIncomingMessage(message.getData());
                }

                if (message.getType() == MessageType.USER_ADDED) {
                    this.informAboutAddingNewUser(message.getData());
                }

                if (message.getType() == MessageType.USER_REMOVED) {
                    this.informAboutDeletingNewUser(message.getData());
                }
            } while(message.getType() == MessageType.TEXT || message.getType() == MessageType.USER_ADDED || message.getType() == MessageType.USER_REMOVED);

            throw new IOException("Unexpected MessageType");
        }

        public void run() {
            try {
                Socket socket = new Socket(Client.this.getServerAddress(), Client.this.getServerPort());
                Client.this.connection = new Connection(socket);
                this.clientHandshake();
                this.clientMainLoop();
            } catch (ClassNotFoundException | IOException var2) {
                this.notifyConnectionStatusChanged(false);
            }

        }
    }
}