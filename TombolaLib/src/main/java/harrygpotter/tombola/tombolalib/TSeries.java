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
 * TSeries objects represent series of six cards made using all 90 numbers of
 * the Tombola, thus having no equal number between them.&nbsp;Besides, There
 * are also methods to compare cards of a series versus cards of another series,
 * checking how is the maximum of equal numbers between cards or rows.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TMakeSix
 * @see ITSetFactory
 * @since 1.8
 */
public class TSeries {

    private TCard[] cards;
    private int maxepc, maxepr;

    /**
     * Create a TSeries instance using the 6 TCard objects passed as input
     * argument.&nbsp; Controls on conformity are minimal, for performance
     * reason (see {@linkplain TMakeSix}).
     *
     * @param cards the array containing exactly 6, well formed, TCard objects
     */
    public TSeries(TCard[] cards) {
        // TODO(1.2) Evaluate the opportunity to make package-only visible this class
        if (cards == null) {
            throw new TTombolaRuntimeException("<FATAL> Impossible to initialize a TSeries object with a null card vector");
        }
        if (cards.length != 6) {
            throw new TTombolaRuntimeException("<FATAL> TCards[] vector must contain exactly 6 cards");
        }
        // TODO(2.0) It should be controlled that the six cards use all 90 numbers without repetition...
        this.cards = cards;
        maxepc = maxepr = 0;
    }

    /**
     * Return the card in index position within the series
     *
     * @param index the position of card in the series, within [0..5] range.
     *
     * @return the card in index position within the series
     */
    public TCard getCard(int index) {
        return cards[index];
    }

    /**
     * Return the maximum amount of equal number that has been found between a
     * card of this series and a card of another series using the
     * {@linkplain TSeries#compareByCard(TSeries)} method since the series
     * creation or a call to {@linkplain TSeries#resetCompareResult()}.
     *
     * @return the maximum amount of equal number found between a card of this
     * series and another one <i>until now</i>.
     */
    public int getCurrentMaxEPC() {
        return maxepc;
    }

    /**
     * Return the maximum amount of equal number that has been found between a
     * row of a card of this series and a row of a card of another series using
     * the {@linkplain TSeries#compareByRow(TSeries)} method since the series
     * creation or a call to {@linkplain TSeries#resetCompareResult()}.
     *
     * @return the maximum amount of equal number found between a row of a card
     * of this series and the row of another card <i>until now</i>.
     */
    public int getCurrentMaxEPR() {
        return maxepr;
    }

    /**
     * Compare each card of this series to each card of the other one, counting
     * and returning the maximum equal numbers found between them.&nbsp;If this
     * value is the highest even found for this or the other series, it is also
     * stored internally the TSeries objects so that can be returned when
     * requested invoking {@linkplain TSeries#getCurrentMaxEPC()} method.
     *
     * @param other The series to compare whit
     * @return the maximum equal number found between the cards of the two
     * series
     */
    public int compareByCard(TSeries other) {
        int tmp_maxepc = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                int tmp = cards[i].compareByCard(other.cards[j]);
                tmp_maxepc = (tmp_maxepc > tmp) ? tmp_maxepc : tmp;
            }
        }
        maxepc = ((maxepc > tmp_maxepc) ? maxepc : tmp_maxepc);
        other.maxepc = ((other.maxepc > tmp_maxepc) ? other.maxepc : tmp_maxepc);
        return tmp_maxepc;
    }

    /**
     * Compare each card of this series to each card of the other one, counting
     * and returning the maximum equal numbers found between their rows.&nbsp;If
     * this value is the highest even found for this or the other series, it is
     * also stored internally the TSeries objects so that can be returned when
     * requested invoking {@linkplain TSeries#getCurrentMaxEPR()} method.
     *
     * @param other The series to compare whit
     * @return the maximum equal number found between the rows of the cards of
     * the two series
     */
    public int compareByRow(TSeries other) {
        int tmp_maxepr = 0;
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 6; j++) {
                int tmp = cards[i].compareByRow(other.cards[j]);
                tmp_maxepr = (tmp_maxepr > tmp) ? tmp_maxepr : tmp;
            }
        }
        maxepr = (maxepr > tmp_maxepr) ? maxepr : tmp_maxepr;
        other.maxepr = (other.maxepr > tmp_maxepr) ? other.maxepr : tmp_maxepr;
        return tmp_maxepr;
    }

    /**
     * Clear the values stored as maximum equal number by card and by row during
     * the comparisons.
     */
    public void resetCompareResult() {
        for (TCard c : cards) {
            c.resetCompareResult();
        }
        maxepr = maxepc = 0;
    }

    /**
     * Verify that there are no equal numbers between series cards.$nbsp;It
     * requires that each single card is well formed by itself (see
     * {@linkplain TCard#checkConformity()}).
     *
     * @return -1 if there ane no equal numbers between cards, the index,
     * between 0 and 5, of the first card of the series having at least one
     * number equal to a number in another card.
     */
    public int verifySeries() {
        for (int i = 0; i < 6; i++) {
            for (int j = i + 1; j < 6; j++) {
                if (cards[i].compareByCard(cards[j]) > 0) {
                    return i;
                }
            }
        }
        return -1;
    }
}           // End Of File - Rel.(1.1)
