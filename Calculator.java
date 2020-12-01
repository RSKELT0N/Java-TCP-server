package company;

import java.security.InvalidParameterException;

public class Calculator {

    public static double calculate(String line) throws InvalidParameterException {
        double x, y;
        char operator;
        String[] parts = line.split(" ");

        if (parts.length != 3)
            throw new InvalidParameterException("invalid syntax");

        try {
            x = Double.parseDouble(parts[0]);
            y = Double.parseDouble(parts[2]);
            operator = parts[1].charAt(0);
        } catch (Exception e) {
            throw new InvalidParameterException("converting values failed");
        }

        switch (operator) {
            case '+':
                return x + y;
            case '-':
                return x - y;
            case '*':
                return x * y;
            case '/':
                if (y != 0)
                    return x / y;
            default:
                throw new InvalidParameterException("unknown operator");
        }
    }

    public static String getBinary(int x) throws InvalidParameterException {
        if (x < 0)
            throw new InvalidParameterException("the parameter can't be less than 0");

        String result = "";
        while (x != 0) {
            result = (x & 1) + result;
            x >>= 1;
        }

        //10 = 1010 = x
        //1010
        //0001
        //0000 -> 0

        //0101
        //0001
        //0001 -> 1

        //0010
        //0001
        //0000 -> 0

        //0001
        //0001
        //0001 -> 1

        //0000

        //while (x != 0) {
        //    result = (x % 2) + result;
        //    x /= 2;
        //}

        //10 mod 2 -> 0
        //5 mod 2 ->  1
        //2 mod 2 ->  0
        //1 mod 2 ->  1
        //0
        return result;
    }
}
