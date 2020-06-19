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
 * This class implements an heuristic algorithm that generate list of series
 * aiming to reduce (i.e.&nbsp;equalize) equal numbers between each couple of
 * cards and equal numbers between couple of single rows of the
 * cards.&nbsp;Technically, generated series are added to the list only if they
 * do not have more equal numbers by card or by row of chosen thresholds if
 * compared to all the cards already inserted in the list.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @since 1.8
 * @see ISetFactory
 * @see TSeriesList
 * @see TMakeSix
 */
public class TProgressiveSetFactory extends TAbstractSetFactory {

    private static final String MY_NAME = "Control & Add Factory 1.0";

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
     * This method implements the generate-control-regenerate loop needed to
     * create the desired number of card series without exceeding limits set for
     * max equal numbers between cards and single rows.
     */
    @Override
    public void run() {
        // Chech if the status change can be done in the more general "requirestart" method
        status = TStatus.RUNNING;
        int start = seriesList.size();
        ts_startExecution = System.currentTimeMillis();
        iterationCounter = 0;
        // TODO(2.0) please review this method... completed in a hurry
        seriesList.setMakeSixSeed(this.seriesBuilder.getRandomSeed());
        seriesList.setMakeSixMethod(TMakeSix.MAKESIX_METHOD_NAME);
        seriesList.setSetFactoryMethod(this.getMethodName());
        while (seriesList.size() < desiredSeries) {
            TSeries sx = new TSeries(this.seriesBuilder.prepareSix());
            this.iterationCounter++;
            this.tp_Elapsed = System.currentTimeMillis() - ts_startExecution;

            int i = 0;
            int temp1 = 0;
            int temp2 = 0;
            while (i < seriesList.size()) {
                if (checkForStop() < 0) {
                    return;
                }
                temp1 = sx.compareByCard(seriesList.get(i));
                temp2 = sx.compareByRow(seriesList.get(i));
                if (temp1 > this.maxepc || temp2 > this.maxepr) {
                    sx = new TSeries(this.seriesBuilder.prepareSix());
                    sx.resetCompareResult(); // Maybe this call can be deleted
                    seriesList.resetAllCompareResult();
                    i = 0;
                } else {
                    i++;
                }
            }
            seriesList.add(sx);
            seriesList.setMakeSixCounter(this.seriesBuilder.getCardCounter());
            int setSize = seriesList.size();
            logger.verbose("[OK. " + setSize + "/" + setSize * 6 + "] ");
        }
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
        // TODO(2.0) add extra info to the comment list...
        // bw.write(String.format(commentPrefix + " %,d cards have been generated in the process.%n", cardSet.getMakeSixCounter()));
        seriesList.prepareLabels();
        //seriesSet.seriesListStatistics();
        this.status = TStatus.COMPLETED;
        ts_EndExecution = System.currentTimeMillis();
        tp_Elapsed = ts_EndExecution - ts_startExecution;
        logger.info("Well done! Process Naturally ended");
    }
}           // End Of File - Rel.(1.1)
