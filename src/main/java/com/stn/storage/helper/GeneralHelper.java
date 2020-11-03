package com.stn.storage.helper;

import java.security.SecureRandom;

public class GeneralHelper {
    private static final String ALPHANUMERIC_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String generateToken() {
        return secureRandom.ints(32, 0, ALPHANUMERIC_CHAR.length()).mapToObj(ALPHANUMERIC_CHAR::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }
}
