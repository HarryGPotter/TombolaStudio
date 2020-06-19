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
 * Abstract class providing standard, basic implementation of <i>common</i>
 * methods for the {@linkplain
 * ISetFactory} interface. Who wants to provide a new heuristic to generate set
 * of series, instead of directly implementing the ISetFactory interface, can
 * sub-class TAbstracSetFactory inheriting all its "boiler-plate"
 * implementations thus concentrating only on the core, differentiating,
 * <code>run()</code> method implementation.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see ISetFactory
 * @see TSimpleSetFactory
 * @see TProgressiveSetFactory
 * @since 1.8
 */
public abstract class TAbstractSetFactory implements ISetFactory {

    protected static int DEFAULT_NUMSERIES = 10;
    //protected static String DEFAULT_HEURISTIC_LOGGER_NAME = "DEFAULT.SERIES.GENERATION.LOGGER";

    protected TStatus status = TStatus.INITIALIZING;
    protected TMakeSix seriesBuilder = null;
    protected TSeriesList seriesList = null;
    protected ILogger logger = null;
    protected int desiredSeries = DEFAULT_NUMSERIES;
    protected int maxepc = MINIMUM_MAXEPC + 3;
    protected int maxepr = MINIMUM_MAXEPR + 2;
    protected Thread execThread;

    protected long limitMilliSecs = MAX_ITERATIONS_MILLISECS;
    protected long limitCount = MAX_ITERATIONS;
    protected long ts_startExecution, ts_EndExecution, tp_Elapsed;
    protected long iterationCounter;

    /**
     * Default constructor for the class.&nbsp;It leaves the object in the
     * "INITIALIZING" status, because, even if default values are provided for
     * the desired number of series, the maximum equal numbers allowed for cards
     * and rows and the iterations limits (counter and milliseconds) for the
     * generation heuristics, <b>the user still have to set the seriesBuilder
     * (TMakeSix instance), the logger (TLogger instance) and the series set
     * (TSeriesList instance) that will contain the generated series of
     * cards.</b>
     */
    public TAbstractSetFactory() {
        // seriesBuilder = new TMakeSix();
        // seriesSet = new TSeriesList();
        // logger = TLogger.getLoggerByName(DEFAULT_HEURISTIC_LOGGER_NAME);
        this.checkInitialization();
    }

    /**
     * Set the {@linkplain TSimpleLogger} object used by the generation
     * heuristic to trace relevant events and errors.&nbsp;It can be set only
     * during the INITIALIZING or READY phase, not in following statuses.
     *
     * @param logger the TLogger object Set Factory will use to trace events and
     * errors.
     */
    @Override
    public void setLogger(ILogger logger) {
        if (status == TStatus.INITIALIZING || status == TStatus.READY) {
            this.logger = logger;
        } else {
            throw new TTombolaRuntimeException("TLogger object cannot be set in this status!");
        }
        checkInitialization();
    }

    /**
     * Return the logger used by this set factory object.
     *
     * @return Return the logger used by this set factory object.
     */
    @Override
    public ILogger getLogger() {
        return this.logger;
    }

    /**
     * Set the number of series that the heuristic will try to
     * generate.&nbsp;This value must be set in the INITIALIZING status to
     * enable the READY status, but usually can also be changed in the following
     * RUNNING or STOPPED statuses.
     *
     * @param desiredNumSeries the number of series that the heuristic will try
     * to generate.
     */
    @Override
    public void setDesiredSeries(int desiredNumSeries) {
        if (desiredNumSeries < 1 || desiredNumSeries > TUtils.MAX_SERIES) {
            throw new TTombolaRuntimeException(String.format("<ERROR!> Series number to generate must be in the range [%d, %d].", 1, TUtils.MAX_SERIES));
        }
        this.desiredSeries = desiredNumSeries;
        checkInitialization();
    }

