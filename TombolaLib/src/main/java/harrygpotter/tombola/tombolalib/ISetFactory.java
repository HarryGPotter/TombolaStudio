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
 * This is the common interface implemented by all Set of Series generation
 * algorithm classes included in the TombolaLib library. All generation
 * algorithm classes have methods to set the desired number of series, the
 * maximum tolerable amount of equal number between cards and between rows of
 * cards. ISetFactory implementing objects have also been designed in order to
 * run the series generation process in a dedicated thread and therefore
 * encapsulate method to start, check status and stop these 'executor' threads.
 *
 * @author Harry G. Potter (harry[.]g[.]potter[@]gmail[.]com)
 * @version 1.1
 * @see TAbstractSetFactory
 * @since 1.8
 */
public interface ISetFactory extends Runnable {

    /**
     * Set the minimum value allowed for the "max equal number between cards"
     * parameter used when checking the quality of a set of series.
     */
    final static int MINIMUM_MAXEPC = 5;

    /**
     * Set the minimum value allowed for the "max equal number between rows"
     * parameter used when checking the quality of a set of series.
     */
    final static int MINIMUM_MAXEPR = 2;

    /**
     * Considering the heuristic nature of many the generation algorithms, a
     * guard limit is used to prevent the (very very unlikely) possibility that
     * the generation method runs indefinitely.&nbsp;This value is the limit to
     * the number of total iterations a generation process can do.
     */
    final static long MAX_ITERATIONS = 500000000L; // 500 Millions series generation

    /**
     * Considering the heuristic nature of many the generation algorithms, a
     * guard limit is used to prevent the (very very unlikely) possibility that
     * the generation method runs indefinitely.&nbsp;This value is the limit to
     * the elapsed time a generation process can run.
     */
    final static long MAX_ITERATIONS_MILLISECS = 1000 * 60 * 60 * 48; // 48h

    /**
     * Enum definition used to maintain the status of the generation algorithm
     */
    static enum TStatus {
        INITIALIZING, READY, RUNNING, STOPPING, STOPPED, COMPLETED
    }

    /**
     * Each class implementing this interface must return a human readable name
     * for the algorithm it uses in order to generate the set of series.
     *
     * @return a descriptive name for the specific series generation algorithm
     * used by the class.
     */
    String getMethodName();

    /**
     * Return the current status of the factory object. Here a brief description
     * of the <i>intended meaning</i> of each state follows:
     * <ul>
     * <li>status == INITIALIZING: there are still mandatory parameters and or
     * component objects to be set;</li>
     * <li>status == READY: everything has been done in order to start the
     * series generation;</li>
     * <li>status == RUNNING: algorithm is running, series are going to be
     * generated;</li>
     * <li>status == STOPPING: the interruption of the process has been
     * requested but the executor thread is still finalizing its things in order
     * to definitively stop;</li>
     * <li>status == STOPPED: generation process has been forcedly interrupted.
     * Desired number of series may have been not completely generated yet.
     * Typically process can be also re-started;</li>
     * <li>status == COMPLETED: the process has naturally completed its
     * execution. Desired series have been generated respecting MaxEPC/MaxEPR
     * configured values.</li>
     * </ul>
     *
     * @return the current status of the factory object
     */
    TStatus getStatus();

    /**
     * While in the "INITIALIZING" status, user should set the number of desired
     * series of card the algorithms has to generate. the value must be greater
     * than 1 and lesser then {@linkplain TUtils#MAX_SERIES}.
     *
     * @param desiredSeries the number of series you want to generate.
     */
    void setDesiredSeries(int desiredSeries);

    /**
     * Return the number of series, each composed by six cards, that have been
     * configured to by generated by the factory.
     *
     * @return the number of series to be produced by the factory.
     */
    int getDesideredSeries();

    /**
     * While in the "INITIALIZING" status, user should set the TMakeSix instance
     * the series ser factory will use to generate each single series.
     *
     * @param seriesBuilder the TMakeSix object the factory object will use.
     */
    void setSeriesBuilder(TMakeSix seriesBuilder);

    /**
     * Return the TMakeSix object used by the set factory to generate each
     * series of cards.
     *
     * @return the TMakeSix object used by the set factory to generate each
     * series of cards.
     */
    TMakeSix getSeriesBuilder();

    /**
     * While in the "INITIALITING" status, could set an initial list of
     * {@linkplain TSeries} instances the generation algorithm will start from.
     * Depending on the specific implementation of the generation algorithm,
     * empty or non empty set are allowed.
     *
     * @param runningSet the set of series of cards you want start from to
     * continue with series generation
     */
    void setSeriesList(TSeriesList runningSet);

    /**
     * Return the List object containing the generated series of cards.
     *
     * @return the List object containing the generated series of cards.
     */
    TSeriesList getSeriesList();

