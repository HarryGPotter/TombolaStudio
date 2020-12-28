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

import java.util.Arrays;
import java.util.Random;
import static harrygpotter.tombola.tombolalib.TUtils.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A TSacchetto object behaves exactly as the ballot box ('sacchetto', in
 * Italian language) that contains the 90 figures of the Tombola game and is
 * used to extract them one at a time before mark each number on the Tombola
 * billboard ('Tabellone'). Numbers can be extracted just one at a time and will
 * come out in a random, unpredictable order. This object has also methods that
 * support alignment with number extraction made manually form a real, physical
 * ballot box, methods for rollback in case of mistakes, etc. Happy tombola game
 * to everyone!
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TGame
 * @see TCard
 * @since 1.8
 */
public class TSacchetto {

    private static final int SWAPS = 1713;

    private int[] numbers;
    private long randomSeed;
    private Random r;
    // ASSUMPTION:
    //   pos always points to the position on the number vector of the NEXT number to be extracted
    private int pos;

    // history[n-1] contains:
    //  -1 if n hasn't been extracted jet
    //   0 if it has been extracted at the first extraction, 1 if it has been extracted
    //     extracted at the secon extraction, ...
    private int[] history;

    /**
     * Default constructor: the TSacchetto object is filled with 90 numbers well
     * shaken and mixed up using a <i>randomly initialized</i> java.util.Random
     * number generators object, so use this constructor if you have no need to
     * 'reproduce' the exact sequence of extracted numbers.
     */
    public TSacchetto() {
        this((new Random()).nextLong());
    }

    /**
     * TSacchetto constructor that allows the TombolaLib user to specify the
     * random seed initialization value, so that it will be possible to build
     * another <i>identical</i>
     * TSacchetto object, that is a ballot box that will perform the same
     * extraction sequence.
     *
     * @param seed the random number generator initialization seed
     */
    public TSacchetto(long seed) {
        this.randomSeed = seed;
        this.r = new Random(randomSeed);
        pos = 0;
        numbers = new int[NOVANTA];
        history = new int[NOVANTA];
        for (int i = 0; i < NOVANTA; i++) {
            numbers[i] = i + 1;
            history[i] = -1;
        }
        shake();
    }

    /**
     * Return the seed long value used to initialize the Random number generator
     * used to mix and shake the numbers within the sacchetto object.
     *
     * @return long value used to initialize the Random number generator
     */
    public long getRandomSeed() {
        return this.randomSeed;
    }

    /**
     * Extract the next random number from the sacchetto, returning -1 is all
     * numbers have already been extracted and the sacchetto is empty.
     *
     * @return the next random number extracted from the sacchetto, -1 if the
     * ballot bow is empty
     */
    public int extract() {
        if (pos < NOVANTA) {
            history[numbers[pos] - 1] = pos;
            return numbers[pos++];
        } else {
            return -1;
        }
    }

    /**
     * Return the extraction count at which the number has been extracted, -1 if
     * it has not been extracted jet.
     *
     * @param number the number you want to know when has been extracted
     * @return the 'moment' (i.e. extraction count) at which the number has been
     * extracted, -1 if it is still in the ballot box.
     */
    public int getExtractionMoment(int number) {
        return history[number - 1];
    }

    /**
     * Return true is the number passed as input argument has been already extracted.
     * 
     * @param number the number to check
     * @return true is the number passed as input argument has been already extracted. 
     */
    public boolean isExtracted(int number) {
        return (history[number - 1] > -1);
    }

    /**
     * Align the sacchetto object forcing the extraction of the number passed as
     * input parameter that, of course, must be a number within the [1..90]
     * range that has not been previously extracted. Return the extracted number
     * (i.e. the same value passed in input) if it is found in the sacchetto, -1
     * otherwise.
     *
     * @param manualExtracted the number you want to extract (i.e. you have
     * extracted manually and want maintain aligned the TSacchetto object.
     * @return the extracted number (the same value passed in input) if it is
     * found in the sacchetto, -1 otherwise.
     */
    public int manuallyExtract(int manualExtracted) {
        for (int i = pos; i < NOVANTA; i++) {
            if (manualExtracted == numbers[i]) {
                numbers[i] = numbers[pos];
                numbers[pos] = manualExtracted;
                return this.extract();
            }
        }
        return -1;
    }

    /**
     * Shake the remaining numbers in the sacchetto, thus increasing the
     * randomness by which they are extracted so, call it from time to time!
     */
    public void shake() {
        for (int i = 0; i < SWAPS; i++) {
            int a = pos + r.nextInt(NOVANTA - pos);
            int b = pos + r.nextInt(NOVANTA - pos);
            int temp = numbers[a];
            numbers[a] = numbers[b];
            numbers[b] = temp;
        }
    }

    /**
     * Return the amount of already extracted numbers of tombola.
     *
     * @return the amount of already extracted numbers of tombola.
     */
    public int getExtractedCount() {
        return pos;
    }

