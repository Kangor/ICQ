package ICQ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader bufferedReader;

    public ConsoleHelper() {
    }

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String s = null;

        while(true) {
            try {
                s = bufferedReader.readLine();
                return s;
            } catch (IOException var2) {
                System.out.println("Произошла ошибка при попытке ввода текста. Попробуйте еще раз.");
            }
        }
    }

    public static int readInt() {
        boolean var0 = false;

        while(true) {
            try {
                int i = Integer.parseInt(readString());
                return i;
            } catch (NumberFormatException var2) {
                System.out.println("Произошла ошибка при попытке ввода числа. Попробуйте еще раз.");
            }
        }
    }

    static {
        bufferedReader = new BufferedReader(new InputStreamReader(System.in));
    }
}
