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
 * TSimpleSetFactory object implements the simplest heuristic to generate series
 * of cards.&nbsp;It simply generates a series of six cards at a time, without
 * any "quality" check regarding the distribution of the numbers between the
 * cards of different series.&nbsp;MaxEPC and MAxEPR values are not used, that
 * is they can be considered as always set, respectively, to 15 and 5.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TMakeSix
 * @see TAbstractSetFactory
 * @see ISetFactory
 * @since 1.8
 */
public class TSimpleSetFactory extends TAbstractSetFactory {

    private static final String MY_NAME = "Pure Random Factory 1.0";

    /**
     * Return a symbolic name identifying the used heuristic algorithm.
     *
     * @return a symbolic name identifying the used heuristic algorithm.
     */
    @Override
    public String getMethodName() {
        return MY_NAME;
    }

    /**
     * This "quality parameter" is not used by this heuristic, so this method
     * return always 15.
     *
     * @return always the 15 value.
     */
    @Override
    public int getMaxEqualPerCard() {
        return 15;
    }

    /**
     * This "quality parameter" is not used by this heuristic, so this method
     * return always 5.
     *
     * @return always the 5 value.
     */
    @Override
    public int getMaxEqualPerRow() {
        return 5;
    }

    /**
     * This method implements the simple loop needed to create the desired
     * number of card series.&nbsp;There is no <i>quality check</i> on generated
     * cards, that is series are generated randomly using the configured
     * TMakeSix series builder object, and no comparison is made between cards
     * belonging to different series.&nbsp;See
     * {@linkplain TProgressiveSetFactory} for something more interesting!
     *
     */
    @Override
    public void run() {
        status = TStatus.RUNNING;
        // Generation begins from what is already present in the Series List
        int start = seriesList.size();

        ts_startExecution = System.currentTimeMillis();
        iterationCounter = 0;

        for (int i = start; i < desiredSeries; i++) {
            this.seriesList.add(new TSeries(seriesBuilder.prepareSix()));
            this.iterationCounter++;
            this.tp_Elapsed = System.currentTimeMillis() - ts_startExecution;
            if (checkForStop() < 0) {
                return;
            }
        }

        // Maybe it is better have sort helper methods directly on TSeriesList class.
        // seriesSet.sort((s2,s1)-> new Integer(s1.getCurrentMEPC()).compareTo(new Integer(s2.getCurrentMEPC())));
        seriesList.compareByCard();
        seriesList.compareByRow();
        seriesList.sortBestToWorstByCard();
        boolean commentFound = false;
        if (seriesList.getComments()!=null) {
            for(String s: seriesList.getComments()) {
                commentFound |= s.equalsIgnoreCase("Series are sorted from 'best' to 'worst' considering maximum equal number between cards.");
            }
        }
        if (!commentFound) {
            seriesList.addComment("Series are sorted from 'best' to 'worst' considering maximum equal number between cards.");
        }
        seriesList.prepareLabels();
        // seriesSet.seriesListStatistics();
        seriesList.setMakeSixCounter(seriesBuilder.getCardCounter());
        seriesList.setMakeSixSeed(seriesBuilder.getRandomSeed());
        seriesList.setMakeSixMethod(TMakeSix.MAKESIX_METHOD_NAME);
        seriesList.setSetFactoryMethod(this.getMethodName());
        status = TStatus.COMPLETED;
        ts_EndExecution = System.currentTimeMillis();
        tp_Elapsed = ts_EndExecution - ts_startExecution;
        logger.info("Well done! Process Naturally ended.");
        //TODO(2.0) Improve logging message.
    }
}           // End Of File - Rel.(1.1)
