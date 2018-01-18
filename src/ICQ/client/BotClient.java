package ICQ.client;

import ICQ.ConsoleHelper;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {

    public class BotSocketThread extends SocketThread {
        @Override
        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            sendTextMessage("Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды.");
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            if (message != null) {
                ConsoleHelper.writeMessage(message);
                if (message.contains(": ")) {
                    String[] nameAndText = message.split(": ");
                    if (nameAndText.length == 2) {
                        SimpleDateFormat date = null;
                        if (nameAndText[1].equals("дата")) {
                            date = new SimpleDateFormat("d.MM.YYYY");
                            sendTextMessage("Информация для " + nameAndText[0] + ": " + date.format(Calendar.getInstance().getTime()));
                        }
                        if (nameAndText[1].equals("день")) {
                            date = new SimpleDateFormat("d");
                            sendTextMessage("Информация для " + nameAndText[0] + ": " + date.format(Calendar.getInstance().getTime()));
                        }
                        if (nameAndText[1].equals("месяц")) {
                            date = new SimpleDateFormat("MMMM");
                            sendTextMessage("Информация для " + nameAndText[0] + ": " + date.format(Calendar.getInstance().getTime()));
                        }
                        if (nameAndText[1].equals("год")) {
                            date = new SimpleDateFormat("YYYY");
                            sendTextMessage("Информация для " + nameAndText[0] + ": " + date.format(Calendar.getInstance().getTime()));
                        }
                        if (nameAndText[1].equals("время")) {
                            date = new SimpleDateFormat("H:mm:ss");
                            sendTextMessage("Информация для " + nameAndText[0] + ": " + date.format(Calendar.getInstance().getTime()));
                        }
                        if (nameAndText[1].equals("час")) {
                            date = new SimpleDateFormat("H");
                            sendTextMessage("Информация для " + nameAndText[0] + ": " + date.format(Calendar.getInstance().getTime()));
                        }
                        if (nameAndText[1].equals("минуты")) {
                            date = new SimpleDateFormat("m");
                            sendTextMessage("Информация для " + nameAndText[0] + ": " + date.format(Calendar.getInstance().getTime()));
                        }
                        if (nameAndText[1].equals("секунды")) {
                            date = new SimpleDateFormat("s");
                            sendTextMessage("Информация для " + nameAndText[0] + ": " + date.format(Calendar.getInstance().getTime()));
                        }
                    }
                }
            }
        }
    }

    @Override
    protected SocketThread getSocketThread() {
        return new BotSocketThread();
    }

    @Override
    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    @Override
    protected String getUserName() {
        String botName = "date_bot_" + ((int) (Math.random() * 100));
        return botName;
    }

    public static void main(String[] args) {
        new BotClient().run();

    }
}
