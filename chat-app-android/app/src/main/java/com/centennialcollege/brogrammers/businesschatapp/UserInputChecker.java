package com.centennialcollege.brogrammers.businesschatapp;

/**
 * Created by Sergey-PC on 05.03.2018.
 */

public class UserInputChecker {
    public static final int MINIMUM_PASSWORD_LENGTH = 6;
    // Errors
    public static final String ERROR_THIS_FIELD_IS_REQUIED = "This field is required";
    // Email errors
    public static final String ERROR_EMAIL_INVALID = "This email address is invalid";
    // Password errors
    public static final String ERROR_PASSWORD_TOO_SHORT = "This password is too short," +
            " minimum length is " + MINIMUM_PASSWORD_LENGTH + " characters";

    public static boolean isEmpty(String text) {
        return text == null || text.length() == 0;
    }

    public static String checkEmail(String email) {
        if (isEmpty(email)) {
            return ERROR_THIS_FIELD_IS_REQUIED;
        } else if (email.length() < 3 || !email.contains("@") || !email.contains(".")) {
            return ERROR_EMAIL_INVALID;
        }
        return null;
    }

    public static String checkPassword(String password) {
        if (isEmpty(password)) {
            return ERROR_THIS_FIELD_IS_REQUIED;
        } else if (password.length() <= MINIMUM_PASSWORD_LENGTH) {
            return ERROR_PASSWORD_TOO_SHORT;
        }
        return null;
    }
}