    /**
     * Return the number of series to be generated as currently set.
     *
     * @return Return the number of series to be generated as currently set.
     */
    @Override
    public int getDesideredSeries() {
        return this.desiredSeries;
    }

    /**
     * Set the TMakeSix object that will be used to generate each serires of six
     * cards.&nbsp;This object must be set only in the INITIALIZING status to
     * enable the READY status.
     *
     * @param seriesBuilder the TMakeSix object that will be used to generate
     * each series of 6 card.
     */
    @Override
    public void setSeriesBuilder(TMakeSix seriesBuilder) {
        if (status == TStatus.INITIALIZING || status == TStatus.READY) {
            this.seriesBuilder = seriesBuilder;
        } else {
            throw new TTombolaRuntimeException("TMakeSix object cannot be set when in the " + status + " status!");
        }
        checkInitialization();
    }

    /**
     * Return the TMakeSix object used by the heuristic to generate each series
     * of six card.
     *
     * @return the TMakeSix object used by the heuristic to generate each series
     * of six card.
     */
    @Override
    public TMakeSix getSeriesBuilder() {
        return this.seriesBuilder;
    }

    /**
     * Set the TSeriesList object that will contain the series generated by the
     * heuristic. In can be set only when the factory object is in the
     * INITIALIZING (or READY) status and cannot be changed subsequently.
     *
     * @param runningSet the series list object you want to use to collect the
     * generated cards.
     */
    @Override
    public void setSeriesList(TSeriesList runningSet) {
        if (runningSet != null && runningSet.size() > TUtils.MAX_SERIES) {
            throw new TTombolaRuntimeException(String.format("<ERROR!> SeriesSet already contains more than %d series, the maximum allowed.", TUtils.MAX_SERIES));
        }
        if (status != TStatus.RUNNING && status != TStatus.STOPPING) {
            this.seriesList = runningSet;
        } else {
            throw new TTombolaRuntimeException("TSeriesList object cannot be set when in the " + status + " status!");
        }
        checkInitialization();
    }

    /**
     * Return the TSeriesList object containing the series generated by the
     * heuristic. You can access to this list whenever you want, independently
     * to the status the factory set is operating in. It is user responsibility
     * do not modify this set during series generation (RUNNING status).
     *
     * @return the TSeriesList object containing the series generated by the
     * heuristic.
     */
    @Override
    public TSeriesList getSeriesList() {
        return this.seriesList;
    }

    /**
     * Set the max equal numbers allowed per card. It must be set when the set
     * factory is in the INITIALIZING on READY status and can be only increased
     * during the RUN phase.
     *
     * @param maxEPC the max equal numbers allowed per card.
     */
    @Override
    public void setMaxEqualPerCard(int maxEPC) {
        if (maxEPC < MINIMUM_MAXEPC) {
            throw new TTombolaRuntimeException(String.format("<ERROR!> Max allowed equal number per card cannot be less than %d.", MINIMUM_MAXEPC));
        }
        if (this.seriesList.size() == 0 || status == TStatus.INITIALIZING || status == TStatus.READY) {
            // I can set the value I want, it has just to be valid.
            this.maxepc = maxEPC;
        } else {
            // I can only increase the maxepc value
            if (maxEPC >= this.maxepc) {
                this.maxepc = maxEPC;
            } else {
                throw new TTombolaRuntimeException("You can only increase the MaxEPC value during the series generation.");
            }
        }
    }

    /**
     * Return the max equal number allowed per card.
     *
     * @return the max equal number allowed per card.
     */
    @Override
    public int getMaxEqualPerCard() {
        return this.maxepc;
    }

