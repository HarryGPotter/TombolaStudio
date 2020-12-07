/*
 * Copyright (c) 2018 Harry G potter (harry.g.potter@gmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, 
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND 
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, 
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package harrygpotter.tombola.tombolalib;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * TSimpleLogger is a quite simple class helping to manage the important task of
 * take trace (that is, log) of actions made by all maior TombolaLib
 * methods.&nbsp;I know I could have chosen to use many beautiful, complex,
 * logger solutions and packages already available for Java, but I preferred to
 * reduce to the minimum external dependencies and keep things as simple as
 * possible.&nbsp;Main principles to use this class and its objects follows:
 * <ul>
 * <li>Each library user can prepare his own logger objects using one of the
 * overloaded static
 * {@linkplain TSimpleLogger#prepareLogger(java.lang.String, harrygpotter.tombola.tombolalib.ILogger.TLogLevel, java.io.PrintStream)}
 * method available. Each log object will be registered with a symbolic name and
 * this name will allow one or more objects within a program to use and send
 * messages to the same logger. Static method
 * {@linkplain TSimpleLogger#getLoggerByName(String)} is here to serve in this
 * way. That's it, simple.</li>
 * <li>Standard, default, simple logger have been already provided with no need
 * to pass through the
 * {@linkplain TSimpleLogger#prepareLogger(java.lang.String, harrygpotter.tombola.tombolalib.ILogger.TLogLevel, java.io.PrintStream)}
 * phase. They have standard names also stored in public final strings:
 * <ul>
 * <li>A default logger used by ISetFactory objects to log series generation
 * events</li>
 * <li>A default logger used by TGame objects to log tombola match related
 * events</li>
 * <li>a generic logger that simply put messages on standard output stream</li>
 * <li>a generic logger that simply put messages on standard error stream</li>
 * <li>a generic logger that simply discard received messages as the mythical
 * /dev/null destination</li>
 * </ul></li></ul>
 *
 * @author Harry G. Potter (harry.g.potter@]mail.com)
 * @version 1.1
 * @since 1.8
 */
public class TSimpleLogger implements ILogger {

    /**
     * The identifying name for the default log that could be used to trace
     * Tombola matches
     */
    public static final String DEFAULT_GAME_LOGGER = "DefaultGameLog";

    /**
     * The identifying name for the default log that could be used to trace
     * SetFactory activities during card series generation.
     */
    public static final String DEFAULT_FACTORY_LOGGER = "DefaulFactoryLog";

    /**
     * The identifying name of a simple log sending its messages to the standard
     * output
     */
    public static final String STANDARD_OUTPUT_LOGGER = "StandardOutputLog";

    /**
     * The identifying name of a simple log sending its messages to the standard
     * error
     */
    public static final String STANDARD_ERROR_LOGGER = "StandardErrorLog";

    /**
     * Just a nostalgic /dev/nul log
     */
    public static final String NULL_LOGGER = "NullLog";

    private static Map<String, TSimpleLogger> loggers = new HashMap<>();
    private DateTimeFormatter dt_formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss.SSS");
    private String loggerName;
    private TLogLevel logLevel;
    private String logFileName;
    private PrintStream logStream;

    /**
     * Return the finest level of log messages that are currently logged by this
     * logger.
     *
     * @return the finest level of log messages that are currently logged by
     * this logger.
     */
    @Override
    public TLogLevel getLevel() {
        return this.logLevel;
    }

    /**
     * Change the level for this logger objects: only messages with a TLogLevel
     * higher or equal to the TLogLevel of the logger will be written/traced.
     *
     * @param level the new level to set for this logger object, expressed using
     * the TLogLevel enum type.
     */
    @Override
    public void setLevel(TLogLevel level) {
        TLogLevel old = this.logLevel;
        this.logLevel = level;
        if (old != level) {
            this.verbose(String.format("Logging level has benn changed from %s to %s", old, level));
        }
    }

    /**
     * Send the message string to the logger with a default log level set to
     * TLogLevel.FATAL.
     *
     * @param message The text message you want to log.
     */
    @Override
    public void fatal(String message) {
        this.log(TLogLevel.FAT, message);
    }

    /**
     * Send the message string to the logger with a default log level set to
     * TLogLevel.ERROR.
     *
     * @param message The text message you want to log.
     */
    @Override
    public void error(String message) {
        this.log(TLogLevel.ERR, message);
    }

    /**
     * Send the message string to the logger with a default log level set to
     * TLogLevel.WARNING.
     *
     * @param message The text message you want to log.
     */
    @Override
    public void warning(String message) {
        this.log(TLogLevel.WAR, message);
    }

    /**
     * Send the message string to the logger with a default log level set to
     * TLogLevel.INFO.
     *
     * @param message The text message you want to log.
     */
    @Override
    public void info(String message) {
        this.log(TLogLevel.INF, message);
    }

    /**
     * Send the message string to the logger with a default log level set to
     * TLogLevel.VERBOSE.
     *
     * @param message The text message you want to log.
     */
    @Override
    public void verbose(String message) {
        this.log(TLogLevel.VER, message);
    }

