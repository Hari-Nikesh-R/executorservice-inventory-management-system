package org.example.exception;

import java.util.Scanner;

public final class InputExceptionHandler<T> {
    private final Scanner scanner;
    private String doubleValue;
    private String integerValue;

    public InputExceptionHandler(Scanner scanner) {
        this.scanner = scanner;
    }

    public T getUserInput(String failureMessage, Class<T> tClass, String inputMessageDescription) {
        try {
            return getInput(failureMessage, tClass, inputMessageDescription);
        } catch (Exception exception) {
            System.out.println("Unable to process, trying again...");
            return getInput(failureMessage, tClass, inputMessageDescription);
        }
    }

    private T getInput(String failureMessage, Class<T> tClass, String inputMessageDescription) {
        if (tClass == String.class) {
            System.out.println(inputMessageDescription);
            return tClass.cast(scanner.nextLine());
        } else if (tClass == Integer.class) {
            while (integerValue == null) {
                System.out.println(inputMessageDescription);
                integerValue = scanner.nextLine();
                if (integerValue.matches("\\d+")) {
                    Integer tempInteger = Integer.parseInt(integerValue);
                    integerValue = null;
                    return tClass.cast(tempInteger);
                } else {
                    integerValue = null;
                    System.out.println(failureMessage);
                }
            }
        } else if (tClass == Double.class) {
            while(doubleValue == null) {
                System.out.println(inputMessageDescription);
                doubleValue = scanner.nextLine();
                if (doubleValue.matches("[-+]?\\d*\\.\\d+([eE][-+]?\\d+)?") || doubleValue.matches("\\d+")) {
                    Double tempDouble = Double.parseDouble(doubleValue);
                    doubleValue = null;
                    return tClass.cast(tempDouble);
                }
                else {
                    doubleValue = null;
                    System.out.println(failureMessage);
                }
            }
        }
        return tClass.cast(null);
    }

//    public String getUserInput(Class<T> tClass) {
//        return scanner.nextLine();
//    }
//
//    public Integer getUserInput() {
//        return scanner.nextInt();
//
//    }
//    public T getUserInput(Double input) {
//
//    }
//    public T getUserInput(Long input) {
//
//    }


}