    /**
     * While in the "INITIALITING" status, set the maximum value allowed for
     * equal number between cards. Typically if this value is exceeded by a
     * couple of cards during the generation process, the series containing one
     * of them is discarded and regenerated.
     *
     * @param maxEPC the maximum equal number per card allowed during card
     * comparison
     */
    void setMaxEqualPerCard(int maxEPC);

    /**
     * Return the value of the parameter set to specify the max equal number
     * allowed per card.
     *
     * @return the max equal number allowed per card.
     */
    int getMaxEqualPerCard();

    /**
     * While in the "INITIALITING" status, set the maximum value allowed for
     * equal number between row of different cards. Typically if this value is
     * exceeded by a couple of rows during the generation process, the series
     * containing the card with one of the exceeding row is discarded and
     * regenerated.
     *
     * @param maxEPR the maximum equal number per row allowed during card
     * comparison
     */
    void setMaxEqualPerRow(int maxEPR);

    /**
     * Return the max equal number allowed per row.
     *
     * @return the max equal number allowed per row.
     */
    int getMaxEqualPerRow();

    /**
     * Set the timeout limit in milliseconds for the generation process. It is
     * used to set an execution limit to the generation process in case the
     * desired parameters of maxEPC and/or maxEPR are too challenging. The
     * parameter should be set when the object is in the INITIALIZING phase, but
     * it can be changed even during the following phases.
     *
     * @param limitMilliSecs the desired timeout, expressed in milliseconds
     */
    void setTimeLimit(long limitMilliSecs);

    /**
     * Return the timeout, in milliseconds, set in order to complete the card
     * generation.
     *
     * @return the timeout, in milliseconds, set in order to complete the card
     * generation.
     */
    long getTimeLimit();

    /**
     * Set a limit to the number of series generation that can be <i>tried</i>
     * during the set creation. It is used to set an execution limit to the
     * generation process in case the desired parameters of maxEPC and/or maxEPR
     * are too challenging (impossible to meet). The parameter should be set
     * when the object is in the INITIALIZING phase, but it can be changed even
     * during the following phases. It limit the number of series of 6 cards.
     *
     * @param iterationsLimit the maximum number of series of cards that can be
     * generated/discarding during the set generation process.
     */
    void setIterationsLimit(long iterationsLimit);

    /**
     * Return the max number of single iterations allowed to complete the card
     * generation.
     *
     * @return the max number of single iterations allowed to complete the card
     * generation.
     */
    long getIterationsLimit();

    /**
     * Return the elapsed time in milliseconds from the start of the generation
     * process to now.
     *
     * @return the elapsed time in milliseconds from the start of the generation
     * process to now.
     */
    long getElapsedMillisecs();
    // TODO(2.0) void resetElapsedMillisecs();
    // TODO(2.0) long getEstimationToCompleteElapsedMillisecs();

    /**
     * Set the logger object that the factory will use to trace its activities
     * and "communicate" to the externals.&nbsp;It MUST be set during the
     * INITALIZATION phase and cannot be changed during the following phases of
     * the generation process.
     *
     * @param logger the logger that the factory will use to trace its
     * activities
     * @see ILogger
     */
    void setLogger(ILogger logger);

    /**
     * Return the current ILogger object used by the factory to communicate
     * progresses and errors during the card generation process.
     *
     * @return the current ILogger object used by the factory
     */
    ILogger getLogger();

    /**
     * Invoke this method to start the generation process after you set all
     * parameters/components needed (setDesiredSeries, setSeriesBuilder,
     * setLogger, etc.).&nbsp;When invoked, the factory status changes from
     * READY to RUNNING.
     */
    void requestStart();

    /**
     * Invoke this method to stop the generation process (i.e.&nbsp;force an
     * interruption).&nbsp;When invoked, the status change from RUNNING to
     * STOPPED
     */
    void requestStop();

    /**
     * Invoke this method to block your execution and wait until the series
     * generation process ends. It should work in the same way as the Java
     * standard library method Thread#join() works.
     *
     * @throws InterruptedException as thrown by {@linkplain Thread#join()}
     * method
     */
    void joinOnEnded() throws InterruptedException;

    /**
     * Invoke this method to block your execution and wait until either the
     * series generation process ends or the timeout in milliseconds passed as
     * input parameter is reached. It should work in the same way as the Java
     * standard library method Thread#join(long) works.
     *
     * @param mSecsTimeout time in milliseconds to wait before retake control
     * @throws InterruptedException as thrown by {@linkplain Thread#join(long)}
     * method
     */
    void joinOnEnded(long mSecsTimeout) throws InterruptedException;
}           // End Of File - Rel.(1.1)
