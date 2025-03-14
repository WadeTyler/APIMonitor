package net.tylerwade.backend.util;

import java.util.Random;

public class Util {

    public static String generateRandomVerificationCode(int length) {

        StringBuilder code = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            code.append(random.nextInt(10));
        }

        return code.toString();
    }
}
