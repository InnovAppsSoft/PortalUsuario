package com.marlon.portalusuario.floating_window;

import android.annotation.SuppressLint;

import java.util.Locale;

public class Util {
    public static long calcDownSpeed(long timeTaken, long downBytes, long upBytes) {
        long downSpeed = 0;

        if (timeTaken > 0) {
            downSpeed = downBytes * 1000 / timeTaken;
        }
        return downSpeed;
    }

    public static long calcUpSpeed(long timeTaken, long downBytes, long upBytes) {
        long upSpeed = 0;

        if (timeTaken > 0) {
            upSpeed = upBytes * 1000 / timeTaken;
        }
        return upSpeed;
    }

    public static long calcTotalSpeed(long timeTaken, long downBytes, long upBytes) {
        long totalSpeed = 0;

        long totalBytes = downBytes + upBytes;

        if (timeTaken > 0) {
            totalSpeed = totalBytes * 1000 / timeTaken;
        }
        return totalSpeed;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < 0) return "0 B";
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.getDefault(), "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }
}
