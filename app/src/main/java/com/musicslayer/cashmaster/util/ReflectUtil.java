package com.musicslayer.cashmaster.util;

import android.app.Activity;

public class ReflectUtil {
    public static <T> T constructDialogInstance(Class<T> clazz, Activity activity, Object... args) {
        try {
            Object[] argArray = new Object[args.length + 1];
            Class<?>[] argClassArray = new Class<?>[args.length + 1];

            // All our Dialog classes have the first argument class as "Activity".
            argArray[0] = activity;
            argClassArray[0] = Activity.class;

            for(int i = 0; i < args.length; i++) {
                argArray[i + 1] = args[i];
                argClassArray[i + 1] = args[i].getClass();
            }

            return clazz.getConstructor(argClassArray).newInstance(argArray);
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            throw new IllegalStateException(e);
        }
    }
}