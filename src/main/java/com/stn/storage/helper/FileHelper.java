package com.stn.storage.helper;

import com.stn.storage.constant.Constant;

import java.io.File;

public class FileHelper {
    public static String removeRequestParamFromExtension(String filename) {
        if (filename == null) return null;
        int index = filename.indexOf('?');
        if (index == -1) return filename;
        return filename.substring(0, index);
    }

    public static void autoCreateDir(String path) {
        try {
            File assetDir = new File(path);
            if (!assetDir.exists()) {
                assetDir.mkdirs();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static String[] splitDataEncodedBase64(String encodedBase64) {
        return encodedBase64.split(Constant.COMMA);
    }

    public static String getRawDataFromEncodedBase64(String encodedBase64) {
        String[] temp = splitDataEncodedBase64(encodedBase64);
        return temp[1];
    }
}
