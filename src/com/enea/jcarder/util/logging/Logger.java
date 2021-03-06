/*
 * JCarder -- cards Java programs to keep threads disentangled
 *
 * Copyright (C) 2006-2007 Enea AB
 * Copyright (C) 2007 Ulrik Svensson
 * Copyright (C) 2007 Joel Rosdahl
 *
 * This program is made available under the GNU GPL version 2, with a special
 * exception for linking with JUnit. See the accompanying file LICENSE.txt for
 * details.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.
 */

package com.enea.jcarder.util.logging;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A simple logging framework.
 *
 * We use our own simple Logging framework for several reasons:
 *  - To avoid interfering with logging from the user application. Even if we
 * were using Log4J instead of java.util.Logging.*, JCarder might interfere with
 * for example Log4J system properties.
 *  - The default java.util.logging.LogManager is reset by a shutdown hook and
 * there is no fixed order in which the shutdown hooks are executed.
 *  - Minimizing the usage of the Java standard library improves performance and
 * minimizes the risk of deadlock if the standard library is instrumented by
 * JCarder.
 */
public final class Logger {
    public static enum Level {
        SEVERE,
        WARNING,
        INFO,
        CONFIG,
        FINE,
        FINER,
        FINEST;

        /**
         * Parse a string and return the corresponding log level.
         *
         * @param string The string to parse.
         * @return
         */
        public static Level fromString(String string) {
            for (Level level : values()) {
                if (string.equalsIgnoreCase(level.toString())) {
                    return level;
                }
            }
            return null;
        }

        public static String getEnumeration() {
            StringBuffer sb = new StringBuffer();
            boolean first = true;
            for (Level level : values()) {
                if (first) {
                    first = false;
                } else {
                    sb.append(", ");
                }
                sb.append(level.toString());
            }
            return sb.toString();
        }
    }

    final Collection<Handler> mHandlers;
    final Level mLevel;

    /**
     * Constructor setting default value of log level.
     *
     * The default is to log everything, i.e., log level FINEST.
     *
     * @param handlers Log handlers. null means no handlers.
     */
    public Logger(Collection<Handler> handlers) {
        this(handlers, Level.FINEST);
    }

    /**
     * Constructor.
     *
     * @param handlers Log handlers. null means no handlers.
     * @param logLevel Log level.
     */
    public Logger(Collection<Handler> handlers, Level logLevel) {
        mLevel = logLevel;
        mHandlers = new ArrayList<Handler>();
        if (handlers != null) {
            // Create a copy of the provided collection instead of sharing
            // it, in order to make sure that mHandlers is immutable and
            // thread safe.
            mHandlers.addAll(handlers);
        }
    }

    /**
     * Check whether a message with a certain level would be logged.
     *
     * @param level The level.
     * @return True if the message would be logged, otherwise false.
     */
    public boolean isLoggable(Level level) {
        return level.compareTo(mLevel) <= 0;
    }

    /**
     * Log a message with level SEVERE.
     *
     * @param message The message.
     */
    public void severe(String message) {
        publishLog(Level.SEVERE, message);
    }


    /**
     * Log a message with level WARNING.
     *
     * @param message The message.
     */
    public void warning(String message) {
        publishLog(Level.WARNING, message);
    }


    /**
     * Log a message with level INFO.
     *
     * @param message The message.
     */
    public void info(String message) {
        publishLog(Level.INFO, message);
    }


    /**
     * Log a message with level CONFIG.
     *
     * @param message The message.
     */
    public void config(String message) {
        publishLog(Level.CONFIG, message);
    }


    /**
     * Log a message with level FINE.
     *
     * @param message The message.
     */
    public void fine(String message) {
        publishLog(Level.FINE, message);
    }


    /**
     * Log a message with level FINER.
     *
     * @param message The message.
     */
    public void finer(String message) {
        publishLog(Level.FINER, message);
    }

    /**
     * Log a message with level FINEST.
     *
     * @param message The message.
     */
    public void finest(String message) {
        publishLog(Level.FINEST, message);
    }

    private void publishLog(Level level, String message) {
        if (isLoggable(level)) {
            for (Handler handler : mHandlers) {
                handler.publish(level, message);
            }
        }
    }
}
