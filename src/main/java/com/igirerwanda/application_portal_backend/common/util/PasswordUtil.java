package com.igirerwanda.application_portal_backend.common.util;

import java.util.regex.Pattern;

public class PasswordUtil {
    private static final String STRONG_PASSWORD_PATTERN = 
        "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{12,}$";
    
    private static final Pattern pattern = Pattern.compile(STRONG_PASSWORD_PATTERN);
    
    public static boolean isStrongPassword(String password) {
        return password != null && pattern.matcher(password).matches();
    }
    
    public static String getPasswordRequirements() {
        return "Password must be at least 12 characters with uppercase, lowercase, number and special character";
    }
}
