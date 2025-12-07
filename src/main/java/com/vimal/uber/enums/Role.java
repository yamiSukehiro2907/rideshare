package com.vimal.uber.enums;

public enum Role {
    USER,
    DRIVER;

    public static boolean isValid(String role) {
        try {
            Role.valueOf(role);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
