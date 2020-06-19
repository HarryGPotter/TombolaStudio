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
 * General helper class for the whole TombolaLib library, containing some
 * general constants values&nbsp;(i.e.&nbsp;hard limits for the card generation
 * algorithms, names of default choices or default values) and some general
 * helper methods.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @since 1.8
 */
public class TUtils {

    /**
     * The name of this Java library
     */
    public static final String LIB_NAME = "TombolaLib";

    /**
     * Current version of this Java library
     */
    public static final String LIB_VERSION = "1.1";

    /**
     * A simple constant to name the highest value in the game of the Tombola
     */
    public static final int NOVANTA = 90;

    /**
     * An hard limit to prevent list of series of cards with too many elements
     * to be properly managed.
     */
    public static final int MAX_SERIES = 166; // 996 Cards

    /**
     * A simple constant to maintain the delimiter for character strings in csv
     * files
     */
    public static final String STRING_DELIMITER = "\"";

    /**
     * A simple constant to maintain the field separator in csv files
     */
    public static final String FIELD_DELIMITER = ";";

    /**
     * Helper string array containing the symbolic names of all supporter list
     * of series generation algorithms implemented and available so far.
     *
     * @see TUtils#getSetFactoryByType(String)
     */
    public static final String[] AVAILABLE_GENERATION_METHODS = {"RANDOM", "PROGRESSIVE"};

    /**
     * Return an ISetFactory interface implementing object providing the list of
     * series of card generation algorithm indicated by the name in
     * input.&nbsp;Available algorithms are listed within the
     * {@linkplain TUtils#AVAILABLE_GENERATION_METHODS} public array.
     *
     * @param factoryType the symbolic name used to identify the type of the
     * desired card series factory
     * @return an ISetFactory interface implementing object providing the list
     * of series of card generation algorithm indicated by the name in input.
     *
     * @see TUtils#AVAILABLE_GENERATION_METHODS
     */
    public static ISetFactory getSetFactoryByType(String factoryType) {
        // TODO(2.0) Figure out a better way to manage factory types availability/creation
        // What about to use a string to be used with Class.forName()???
        switch (factoryType) {
            case "RANDOM":
                return new TSimpleSetFactory();
            case "PROGRESSIVE":
                return new TProgressiveSetFactory();
        }
        return null;
    }

    /**
     * Return the "tombola tens" to which the passed number belongs to.&nbsp;In
     * other words, return the column index on a tombola card of the number
     * passed as argument.
     *
     * @param number to which evaluate the proper column index on a tombola
     * card.
     * @return the column index of the number is properly displaced on a tombola
     * card.
     */
    public static final int decina(int number) {
        return ((number == NOVANTA) ? 8 : (number / 10));
    }

    /**
     * Sum 'element by element' the two vectors passed as input arguments.
     *
     * @param v first vector to add
     * @param w second vector to add
     * @return the same first vector passed as input, whose element have been
     * incremented by values of second vector w.
     */
    public static final int[] addToVectorV(int[] v, int[] w) {
        if (v == null || w == null) {
            return null;
        }
        int min = ((v.length < w.length) ? v.length : w.length);
        for (int i = 0; i < min; i++) {
            v[i] += w[i];
        }
        return v;
    }

    /**
     * Compare, 'element by element', two vectors or two sub-vectors returning
     * the count of equal couple of numbers.&nbsp;Used to verify how many equal
     * numbers there are within two cards or two card rows.
     *
     * @param v1 first vector source to compare
     * @param v2 second vector source to compare
     * @param start1 the first index within v1 vector to start to compare
     * @param start2 the first index within v2 vector to start to compare
     * @param length how many number from each vector must be compared
     * @return the total count of couple of equal numbers found.
     */
    public static final int compareSubVectors(int[] v1, int[] v2, int start1, int start2, int length) {
        int result = 0;
        for (int i = start1; i < start1 + length; i++) {
            for (int j = start2; j < start2 + length; j++) {
                result += ((v1[i] == v2[j]) ? 1 : 0);
            }
        }
        return result;
    }

    /**
     * Helper method that prepare a pretty string (in English) showing a time
     * duration. &nbsp;Usually used to show on screen or on files the time
     * elapsed to prepare a list of series.
     *
     * @param milliseconds the elapsed time expressed in milliseconds
     * @return the pretty string ready to be showed on screen or printed
     */
    public static final String prettyMilliseconds(long milliseconds) {
        // TODO(2.0) Let's manage negative milliseconds values 
        long[] fractions = new long[5];
        String[] labels = {"milliseconds", "seconds", "minutes", "hours", "days"};
        int counter = 0;
        fractions[0] = milliseconds;
        if (fractions[0] > 1000) {
            counter++;
            fractions[1] = fractions[0] / 1000;
            fractions[0] = fractions[0] % 1000;
        }
        if (fractions[1] > 60) {
            counter++;
            fractions[2] = fractions[1] / 60;
            fractions[1] = fractions[1] % 60;
        }
        if (fractions[2] > 60) {
            counter++;
            fractions[3] = fractions[2] / 60;
            fractions[2] = fractions[2] % 60;
        }
        if (fractions[3] > 24) {
            counter++;
            fractions[4] = fractions[3] / 24;
            fractions[3] = fractions[3] % 24;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = counter; i >= 0; i--) {
            if (fractions[i] > 0) {
                String label = (fractions[i] == 1 ? labels[i].substring(0, labels[i].length() - 1) : labels[i]);
                sb.append("").append(fractions[i]).append(" ").append(label);
                if (i > 0) {
                    sb.append(", ");
                }
            }

        }
        if (sb.length() > 2 && sb.charAt(sb.length() - 2) == ',') {
            return sb.substring(0, sb.length() - 2);
        } else {
            return sb.toString();
        }
    }

    // TODO(2.0) Let's check if it is more suitable to put here swap() and shakeVector()
    // functions with related global constants (mischiate) instead of repeating things both in TCard 
    // and TCardFactory
}           // End Of File - Rel.(1.1)
