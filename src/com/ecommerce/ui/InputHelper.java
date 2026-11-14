package com.ecommerce.ui;

import java.util.Scanner;

/** Reads keyboard input safely: bad numbers are caught and the user is asked again. */
public class InputHelper {

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public String readNonEmpty(String prompt) {
        while (true) {
            String value = readLine(prompt);
            if (!value.isEmpty()) {
                return value;
            }
            ConsoleMenu.printError("This field cannot be empty.");
        }
    }

    public int readInt(String prompt) {
        while (true) {
            String value = readLine(prompt);
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                ConsoleMenu.printError("'" + value + "' is not a whole number. Please try again.");
            }
        }
    }

    public int readInt(String prompt, int min, int max) {
        while (true) {
            int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            ConsoleMenu.printError("Please enter a number from " + min + " to " + max + ".");
        }
    }

    public double readDouble(String prompt, double min) {
        while (true) {
            String value = readLine(prompt);
            try {
                double number = Double.parseDouble(value);
                if (number >= min) {
                    return number;
                }
                ConsoleMenu.printError("Please enter a number of at least " + min + ".");
            } catch (NumberFormatException e) {
                ConsoleMenu.printError("'" + value + "' is not a number. Please try again.");
            }
        }
    }

    public boolean confirm(String prompt) {
        while (true) {
            String value = readLine(prompt + " (y/n): ").toLowerCase();
            if (value.equals("y") || value.equals("yes")) {
                return true;
            }
            if (value.equals("n") || value.equals("no")) {
                return false;
            }
            ConsoleMenu.printError("Please type y or n.");
        }
    }

    public void pause() {
        readLine("\nPress Enter to continue...");
    }
}