    /**
     * Send the message string to the logger using the specified level of
     * attention.&nbsp;The message will be traced only if the log level of the
     * logger is higher or equal to the log level of the message.
     *
     * @param msgLevel The level of attention you want to use for this specific
     * message.
     * @param message The text message you want to log.
     */
    @Override
    public void log(TLogLevel msgLevel, String message) {
        if (logStream == null) {
            return;
        }
        if (msgLevel.ordinal() <= this.logLevel.ordinal()) {
            StringBuilder sb = new StringBuilder("[");
            //TODO(2.0) capise se si può fare di meglio per customizzare il messaggio di Log...
            sb.append(ZonedDateTime.now().format(dt_formatter));
            sb.append("] [").append(msgLevel).append("] [").append(message).append("]");
            logStream.println(sb.toString());
        }
    }

    /**
     * TODO(1.1) Write comment here.
     * @param level
     * @param gameId
     * @param count
     * @param extracted
     * @param msg 
     */
    @Override
    public void gameLog(TLogLevel level, String gameId, int count, int extracted, String msg) {
        if (logStream == null) {
            return;
        }
        if (level.ordinal() <= this.logLevel.ordinal()) {
        // [Timestamp] [level] [gameid] [counter] [extracted] [message]
            String logEntry = String.format("[%s] [%s] [%s] [%2d] [%2d] [%s]",
                    ZonedDateTime.now().format(dt_formatter), level, gameId, count,
                    extracted, msg);
            logStream.println(logEntry);
        }
    }

    /**
     * Allows for client class to retrieve a TLogger instance using a
     * descriptive name as identifier, so that many classes can easily share the
     * same log instance within an application.
     *
     * @param name The name that uniquely identify the logger object
     * @return the TLogger object to use to log events and messages if the
     * logger has been previously prepared and exists, null otherwise.
     */
    public static TSimpleLogger getLoggerByName(String name) {
        if (name == null || name.length() < 1) {
            return null;
        }
        TSimpleLogger result = loggers.get(name);
        if (result == null) {
            switch (name) {
                case DEFAULT_FACTORY_LOGGER:
                    prepareLogger(name, TLogLevel.VER, System.out);
                    break;
                case DEFAULT_GAME_LOGGER:
                    prepareLogger(name, TLogLevel.VER, System.out);
                    break;
                case STANDARD_OUTPUT_LOGGER:
                    prepareLogger(name, TLogLevel.VER, System.out);
                    break;
                case STANDARD_ERROR_LOGGER:
                    prepareLogger(name, TLogLevel.VER, System.out);
                    break;
                case NULL_LOGGER:
                    prepareLogger(name, TLogLevel.FAT, (PrintStream) null);    //???
                    break;
            }
        }
        result = loggers.get(name);
        return result;
    }

    /**
     * Use this method to prepare a new logger object that can be used by all
     * other TombolaLib major protagonists, such as {@linkplain ISetFactory} or
     * {@linkplain TGame} objects.
     *
     * @param logName the name to uniquely identify the logger object
     * @param level the initial level of message upon which log messages are
     * effectively traced
     * @param filename the text filename where log messages will be spooled to
     * @throws FileNotFoundException is for some reason it will not be possible
     * to open the filename passed as argument.
     */
    public static void prepareLogger(String logName, TLogLevel level, String filename) throws FileNotFoundException {
        loggers.put(logName, new TSimpleLogger(logName, level, filename));
    }

    /**
     * Use this method to prepare a new logger object that can be used by all
     * other TombolaLib major protagonists, such as {@linkplain ISetFactory} or
     * {@linkplain TGame} objects.
     *
     * @param logName the name to uniquely identify the logger object
     * @param level the initial level of message upon which log messages are
     * effectively traced
     * @param stream the PrintStream object where log messages will be spooled
     * to (it can be System.out or System.err as well).
     */
    public static void prepareLogger(String logName, TLogLevel level, PrintStream stream) {
        loggers.put(logName, new TSimpleLogger(logName, level, stream));
    }

    /**
     * Allows the change of the format used to print the timestamp at the
     * beginning of each log message.
     *
     * @param format the String specifying the timestamp format to use,
     * following the well known convention of Java DateTimeFormatter objects.
     */
    public void setTimeStampFormat(String format) {
        dt_formatter = DateTimeFormatter.ofPattern(format);
    }

    // --- Private zone ------------------------------------------------------------
    private TSimpleLogger(String name, TLogLevel level, String fileName) throws FileNotFoundException {
        if (name == null || name.length() < 1) {
            throw new TTombolaRuntimeException("<FATAL!> Tlogger name must be not null and not empty.");
        }
        this.loggerName = name;
        this.logLevel = ((level == null) ? TLogLevel.VER : level);
        if (fileName != null && fileName.length() > 0) {
            this.logStream = new PrintStream(new FileOutputStream(fileName, true));
        }
    }

    private TSimpleLogger(String name, TLogLevel level, PrintStream stream) {
        this.loggerName = name;
        this.logLevel = ((level == null) ? TLogLevel.VER : level);
        this.logStream = stream;
        // TODO(2.01) Rivedere bene... capire meglio che si vuole fare...
    }

}           // End Of File - Rel.(1.1)
