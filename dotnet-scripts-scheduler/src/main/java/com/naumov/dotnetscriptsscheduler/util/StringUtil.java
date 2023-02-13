package com.naumov.dotnetscriptsscheduler.util;

public final class StringUtil {

    public static String omitLongString(String std) {
        return std != null ? "<CONTENT OMITTED>" : null;
    }
}
