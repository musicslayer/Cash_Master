package com.musicslayer.cashmaster.util;

import com.musicslayer.cashmaster.app.App;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class FileUtil {
    public static File writeTempFile(String s) {
        // Returns a temp file with the String written to it.
        File file;
        try {
            file = File.createTempFile("CashMaster_TextFile_", ".txt", new File(App.cacheDir)); // TODO CashMater
            FileUtils.writeStringToFile(file, s, StandardCharsets.UTF_8);
        }
        catch(Exception e) {
            ThrowableUtil.processThrowable(e);
            file = null;
        }

        return file;
    }
}
