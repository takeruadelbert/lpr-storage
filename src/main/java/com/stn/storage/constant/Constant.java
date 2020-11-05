package com.stn.storage.constant;

import java.io.File;

public class Constant {
    public static final String DIRECTORY_SEPARATOR = "/";
    public static final String EMPTY_STRING = "";
    public static final String WHITESPACE = " ";
    public static final String COMMA = ",";
    public static final String PARENT_DIRECTORY = new File(System.getProperty("user.dir")).getParent() != null ? new File(System.getProperty("user.dir")).getParent() : new File(System.getProperty("user.dir")).toString();
}