    /**
     * Set the max equal numbers allowed per row. It must be set when the set
     * factory is in the INITIALIZING on READY status and can be only increased
     * during the RUN phase.
     *
     * @param maxEPR the max equal numbers allowed per row.
     */
    @Override
    public void setMaxEqualPerRow(int maxEPR) {
        if (maxEPR < MINIMUM_MAXEPR) {
            throw new TTombolaRuntimeException(String.format("<ERROR!> Max allowed equal number per row cannot be less than %d.", MINIMUM_MAXEPR));
        }
        if (this.seriesList.size() == 0 || status == TStatus.INITIALIZING || status == TStatus.READY) {
            // I can set the value I want, it has just to be valid.
            this.maxepr = maxEPR;
        } else {
            // I can only increase the maxepc value
            if (maxEPR >= this.maxepr) {
                this.maxepr = maxEPR;
            } else {
                throw new TTombolaRuntimeException("You can only increase the MaxEPR value during the series generation.");
            }
        }
    }

    /**
     * Return the max equal number allowed per row.
     *
     * @return the max equal number allowed per row.
     */
    @Override
    public int getMaxEqualPerRow() {
        return this.maxepr;
    }

    /**
     * Set an interval time that limit the execution of the series generation
     * process. This is mainly done to prevent endless runs in case of too
     * challenging values set for MaxEPC and/or MaxEPR quality indicators. If
     * this limit is reached, the heuristic will initiate the stop processing.
     *
     * @param limitMilliSecs the maximum execution time allowed for a run of
     * series generation.
     */
    @Override
    public void setTimeLimit(long limitMilliSecs) {
        if (limitMilliSecs < 1000) {
            // TODO(1.2) Do not use costants within the code!
            throw new TTombolaRuntimeException("<ERROR!> A negative value or a value less than 100 milliseconds does not make sense.");
        }
        this.limitMilliSecs = limitMilliSecs;
    }

    /**
     * @return In milliseconds, the timeout set for series generation processes
     */
    @Override
    public long getTimeLimit() {
        return this.limitMilliSecs;
    }

    /**
     * Set a maximum number of loop cycles that limit the execution of the
     * series generation process. This is mainly done to prevent endless runs in
     * case of too challenging values set for MaxEPC and/or MaxEPR quality
     * indicators. If this limit is reached, the heuristic will initiate the
     * stop processing.
     *
     * @param limitCount the maximum number of loop allowed to complete the
     * series list generation process.
     */
    @Override
    public void setIterationsLimit(long limitCount) {
        if (limitCount < 1000) {
            // TODO(1.2) Do not use costants within the code!
            throw new TTombolaRuntimeException("<ERROR!> A negative value or a value less than 1000 iteractions does not make sense.");
        }
        this.limitCount = limitCount;
    }

    /**
     * @return the maximum number of loop cycles allowed for the series
     * generation process
     */
    @Override
    public long getIterationsLimit() {
        return this.limitCount;
    }

    /**
     * If the set factory object is in the RUNNING status, this method returns,
     * in milliseconds, the time elapsed since the series generation process was
     * started.
     *
     * @return the time elapsed since the series generation process was started,
     * or the complete duration of the process if it is already ended or has
     * been stopped. Return zero if the series generation process has not
     * started jet.
     */
    @Override
    public long getElapsedMillisecs() {
        if (status == TStatus.RUNNING) {
            return (System.currentTimeMillis() - ts_startExecution);
        } else if (status == TStatus.COMPLETED || status == TStatus.STOPPED) {
            return (ts_EndExecution - ts_startExecution);
        } else {
            return 0;
        }
    }

