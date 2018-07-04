package utils;

import org.apache.commons.lang3.StringUtils;

public class Utilities {
    
    public static boolean isNumeric(String stringValue){
        return StringUtils.isNumeric(stringValue);
    }
    
    public static boolean isInteger(String stringValue) {
        if (stringValue == null) {
            return false;
        }
        int length = stringValue.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (stringValue.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = stringValue.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }
    
    public static boolean isDouble(String stringValue) {
        try {
            Double.parseDouble(stringValue);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