    /**
     * Return the last number extracted from the sacchetto, without any change
     * to the status of the sacchetto itself; return -1 is no number has been
     * already extracted.
     *
     * @return the last number extracted from the sacchetto, -1 if the sacchetto
     * is new (no number has been already extracted)
     */
    public int getLastExtracted() {
        if (pos == 0) {
            return -1;
        } else {
            return numbers[pos - 1];
        }
    }

    /**
     * Return an array containing the numbers already extracted from the
     * sacchetto, in the same order by which they come out from the ballot box,
     * return null if no number has been already extracted.
     *
     * @return an array containing the numbers already extracted from the
     * sacchetto, null if no number has been already extracted
     */
    public int[] getExtractedAsArray() {
        if (pos == 0) {
            return null;
        }
        int[] extracted;
        extracted = Arrays.copyOfRange(numbers, 0, pos);
        return extracted;
    }

    /**
     * Return a List containing the numbers already extracted from the
     * sacchetto, in the same order by which they come out from the ballot box,
     * return null if no number has been already extracted.
     *
     * @return a List object containing the numbers already extracted from the
     * sacchetto, null if no number has been already extracted
     */
    public List<Integer> getExtractedAsList() {
        ArrayList<Integer> extracted = new ArrayList<>();
        if (pos > 0) {
            for (int i = pos - 1; i >= 0; i--) {
                extracted.add(numbers[i]);
            }
        }
        return extracted;
    }

    /**
     * Return the List of all numbers already extracted except the last one (see
     * {@link TSacchetto#getLastExtracted()}), ordering them from the most recent
     * extracted to the oldest one.&nbsp;An empty, not null list is returned if less 
     * than two numbers have been extracted.
     * 
     * @return the all numbers already extracted, excluded the last one.
     */
    public List<Integer> getExtractionHistory() {
        return getExtractionHistory(90);
    }

    /**
     * Return the List of numbers already extracted except the last one (see
     * {@link TSacchetto#getLastExtracted()}), ordering them from the most recent
     * extracted to the oldest one.&nbsp;An empty, not null list is returned if less 
     * than two numbers have been extracted.
     *
     * @param limit specified how many numbers should be included in the returned list.
     * 
     * @return the last 'limit' numbers already extracted, excluded the last one.
     */
    public List<Integer> getExtractionHistory(int limit) {
        ArrayList<Integer> extracted = new ArrayList<>();
        if (pos < 2) { 
            return extracted;
        }
        int start = pos - 2;
        int stop = (start - limit + 1) > 0 ? (start - limit + 1) : 0;
        for (int i = start; i >= stop; i--) {
            extracted.add(numbers[i]);
        }
        return extracted;
    }

    /**
     * Return an array containing, in ascending order, all the numbers that have
     * still not been extracted from the sacchetto, null if all 90 numbers have
     * been already extracted.
     *
     * @return an array containing, in ascending order, all the numbers that
     * have still not been extracted from the sacchetto, null if all 90 numbers
     * have been already extracted.
     */
    public int[] getToExtractAsArray() {
        if (pos == NOVANTA) {
            return null;
        }
        int[] toExtract;
        toExtract = Arrays.copyOfRange(numbers, pos, NOVANTA);
        Arrays.sort(toExtract);
        return toExtract;
    }

    /**
     * Undo the last extraction operation putting the last extracted numbers
     * again into the sacchetto object (of course, as happens using a physical
     * sacchetto, the next {@linkplain TSacchetto#extract} operation will
     * eventually return a different number!).
     *
     * @return 1 if the rollback operation succeeded, -1 in case of error (for
     * example, if you try to invoke rollback even before the first number
     * extraction)
     */
    public int rollBack() {
        return rollBack(1);
    }

    /**
     * Undo the last 'howMany' extractions from the sacchetto if enough numbers
     * have been already extracted, otherwise return -1 as error code.
     *
     * @param howMany the number of step back you want to do.
     * @return the amount of numbers re-put in the sacchetto, -1 in case of
     * error
     */
    int rollBack(int howMany) {
        if (pos >= howMany) {
            pos -= howMany;
            shake();
            return howMany;
        } else {
            return -1;
        }
    }

    /**
     * Undocumented test support code (TODO: DELETE and add JUnit Tests!)
     */
    public static void simpleTest() {
        TSacchetto s = new TSacchetto();
        System.out.println("First Extracted number: " + s.extract());
        System.out.println("Second Extracted number: " + s.extract());
        System.out.println("Third extracted number: " + s.extract());
        System.out.println("Fourth extracted number (forced): " + s.manuallyExtract(13));
        System.out.print(">>> Still to be extracted: ");
        for (int toExtract : s.getToExtractAsArray()) {
            System.out.print("" + toExtract + " ");
        }
        System.out.println();
        System.out.println("Roll back a number: " + s.rollBack());
        System.out.println("Now last extracted is: " + s.getLastExtracted());
        for (int toExtract : s.getToExtractAsArray()) {
            System.out.print("" + toExtract + " ");
        }
        System.out.println();
    }
}           // End Of File - Rel.(1.1)