    /**
     * Invoke this method to start the set series generation process. This
     * method should be called only if the set factory is in the READY status,
     * but it can be used also to re-start a process already in the STOPPED or
     * COMPLETED status. It will instantiate a dedicated Java Thread (daemon
     * type, so it will not block the whole Java virtual machine instance) to
     * execute the process. The status of the set factory object is NOT changed.
     * The {@linkplain ISetFactory#run()} method has the responsibilities to
     * change it from READY (or STOPPED, or COMPLETED) to RUNNING as one of its
     * first instructions.
     */
    @Override
    public void requestStart() {
        if (status == TStatus.INITIALIZING) {
            throw new TTombolaRuntimeException("<WARNING!> Mandatory set factory parameters have not been set. ");
            // TODO(2.0) Specify better what is missing...
        }
        // TODO(2.0) Move this control to the corresponing set method?
        if (this.desiredSeries <= this.seriesList.size()) {
            throw new TTombolaRuntimeException(String.format("<WARNING!> There are already %d seires in the set. Delete current series or raise the desired amount to generate more.", this.seriesList.size()));
        }
        if (status == TStatus.RUNNING) {
            throw new TTombolaRuntimeException("<WARNING!> Strange situation here: a SetFactory thread is already in execution. Cannot start another thread.");
        }
        if (status == TStatus.READY || status == TStatus.COMPLETED || status == TStatus.STOPPED) {
            this.execThread = new Thread(this);
            this.execThread.setDaemon(true);   // Are we sure?

            // The following instruction is commented because it shoulbe the first instruction in the run() method of the implementing sub-class.
            this.status = TStatus.RUNNING;

            this.execThread.start();
            //TODO(2.0) This log message is really ugly...
            logger.info(this.getMethodName() + " executor started to generate " + this.getDesideredSeries() + " series, " + this.getDesideredSeries() * 6 + " cards.");

        } else {
            throw new TTombolaRuntimeException("<WARNING!> Strange situation here: a SetFactory thread is stopping or is in a bed zombie status.");
        }
    }

    /**
     * Invoke this method to request the interruption of the series generation
     * algorithm before it completes all desired series of cards. Interruption
     * request should be examined within the {@linkplain ISetFactory#run()}
     * method (the factory is in RUNNING state) and the elaboration will be
     * interrupted. The set factory object will be moved to the STOPPED status.
     * A further invocation of the {@linkplain ISetFactory#requestStart()} could
     * typically re-start the process where it was interrupted.
     */
    @Override
    public void requestStop() throws TTombolaRuntimeException {
        if (this.execThread != null && this.execThread.isAlive() && status == TStatus.RUNNING) {
            status = TStatus.STOPPING;
            logger.verbose("Request to stop the executor issued");
        } else {
            throw new TTombolaRuntimeException("<WARNING!> Strange situation here: There is no alive thread to stop. Why you are asking this?");
        }
    }

    /**
     * Invoke this method to block your execution and wait the completion (or
     * the interruption) of the Java thread dedicated to the generation of the
     * series of cards. This method works just like the
     * {@linkplain Thread#join()} method of the Java standard library.
     *
     * @throws InterruptedException like the standard Java library
     * {@linkplain Thread#join()} method.
     * @throws TTombolaRuntimeException to indicate a request to wait for a
     * series generation process that has not been already started, has been
     * already interrupted or is already completed.
     */
    @Override
    public void joinOnEnded() throws InterruptedException, TTombolaRuntimeException {
        if (this.execThread != null && this.execThread.isAlive() && (status != TStatus.STOPPED && status != TStatus.INITIALIZING && status != TStatus.COMPLETED)) {
            this.execThread.join();
        } else {
            throw new TTombolaRuntimeException("<WARNING!> Pay attention! It seems there is nothing to wait for here.");
        }
    }

    /**
     * Invoke this method to block your execution and wait either the completion
     * (or the interruption) of the Java thread dedicated to the generation of
     * the series of cards, or the timeout passed as input parameter. This
     * method works just like the {@linkplain Thread#join(long)} method of the
     * Java standard library.
     *
     * @param mSecsTimeout maximum wait time expressed in milliseconds after
     * that the thread is un-blocked anyway.
     * @throws InterruptedException like the standard Java library
     * {@linkplain Thread#join(long)} method.
     * @throws TTombolaRuntimeException to indicate a request to wait for a
     * series generation process that has not been already started, has been
     * already interrupted or is already completed.
     */
    @Override
    public synchronized void joinOnEnded(long mSecsTimeout) throws InterruptedException, TTombolaRuntimeException {
        if (this.execThread != null && this.execThread.isAlive() && (status != TStatus.STOPPED && status != TStatus.INITIALIZING && status != TStatus.COMPLETED)) {
            this.execThread.join(mSecsTimeout);
        } else {
            throw new TTombolaRuntimeException("<WARNING!> Pay attention! It seems there is nothing to wait for here.");
        }
    }

