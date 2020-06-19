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

import java.util.Random;
import java.util.Arrays;

/**
 * Here some magic creation happens! <b>TMakeSix</b> is the class implementing
 * the algorithm to prepare six TCard objects in a coordinated way, so that they
 * compose a <b><i>series of cards</i></b>, a group of six cards using all 90
 * numbers of the game so they do not have duplicates.&nbsp;
 * <b><i>Special thanks to the "Doc" for the great idea, years and years
 * ago.</i></b>
 * The algorithm can be considered 'heuristic', using a random number generator.
 * It is currently used by all other card set generator methods included in
 * TombolaLib.
 * <p>
 * <i>This class should be named TSeriesBuilder but... you know, sometimes we
 * need to divert from the main road.</i>
 * <p>
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TCard
 * @see TSeries
 * @see ISetFactory
 * @since 1.8
 */
public class TMakeSix {

    /**
     * Every great idea should have a name...
     */
    public static final String MAKESIX_METHOD_NAME = "PerfectSwap_1.1";

    private static final long FIRST_SWAPS = 1713;
    private static final long MAX_RANDOM_ITERATIONS = 5000000; // Five millions

    private long randomSeed;
    private Random r;
    private long generatedCardCounter = 0;
    private int[] randomBox = new int[90];
    // private boolean forcing[];  // TODO(3.0) add the "force preferred numbers functionality?"

    private boolean flagAvoidEmptyColumn = true;
    private boolean flagSortEachRow = true;

    /**
     * Constructor requiring a long value in input to be used as initializer for
     * the random number generator used by the algorithm that prepare the six
     * cards of the series.&nbsp;TMakeSix objects initialized with the same seed
     * will generate the same sequences of cards.&nbsp;It also require a flag
     * that enable/disable the control and elimination of totally empty column.
     *
     * @param seed value used to initialize the random number generator.
     * @param avoidEmptyColumn true to avoid that on classical number
     * disposition of a card, the grid of 3 rows and 9 columns, a totally empty
     * column appears. This could be just an aesthetic issue, cards are well
     * formed and perfectly working in any case. A 'false' value for this
     * parameter could improve performance when creating large sets of cards.
     */
    public TMakeSix(long seed, boolean avoidEmptyColumn) {
        this.randomSeed = seed;
        this.flagAvoidEmptyColumn = avoidEmptyColumn;
        r = new Random(randomSeed);
        fill();
    }

    /**
     * Constructor requiring a long value in input to be used as initializer for
     * the random number generator used by the algorithm that prepare the six
     * cards of the series.&nbsp;TMakeSix objects initialized with the same seed
     * will generate the same sequences of cards.
     *
     * @param seed value used to initialize the random number generator.
     */
    public TMakeSix(long seed) {
        this(seed, true);
    }

    /**
     * Constructor requiring a flag that enable/disable the control and
     * elimination of totally empty column.&nbsp;Random seed is initialized
     * "randomly"...&nbsp;so pay attention!
     *
     * @param avoidEmptyColumn true to avoid that on classical number
     * disposition of a card, the grid of 3 rows and 9 columns, a totally empty
     * column appears. This could be just an aesthetic issue, cards are well
     * formed and perfectly working in any case. A 'false' value for this
     * parameter could improve performance when creating large sets of cards.
     */
    public TMakeSix(boolean avoidEmptyColumn) {
        this(new Random().nextLong(), avoidEmptyColumn);
    }

    /**
     * Default constructor. Random seed is initialized "randomly"...&nbsp;so pay
     * attention!
     */
    public TMakeSix() {
        this(new Random().nextLong());
    }

    /**
     * Return the value of the seed used to initialize the random number
     * generator.
     *
     * @return the value of the seed used to initialize the random number
     * generator.
     */
    public long getRandomSeed() {
        return randomSeed;
    }

    /**
     * Return the number of card that have been already created by this TMakeSix
     * object.
     *
     * @return the number of card that have been already created by this
     * TMakeSix object.
     */
    public long getCardCounter() {
        return generatedCardCounter;
    }

    /**
     * Return the value on the configuration flag used by this TMakeSix object
     * to activate/deactivate the control to avoid empty columns on the cards.
     * The value can be set only when the TMakeSix object is created. A true
     * value prepare cards aesthetically more appealing at the cost of increased
     * generation time. A false value disables the control, thus improving
     * performance during the creation of large sets of series of cards.
     *
     * @return true is the control to avoid that a the card can be present an
     * empty colum is active, false otherwise.
     */
    public boolean isEmptyColumnAvoided() {
        return this.flagAvoidEmptyColumn;
    }

