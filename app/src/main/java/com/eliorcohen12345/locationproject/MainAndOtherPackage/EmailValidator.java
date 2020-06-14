package com.eliorcohen12345.locationproject.MainAndOtherPackage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator {

    private static volatile EmailValidator sInstance;

    private EmailValidator() {
        if (sInstance != null) {
            throw new RuntimeException("Use getInstance() method to get the single instance of this class.");
        }
    }

    public static EmailValidator getInstance() {
        if (sInstance == null) {
            synchronized (EmailValidator.class) {
                if (sInstance == null) sInstance = new EmailValidator();
            }
        }

        return sInstance;
    }

    private static String EMAIL_PATTERN =
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+";
    private static Pattern pattern;
    private static Matcher matcher;

    public  boolean isValidEmail(final String password) {
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();
    }

}
