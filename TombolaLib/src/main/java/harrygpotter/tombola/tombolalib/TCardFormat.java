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
 * Used to specify the String format that is possible to use to visualize a card
 * or save it in files.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TCard
 * @see TCardFormatter
 * @see TFileFormatter
 * @since 1.8
 */
public enum TCardFormat {

    /**
     * This TCardFormat value is used specifically when reading files, to let {@linkplain
     * TFileFormatter#readSeriesFile(String)} try to auto-detect the format used
     * to store the cards using meta-information provided within the header
     * comments of the file itself.
     */
    AUTO,
    /**
     * Prepare a unique string containing the card label, 27 Numbers on a row
     * (zeros in empty positions), plus the Jolly index within the [0..14] range
     * at the end.
     */
    CSV,
    /**
     * Prepare a unique string containing the card label, 27 Numbers on a row
     * (zeros in empty positions), followed by the Jolly index within the
     * [0..14] range and the maxEqualPerCard and maxEqualPerRow values.
     */
    CSV_PLUS,
    /**
     * Prepare a unique string containing the card label, 15 Numbers on a row
     * (empty positions are skipped), plus the Jolly index within the [0..14]
     * range at the end.
     */
    CSV_PACKED,
    /**
     * Prepare a unique string containing the card label, 15 Numbers on a row
     * (empty positions are skipped), followed by the Jolly index within the
     * [0..14] range and the maxEqualPerCard and maxEqualPerRow values.
     */
    CSV_PACKED_PLUS,
    /**
     * The card is 'nicely' visualized on three rows, putting numbers aligned in
     * their proper columns and leaving spaces where numbers are not
     * present.&nbsp;Jolly number is enclosed in square brackets.&nbsp;If
     * {@linkplain TCard#getExtractionCheckCount()} return a value greater than
     * zero, extracted numbers are marked within rounded brackets.&nbsp;If the
     * Jolly number has been extracted, square brackets are substituted by
     * asterisks.
     */
    PRETTY,
    /**
     * TODO(2.0) Still Unsupported
     */
    TINY,
    /**
     * TODO(2.0) Still Unsupported
     */
    TINY_SPACED,
    /**
     * TODO(2.0) Still Unsupported
     */
    JSON,
    /**
     * TODO(2.0) Still Unsupported
     */
    XML,
    
    /**
     * TODO(2.0) Still Unsupported
     */
    SQL,
    
    /**
     * TODO(1.2) Write comment here
     * The file aims to be ready to be used within a Microsoft Word template leveraging
     * its "mail merge" functionalities. To achieve this goal the file has an heading line 
     * with columns (fields) names, 27 numbers for each card (so, putting zeros where 
     * there are no numbers on the matrix), no comment lines and use the real jolly 
     * number instead of its index, without brackets.
     */
    MSWORD_MAILMARGE
}           // End Of File - Rel.(1.1)