    /**
     * This is the king of the party! Using a funny and smart heuristic, with
     * some randomness, this method generates six cards (a 'series') having no
     * number in common, so using all 90 available numbers.
     *
     * @return six cards in a plain Java array having no number in common, so
     * using all 90 available numbers.
     */
    public TCard[] prepareSix() {
        long random_loop_guard = 0;

        // Step 1. Shake the randomBox
        shake();

        // Step 2. Eliminate position conflicts on each row
        int i = 0;
        while (i < TUtils.NOVANTA) {
            if (positionConflict(randomBox, i)) {
                int a = r.nextInt(TUtils.NOVANTA);
                int temp = randomBox[i];
                randomBox[i] = randomBox[a];
                randomBox[a] = temp;
                i = 0;
            } else {
                i++;
            }
            // TODO(2.0) Use resource bundles instead of string literals in the code...
            if (++random_loop_guard > MAX_RANDOM_ITERATIONS) {
                throw new TTombolaRuntimeException("Max iteractions limit has been reached. Something goes wrong.");
            }
        }

        // Step 3. (OPTIONAL) Control and eliminate totally empty columns
        if (flagAvoidEmptyColumn) {
            i = 0;
            while (i < 6) {
                if (findEmptyColumn(i) > -1) {
                    swapVectors(randomBox, randomBox, i * 15 + 5 * r.nextInt(3), r.nextInt(6) * 15 + 5 * r.nextInt(3), 5);
                    i = 0;
                } else {
                    i++;
                }
                // TODO(2.0) Use resource bundles instead of string literals in the code...
                if (++random_loop_guard > MAX_RANDOM_ITERATIONS) {
                    throw new TTombolaRuntimeException("Max iteractions limit has been reached. Something goes wrong.");
                }
            }
        }

        // Step4. (OPTIONAL) Sort each rows of five numbers.
        if (flagSortEachRow) {
            for (i = 0; i < 18; i++) {
                Arrays.sort(randomBox, 5 * i, 5 * (i + 1));
            }
        }

        // Step 5. Prepare six card to return
        TCard[] newSeries = new TCard[6];
        for (i = 0; i < 6; i++) {
            newSeries[i] = new TCard("", Arrays.copyOfRange(randomBox, i * 15, i * 15 + 15), r.nextInt(15), false);
            generatedCardCounter++;
        }
        return newSeries;
    }

    /**
     * This method returns a fresh new TSeries object, made by six cards
     * generated using {@link TMakeSix#prepareSix()} method.
     *
     * @return six TCards objects wrapped in a TSeries object.
     * 
     */
    public TSeries prepareSeries() {
        return new TSeries(prepareSix());
    }
    
    /**
     * Fill the randomBox array with numbers from 1 to 90, in ascending order
     */
    private void fill() {
        for (int i = 0; i < TUtils.NOVANTA; i++) {
            randomBox[i] = i + 1;
        }
    }

    /**
     * Execute a fixed amount of swaps between couples of numbers randomly
     * chosen on the randomBox array. Using a fixed amount of swaps support the
     * possibilities to generate identical sequences of cards if different
     * TMakeSix objects are initialized with the same seeds.
     */
    private void shake() {
        for (int i = 0; i < FIRST_SWAPS; i++) {
            int a = r.nextInt(TUtils.NOVANTA);
            int b = r.nextInt(TUtils.NOVANTA);
            int temp = randomBox[a];
            randomBox[a] = randomBox[b];
            randomBox[b] = temp;
        }
    }

    /**
     * Swap the corresponding elements of two arrays or sub-arrays of integers.
     *
     * @param v1 is the first array of integers
     * @param v2 is the second array of integers
     * @param v1start the index of the first element to swap on the first array
     * @param v2start the index of the first element to swap on the second array
     * @param length the number of elements to swap between the two arrays
     */
    private void swapVectors(int[] v1, int[] v2, int v1start, int v2start, int length) {
        //TODO(2.0) Evaluate is this method can be best positioned as static method 
        // in TUtil class, becouse used also by other classes within the package.
        for (int i = 0; i < length; i++) {
            int t = v1[i + v1start];
            v1[i + v1start] = v2[i + v2start];
            v2[i + v2start] = t;
        }
    }

    /**
     * Check if the n-th element of the array has "conflicts" with its neighbors
     * in the same sub-array of 5 elements (each sub-array starts at positions
     * 0, 5, 10, 15, ...). A conflict happens when there are two or more numbers
     * within the same tens.
     *
     * @param vector the array containing the numbers to check against
     * @param n the number to be checked
     * @return false if there is no conflict, true otherwise.
     */
    private boolean positionConflict(int[] vector, int n) {
        boolean flag = false;
        int start = (n / 5) * 5;
        for (int i = start; i < start + 5; i++) {
            flag |= ((n != i) && (TUtils.decina(vector[n]) == TUtils.decina(vector[i])));
        }
        return flag;
    }

    /**
     * Return the number in position row, column of a specified card of the
     * series.
     *
     * @param card the index of the card
     * @param row the index of the row on the chosen card
     * @param column the index of the column
     * @return return the number is present, 0 otherwise.
     */
    private int numberByPosition(int card, int row, int column) {
        for (int k = 0; k < 5; k++) {
            if (TUtils.decina(randomBox[card * 15 + row * 5 + k]) == column) {
                return randomBox[card * 15 + row * 5 + k];
            }
        }
        return 0;
    }

    /**
     * Returns the index value (in the range [0,8]) of the first totally empty
     * column founded in the passed card, returns -1 if no empty column is
     * found.
     *
     * @param card TCard object to inspect
     * @return index value of the first totally empty column founded, -1
     * otherwise.
     */
    private int findEmptyColumn(int card) {
        //
        for (int j = 0; j < 9; j++) {
            boolean flag = true;
            for (int i = 0; i < 3; i++) {
                flag &= (numberByPosition(card, i, j) == 0);
            }
            if (flag) {
                return j;
            }
        }
        return -1;
    }
}           // End Of File - Rel.(1.1)
