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

import java.io.PrintStream;

/**
 * TSeriesListStats is an helper data structure class used to collect basic
 * information about the "quality" of a list of series of cards object.&nbsp;For
 * a given TListSeries object, a TSeriesListStats collects how many cards there
 * are that have X numbers equal to another card within the same series list
 * (for each X in [0, 15]) and many cards there are that have at least one row
 * with Y numbers equals to at least one row on another card within the same
 * series list (for each Y in [0, 5]).&nbsp;Maybe this is not a super-elegant
 * class or solution an in next releases I'll find the time to improve it...
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TSeriesListStats {

    final private int[] maxepc_distribution;
    final private int[] maxepr_distribution;

    /**
     * Constructor accessible only by the TombolaLib classes. It is used by
     * {@linkplain TSeriesList#seriesListStatistics()} to evaluate 'equality'
     * between cards and rows of the list of series.
     *
     * @param maxepc_distribution the vector containing, for each N in [0,15],
     * how many card couples in a list of series having N equal number each
     * other.
     * @param maxepr_distribution the vector containing, for each N in [0,5],
     * how many row couples in a list of series having N equal number each
     * other.
     *
     * @see TSeriesList#seriesListStatistics()
     */
    TSeriesListStats(int[] maxepc_distribution, int[] maxepr_distribution) {
        this.maxepc_distribution = maxepc_distribution;
        this.maxepr_distribution = maxepr_distribution;
    }

    /**
     * Return the "maxepc" distribution vector, that is a vector containing, for
     * each N in [0,15], how many card couples in a list of series have N equal
     * number each other.
     *
     * @return the "maxepc" distribution vector
     */
    public int[] getMaxEPCdistribution() {
        return maxepc_distribution;
    }

    /**
     * Return the "maxepr" distribution vector, thait is avector containing, for
     * each N in [0,5], how many row couples in a list of series have N equal
     * number each other.
     *
     * @return the "maxepr" distribution vector
     */
    public int[] getMaxEPRdistribution() {
        return maxepr_distribution;
    }

    /**
     * Provide a quick print of the data contained in this TSeriesListStats
     * object towards the PrintStream object passed as input field.
     *
     * @param ps the PrintStream object where to flush the data stream
     * @return the same PrintStream object passed as input, as usually in this
     * cases.
     */
    public PrintStream quickPrint(PrintStream ps) {

        for (int i = 0; i < 16; i++) {
            if (maxepc_distribution[i] > 0) {
                //workLogger.log(TLogger.TLogLevel.VERBOSE, "Ci sono " + maxepc_distribution[i] + " cartelle con " + i + " numeri uguali ad un'altra cartella.");
                ps.println("Ci sono " + maxepc_distribution[i] + " cartelle con " + i + " numeri uguali ad un'altra cartella.");
            }
        }
        for (int i = 0; i < 6; i++) {
            if (maxepr_distribution[i] > 0) {
                //workLogger.log(TLogger.TLogLevel.VERBOSE, "Ci sono " + maxepr_distribution[i] + " cartelle con almeno una riga con " + i + " numeri uguali ad un'altra cartella.");
                ps.println("Ci sono " + maxepr_distribution[i] + " cartelle con almeno una riga con " + i + " numeri uguali ad un'altra cartella.");
            }
        }
        return ps;
    }
}           // End Of File - Rel.(1.1)