    /**
     * @return the current status of the set factory object (see:
     * {@linkplain TStatus})
     */
    @Override
    public TStatus getStatus() {
        return this.status;
    }

    /**
     * When created, an object implementing the ISetFactory interface is in the
     * "INITIALIZIG" state, that is mandatory parameters and indispensable
     * components still must be set.&nbsp;This method check is parameters have
     * been properly set and consequently update the object status to "READY".
     * Specifically, following parameters and component objects are considered
     * mandatory: the desired number of series, the TMakeSix instance used to
     * build each series, the TSeriesList object that will contain all generated
     * series, a TLogger instance where log relevant events and error messages,
     * the maximum equal numbers allowed per card and per row, the guard limit
     * counter for the heuristic iterations and the guard limit for the elapsed
     * elaboration time, expressed in millisecond. The default constructor of
     * this abstract class,
     * {@linkplain TAbstractSetFactory#TAbstractSetFactory()}, does not provide
     * defaults value, so the TombolaLib users must call all relevant
     * <code>setXXX</code> method to let the object reach the "READY" state,
     * thus enabling the invocation of the
     * {@linkplain TAbstractSetFactory#requestStart()} method. Classes
     * sub-classing TAbstractSetFactory can provide initial, defaults value
     * overriding their default constructor, thus directly entering the "READY"
     * state.
     *
     * @return the current status of the setFactory object
     * @see TAbstractSetFactory#TAbstractSetFactory()
     */
    protected TStatus checkInitialization() {
        if (status == TStatus.INITIALIZING
                && seriesBuilder != null
                && seriesList != null
                && logger != null
                && desiredSeries > 0
                && maxepc >= MINIMUM_MAXEPC
                && maxepr >= MINIMUM_MAXEPR
                && limitCount > 0
                && limitMilliSecs > 0) {
            status = TStatus.READY;
        }
        return status;
    }

    /**
     * This is a protected, helper designed to be invoked within the run()
     * method, likely in the inner series generation loop, to check if a process
     * stop has been either externally requested by means of a requestStop()
     * invocation or needed because at least one of the process limiting
     * parameters have been reached. It returns: -1 if the process is stopped
     * due to an external call to the requestStop() method, -2 if the iteration
     * limit has been reached, -3 if the timeout for the process has been
     * reached. Return 0 is there is no reason to stop the process.
     *
     * @return -1 if the process is stopped due to an external call to the
     * requestStop() method, -2 if the iteration limit has been reached, -3 if
     * the timeout for the process has been reached. Return 0 is there is no
     * reason to stop the process.
     */
    protected int checkForStop() {
        // Controlli da fare sempre all'inizio delle parti più interne dei cicli
        if (status == TStatus.STOPPING) {
            status = TStatus.STOPPED;
            logger.info("Card Generation process has been stopped, as requested.");
            return -1;
        }
        if (this.limitCount > 0 && this.iterationCounter > this.limitCount) {
            status = TStatus.STOPPED;
            logger.error("Process stopped becouse loop iteration limit has been reached");
            return -2;
        }
        if (this.limitMilliSecs > 0 && this.tp_Elapsed > this.limitMilliSecs) {
            status = TStatus.STOPPED;
            logger.error("Process stopped becouse loop iteration limit has been reached");
            return -3; // UHM... questi return sono da pensare bene...
        }
        return 0;
    }
    //TODO(2.0) Review all literals, check what is error and what is warning, etc...
}           // End Of File - Rel.(1.1)
