package ru.karpyuk.desktopchat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ConsoleHelper {
    private static BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));

    public static void writeMessage(String message) {
        System.out.println(message);
    }

    public static String readString() {
        String s = null;
        while (s == null) {
            try {
                s = bufferedReader.readLine();
            } catch (IOException e) {
                System.out.println("Ошибка ввода строки");
            }
        }
        return s;
    }

    public static int readInt() {
        while(true) {
            try {
                return Integer.parseInt(readString());
            } catch (NumberFormatException e) {
                System.out.println("Ошибка ввода числа");
            }
        }
    }
}
