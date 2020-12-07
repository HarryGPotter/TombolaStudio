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

/**
 * Simple (maybe too simple in this release) interface used by other TombolaLib
 * objects to trace their activities both during the card series generation
 * phase (ISetFactory objects) and the simulated/assisted tombola games (TGame
 * objects).&nbsp;An interface is used to let TombolaLib users to provide their
 * custom implementation of ILogger objects.&nbsp;Currently the library provide
 * a first practical implementation (see {@linkplain TSimpleLogger}) to log on
 * plain text files or on standard console output.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @since 1.8
 * @see ISetFactory
 * @see TGame
 * @see TSimpleLogger
 */
public interface ILogger {

    /**
     * As usual, define the discrete "levels" of importance for messages passed
     * to Ilogger instances, in order to prioritize them and decide if they must
     * be logged or not based on the log level set for each logger.
     */
    public static enum TLogLevel {
        FAT, ERR, WIN, CAN, WAR, INF, VER
    }

    /**
     * Change the level for this ILogger objects: only messages with a TLogLevel
     * higher or equal to the TLogLevel of the ILogger will be written/traced.
     *
     * @param level maximum level of messages that will be traced by this logger
     * object.
     */
    void setLevel(TLogLevel level);

    /**
     * Return the "level" up to the log messages passed to this logger will be
     * traced (that is, saved on a file, printed, stored on a database).
     *
     * @return the current LogLevel set for this ILogger object
     */
    TLogLevel getLevel();

    /**
     * Send the msg string to the logger with a default log level set to
     * TLogLevel.INFO.
     *
     * @param msg The text message you want to log.
     */
    void info(String msg);

    /**
     * Send the msg string to the logger with a default log level set to
     * TLogLevel.WARNING.
     *
     * @param msg The text message you want to log.
     */
    void warning(String msg);

    /**
     * Send the msg string to the logger with a default log level set to
     * TLogLevel.ERROR.
     *
     * @param msg The text message you want to log.
     */
    void error(String msg);

    /**
     * Send the msg string to the logger with a default log level set to
     * TLogLevel.FATAL.
     *
     * @param msg The text message you want to log.
     */
    void fatal(String msg);

    /**
     * Send the msg string to the logger with a default log level set to
     * TLogLevel.VERBOSE.
     *
     * @param msg The text message you want to log.
     */
    void verbose(String msg);

    /**
     * Send the msg string to the logger using the specified level of
     * attention.&nbsp;The message will be traced only if the log level of the
     * logger is higher or equal to the log level of the message.
     *
     * @param level The level of attention you want to use for this specific
     * message.
     * @param msg The text message you want to log.
     */
    void log(TLogLevel level, String msg);
    
    /**
     * TODO(1.1) Write comments here
     * @param level
     * @param gameId
     * @param count
     * @param extracted
     * @param msg 
     */
    void gameLog(TLogLevel level, String gameId, int count, int extracted, String msg);

}           // End Of File - Rel.(1.1)
