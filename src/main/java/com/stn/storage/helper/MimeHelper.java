package com.stn.storage.helper;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MimeHelper {
    public static Map<String, String> extMine = new HashMap<>();

    MimeHelper() {
        extMine.put(".aac", "audio/aac");
        extMine.put(".abw", "application/x-abiword");
        extMine.put(".arc", "application/x-freearc");
        extMine.put(".avi", "video/x-msvideo");
        extMine.put(".azw", "application/vnd.amazon.ebook");
        extMine.put(".bin", "application/octet-stream");
        extMine.put(".bmp", "image/bmp");
        extMine.put(".bz", "application/x-bzip");
        extMine.put(".bz2", "application/x-bzip2");
        extMine.put(".csh", "application/x-csh");
        extMine.put(".css", "text/css");
        extMine.put(".csv", "text/csv");
        extMine.put(".doc", "application/msword");
        extMine.put(".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        extMine.put(".eot", "application/vnd.ms-fontobject");
        extMine.put(".epub", "application/epub+zip");
        extMine.put(".gz", "application/gzip");
        extMine.put(".gif", "image/gif");
        extMine.put(".htm", "text/html");
        extMine.put(".html", "text/html");
        extMine.put(".ico", "image/vnd.microsoft.icon");
        extMine.put(".ics", "text/calendar");
        extMine.put(".jar", "application/java-archive");
        extMine.put(".jpeg", "image/jpeg");
        extMine.put(".jpg", "image/jpeg");
        extMine.put(".js", "text/javascript");
        extMine.put(".json", "application/json");
        extMine.put(".jsonld", "application/ld+json");
        extMine.put(".mid", "audio/midi");
        extMine.put(".midi", "audio/x-midi");
        extMine.put(".mjs", "text/javascript");
        extMine.put(".mp3", "audio/mpeg");
        extMine.put(".mpeg", "video/mpeg");
        extMine.put(".mpkg", "application/vnd.apple.installer+xml");
        extMine.put(".odp", "application/vnd.oasis.opendocument.presentation");
        extMine.put(".ods", "application/vnd.oasis.opendocument.spreadsheet");
        extMine.put(".odt", "application/vnd.oasis.opendocument.text");
        extMine.put(".oga", "audio/ogg");
        extMine.put(".ogv", "video/ogg");
        extMine.put(".ogx", "application/ogg");
        extMine.put(".opus", "audio/opus");
        extMine.put(".otf", "font/otf");
        extMine.put(".png", "image/png");
        extMine.put(".pdf", "application/pdf");
        extMine.put(".php", "application/x-httpd-php");
        extMine.put(".ppt", "application/vnd.ms-powerpoint");
        extMine.put(".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        extMine.put(".rar", "application/vnd.rar");
        extMine.put(".rtf", "application/rtf");
        extMine.put(".sh", "application/x-sh");
        extMine.put(".svg", "image/svg+xml");
        extMine.put(".swf", "application/x-shockwave-flash");
        extMine.put(".tar", "application/x-tar");
        extMine.put(".tif", "image/tiff");
        extMine.put(".tiff", "image/tiff");
        extMine.put(".ts", "video/mp2t");
        extMine.put(".ttf", "font/ttf");
        extMine.put(".txt", "text/plain");
        extMine.put(".vsd", "application/vnd.visio");
        extMine.put(".wav", "audio/wav");
        extMine.put(".weba", "audio/webm");
        extMine.put(".webm", "video/webm");
        extMine.put(".webp", "image/webp");
        extMine.put(".woff", "font/woff");
        extMine.put(".woff2", "font/woff2");
        extMine.put(".xhtml", "application/xhtml+xml");
        extMine.put(".xls", "application/vnd.ms-excel");
        extMine.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        extMine.put(".xml", "application/xml");
        extMine.put(".xul", "application/vnd.mozilla.xul+xml");
        extMine.put(".zip", "application/zip");
        extMine.put(".3gp", "video/3gpp");
        extMine.put(".3g2", "video/3gpp2");
        extMine.put(".7z", "application/x-7z-compressed");
    }

    public static String guess(String extension) {
        return extMine.get(extension);
    }
}
