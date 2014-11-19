package com.messenger.fade.util;

import android.util.TimingLogger;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MLog {

    private static boolean isEnabled = true;
    private final static Map<String, TimingLogger> timingLoggers = new HashMap<String, TimingLogger>();


    private MLog() {
    }


    public static void setEnabled(final String packageName,
                                  final boolean enable) {

        isEnabled = enable;
        if (!enable) {
            disableSystemOut();
            Logger.getLogger(packageName).setLevel(Level.OFF);
        } else {
            Logger.getLogger(packageName).setLevel(Level.FINER);
        }
    }

    public static boolean isEnabled() {
        return isEnabled;
    }


    public static void v(final String tag, final Object... vals) {

        if (isEnabled) {
            Logger.getLogger(tag).log(Level.FINER, buildString(vals));
        }
    }


    public static void d(final String tag, final Object... vals) {

        if (isEnabled) {
            Logger.getLogger(tag).log(Level.FINE, buildString(vals));
        }
    }


    public static void i(final String tag, final Object... vals) {

        if (isEnabled) {
            Logger.getLogger(tag).log(Level.INFO, buildString(vals));
        }
    }


    public static void w(final String tag, final Object... vals) {

        if (isEnabled) {
            Logger.getLogger(tag).log(Level.WARNING, buildString(vals));
        }
    }


    public static void e(final String tag, final Object... vals) {

        if (isEnabled) {
            Logger.getLogger(tag).log(Level.SEVERE, buildString(vals));
        }
    }


    public static void e(final String tag, final String log, final Throwable t) {

        if (isEnabled) {
            final Logger logger = Logger.getLogger(tag);
            logger.log(Level.SEVERE, log);
            logger.log(Level.SEVERE, t.toString());
            StackTraceElement[] eles = t.getStackTrace();
            for (StackTraceElement ele : eles) {
                logger.log(Level.SEVERE, ele.toString());
            }
        }
    }


    public static void startProfiling(String event) {

        if (isEnabled) {
            TimingLogger logger = new TimingLogger("Profile", event);
            timingLoggers.put(event, logger);
        }
    }


    public static void addProfilingSplit(String event, String splitLabel) {

        if (isEnabled) {
            TimingLogger logger = timingLoggers.get(event);
            if (logger != null) {
                logger.addSplit(splitLabel);
            }
        }
    }


    public static void endProfiling(String event) {

        if (isEnabled) {
            TimingLogger logger = timingLoggers.remove(event);
            if (logger != null)
                logger.dumpToLog();
        }
    }


    private static String buildString(Object... strings) {

        final StringBuilder sb = new StringBuilder();
        for (Object s : strings) {
            sb.append(s);
        }
        return sb.toString();
    }


    public static void destroy() {

        timingLoggers.clear();
    }


    private static void disableSystemOut() {

		/*
         * disable System.out.print.....
		 */
        System.setOut(new PrintStream(new OutputStream() {

            public void write(int b) {

                // do nothing
            }
        }));

        System.setErr(new PrintStream(new OutputStream() {

            public void write(int b) {

                // do nothing
            }
        }));
    }

}
