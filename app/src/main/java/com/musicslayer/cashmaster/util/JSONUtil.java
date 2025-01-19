package com.musicslayer.cashmaster.util;

import org.json.JSONObject;

public class JSONUtil {
    public static boolean isValidJSON(String s) {
        // Returns whether or not "s" can be parsed as a valid JSON object.
        try {
            new JSONObject(s);
            return true;
        }
        catch(Exception ignored) {
            return false;
        }
    }
}
