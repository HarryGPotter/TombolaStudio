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

import java.io.Serializable;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Random;

/**
 * A <b>TCard</b> instance plays the role of a single "<i>cartella</i>" in the
 * traditional
 * <b><i>Italian game of Tombola.</i></b>&nbsp;Each card is composed by 15
 * numbers, randomly chosen in the range [1..90] and placed on a grid of 3 rows
 * by 9 columns, scattered so that on each row there are 4 empty spaces and 5
 * numbers each one belonging to a different <i>tens</i>.
 * <p>
 * TCard has a couple of public accessible constructors that prepare
 * "independent", "offhand" card instances having numbers randomly chosen
 * independently for each single card. These constructors are intended for
 * casual use and are not the preferred way to generate a consistent amount of
 * card objects. {@linkplain TMakeSix} class has methods to prepare groups of 6
 * cards all together, usually called a "series" of 6 cards. A series is made up
 * using all possible 90 numbers, so that there are no equal numbers among
 * cards. Using card series optimizes performance when playing Tombola,
 * hopefully reducing the probability of contemporary wins.
 * <p>
 * TombolaLib includes also classes implementing the {@linkplain ISetFactory}
 * interface and providing several heuristic algorithms aiming to generate large
 * sets of card series while trying to reduce or limit equal numbers between
 * cards and/or card rows.
 * <p>
 * Once prepared, cards can be saved and read to and from files.
 * {@linkplain TFileFormatter} objects write and read cards to and from text or
 * binary files, in several available formats, to provide means to save works
 * and to exchange information with other software (i.e. for card fine printing
 * on paper, database import/export, etc.)
 * <p>
 *
 * Finally, cards objects could be used to realize <i>real time, digital
 * twins</i> of Tombola games, specially with large groups or communities of
 * families and friends, providing support to manage cards sale and
 * distribution, automatic cards check against extracted numbers, prizes
 * assignment and enabling more engagement and fun with unusual, live
 * statistics, panic generating insights, etc. TombolaLib has classes and
 * methods to support the implementation of also these kind of applications. See
 * {@linkplain TGame} documentation for a first glance of what can be
 * possible.<p>
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @see TMakeSix
 * @see ISetFactory
 * @see TFileFormatter
 * @see TGame
 * @since 1.8
 */
public class TCard implements Serializable {

    // Everithing a simple TombolaCard has to be and do
    // ---------------------------------------------------------------- ^_^ -
    private String label = "";
    private String notes;
    private String owner;
    private int[] numbers = new int[15];
    private int jollyIndex = -1;

    private int maxepc = 0;
    private int maxepr = 0;

    private int performedChecks;
    private boolean[] matched = new boolean[15];
    private int[] rowScores = new int[3];
    private boolean jollyChecked;
    private int lastMatchingRow, lastMatchingScore;

    protected TNumberCell[][] grid = null;

    /**
     * "Offhand" constructor that can be used to randomly generate a single,
     * well formed, independent TCard object.&nbsp;Each card has a label
     * property used to uniquely identify it, specially helpful when the card
     * belongs to a (large) set of series (see the {@linkplain ISetFactory}
     * interface).
     *
     * @param label A symbolic name usually used to uniquely identify the card.
     * @see TMakeSix
     * @see ISetFactory
     */
    public TCard(String label) {
        this(label, (new Random()).nextLong(), true);
    }

    /**
     * "Offhand" constructor that can be used to randomly generate a single,
     * well formed, independent TCard object, choosing its name (label), the
     * seed used to initialize the random numbers generator and another
     * <i>aesthetic</i> parameter.&nbsp;<b>Important! Passing the same
     * <code>randomSeed</code> value to this constructor generates identical
     * card objects (same 15 numbers on the cards in the same disposition).</b>
     *
     * @param label A symbolic name usually used to uniquely identify the card.
     * @param randomSeed the seed used to initialize the pseudo random number
     * generator used to prepare the card. Use the same seed to generate cards
     * with identical numbers and numbers disposition.
     * @param avoidEmptyColumn true to avoid that on classical number
     * disposition of a card, the grid of 3 rows and 9 columns, a totally empty
     * column appears. This could be just an aesthetic issue, cards are well
     * formed and perfectly working in any case. A 'true' value for this
     * parameter could reduce performance when creating large sets of cards.
     */
    public TCard(String label, long randomSeed, boolean avoidEmptyColumn) {
        this.label = label;
        Random r = new Random(randomSeed);
        int pos = 0;
        while (pos < 15) {
            // A random namber is chosen to fill position pos
            // and then checked to see if it is equal to a number previously
            // inserted.
            numbers[pos] = 1 + r.nextInt(TUtils.NOVANTA);
            boolean number_ok = true;
            for (int i = 0; i < pos; i++) {
                if (numbers[i] == numbers[pos]) {
                    // number in pos position is already on the card.
                    number_ok = false;
                    break;
                }
            }

            // Now check is made to verify that number in pos isn't in the same
            // ten ('decina') of previously inserted numbers.
            boolean decina_ok = true;
            if (number_ok) {
                int start = (pos / 5) * 5;
                for (int i = start; i < pos; i++) {
                    if (TUtils.decina(numbers[pos]) == TUtils.decina(numbers[i])) {
                        decina_ok = false;
                        break;
                    }
                }
            }
            if (number_ok && decina_ok) {
                // If both previous checks result ok we can proceed to the next
                // number of the card.
                pos++;
            }
        }
        // Setting the jolly number for this card.
        // Even if Jolly card are not used, it is importat to have a fix number of call to the
        // Random generator so to have always identical results when working with TCard objects
        // initialized with the same seed value.
        this.jollyIndex = r.nextInt(15);

        if (avoidEmptyColumn) {
            // Search for total empty column
            int[] columnCheck = new int[9];
            Arrays.fill(columnCheck, 0);
            for (int i = 0; i < 15; i++) {
                columnCheck[TUtils.decina(numbers[i])]++;
            }
            int i = 0;
            while (i < 9) {
                if (columnCheck[i] > 0) {
                    i++;
                } else {
                    // There is an empty column, the ith one
                    int rowToMove = r.nextInt(3);
                    int newNumberToPut = r.nextInt(10) + (i * 10);
                    if (i == 0) {
                        newNumberToPut = r.nextInt(9) + 1;
                    } else if (i == 8) {
                        newNumberToPut = r.nextInt(11) + (80);
                    }
                    for (int j = rowToMove * 5; j < (rowToMove * 5 + 5); j++) {
                        if (columnCheck[TUtils.decina(numbers[j])] > 1) {
                            columnCheck[TUtils.decina(numbers[j])]--;
                            columnCheck[i]++;
                            numbers[j] = newNumberToPut;
                            break;
                        }
                    }
                }
            }
        }
        this.sortEachRow();
        this.resetGameStatus();
    }   //TODO(2.0) Insert a "infinite loop" guard where teoretically needed?

    /**
     * Return the label (that is, the name, the unique identifier) of the card.
     * Label have importance when cards are collected in huge sets and possibly
     * used by large group of friends to play. {@linkplain TSeriesList} objects
     * are used within this library to collect and deal with such large sets of
     * cards, as well as to provide 'progressive' labels for identifing them.
     *
     * @return the string containing the name of the card, usually defined at
     * card creation time.
     * @see TSeriesList#prepareLabels()
     */
    public String getLabel() {
        return label;
    }

    /**
     * Return the card number at the (row, column) position, if any, considering
     * the card as formed by three rows of nine number positions each, return
     * zero if no number is found at (row,column) position.
     *
     * @param row is the row index of the requested card number. It must be
     * within [0,2] interval.
     * @param column is the column index of the requested card number. It must
     * be within [0,8] interval.
     * @return card number in position (row, column) if any, 0 otherwise.
     */
    public int getNumber(int row, int column) {
        for (int k = 0; k < 5; k++) {
            if (TUtils.decina(numbers[row * 5 + k]) == column) {
                return numbers[row * 5 + k];
            }
        }
        return 0;
    }

    /**
     * Return the card number at position 'index', considering the card as a
     * unique, '<i>linear</i>' array of 15 numbers.
     *
     * @param index the 'linear' index of the desired number. It must be within
     * [0,14] interval.
     * <b>Attention!</b> For performance reasons no checks are made on index
     * validity. Out of bounds values will rise runtime Java exceptions.
     * @return card Number at 'index' position.
     */
    public int getNumber(int index) {
        return numbers[index];
    }

    /**
     * Check if the card object is correct and 'well formed', that is if its
     * numbers are actually disposed so that the card is a valid card for the
     * Tombola Game (five different numbers of different tens on each of the
     * three rows, ...).&nbsp;This method is useful mainly when cards objects
     * are not generated by TombolaLib constructors or generation methods, but
     * read from external, potentially uncontrolled files or other sources.
     *
     * @return -1 if the card is well formed, the position within [0,14] range
     * of the first number raising an issue.
     */
    public int checkConformity() {
        // First it checks if all numbers are in the range [1,90] and if there are any 
        // duplicate numbers.
        BitSet b = new BitSet(90);
        b.set(0, 89, false);
        for (int i = 0; i < 15; i++) {
            if (numbers[i] < 1 || numbers[i] > TUtils.NOVANTA) {
                return i;
            } else {
                if (!b.get(numbers[i] - 1)) {
                    b.set(numbers[i] - 1, true);
                } else {
                    return i;
                }
            }
        }
        // Then, it checks is there are 'decina conflicts'
        for (int i = 0; i < 3; i++) {
            boolean flag = false;
            int start = i * 5;
            for (int j = start; j < start + 5; j++) {
                for (int l = j + 1; l < start + 5; l++) {
                    if (TUtils.decina(numbers[l]) == TUtils.decina(numbers[j])) {
                        return j;
                    }
                }
            }
        }
        return -1;
    }

    /**
     * This method compare two cards (<i>this</i> one and the <i>other</i> one)
     * row by row looking for equal numbers.&nbsp;9 rows couples are compared
     * and the maximum amount of equal numbers between one of this couples is
     * both returned and saved within the state of this or the other card
     * whenever it is higher of eventually previous saved values.&nbsp;Consider
     * this method as an helping method used within card sets generator
     * algorithms to check/reduce/eliminate cards resulting "<i>too equal</i>"
     * each other, thus with higher probability to lead to concurrent wins.
     *
     * @param other The second TCard objects against to compare each rows.
     * @return the maximum amount of equal numbers between all the 9 compared
     * couples of rows.
     * @see ISetFactory
     */
    public int compareByRow(TCard other) {
        int max_found = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int count = TUtils.compareSubVectors(this.numbers, other.numbers, i * 5, j * 5, 5);
                if (count > this.maxepr) {
                    this.maxepr = count;
                }
                if (count > other.maxepr) {
                    other.maxepr = count;
                }
                if (count > max_found) {
                    max_found = count;
                }
            }
        }
        return max_found;
    }

    /**
     * This method compare two cards (<i>this</i> one and the <i>other</i> one)
     * looking for equal numbers between them.&nbsp;The amount of equal numbers
     * found is both returned and internally saved within the state of this or
     * the other card whenever it is higher of eventually previous saved
     * values.&nbsp;Consider this method as an helping method mainly used by
     * card sets generator algorithms to check/reduce/eliminate cards "<i>too
     * equal</i>" each other, thus with higher probability to lead to concurrent
     * wins.
     *
     * @param other The second TCard objects against to compare the card.
     * @return the amount of equal numbers found between the two cards.
     * @see ISetFactory
     */
    public int compareByCard(TCard other) {
        int count = TUtils.compareSubVectors(this.numbers, other.numbers, 0, 0, 15);
        if (count > this.maxepc) {
            this.maxepc = count;
        }
        if (count > other.maxepc) {
            other.maxepc = count;
        }
        return count;
    }

    /**
     * Reset the internal card variables holding the maximum equal numbers
     * founded by {@linkplain TCard#compareByCard(TCard)} and
     * {@linkplain TCard#compareByRow(TCard)} methods.
     */
    public void resetCompareResult() {
        maxepc = maxepr = 0;
    }

    /**
     * Return the number (one of the 15 numbers of the card) considered "Jolly"
     * for this card. Syntactic sugar: <code>c.getJolly();</code> is equivalent
     * to <code>c.getNumber(c.getJollyIndex());</code>
     *
     * @return the number (the value) considered "Jolly" for this card. -1 if
     * the "Jolly number" feature has not been activated and used.
     * @see TCard#getJollyIndex()
     */
    public int getJolly() {
        return (jollyIndex > 0 ? this.numbers[jollyIndex] : -1);
    }

    /**
     * Return the <i>linear</i> index of the Jolly Number of the card, that is
     * its position within the [0,14] array of numbers arranged on three rows
     * and forming the card.
     *
     * @return the <i>linear</i> position of the Jolly Number of the card. -1 if
     * the Jolly is not active on this card.
     * @see TCard#getJolly()
     */
    public int getJollyIndex() {
        return this.jollyIndex;
    }

    /**
     * Check if the number in [row, column] position exists and is Jolly and, if
     * the case, return true with no changes to the status of the card.
     *
     * @param row the row index of the number you want to check if is jolly
     * @param column the column index of the number you want to check if is
     * jolly
     * @return true if the in the [row,colum] position there is a number and it
     * is the jolly one, false otherwise.
     */
    public boolean isJolly(int row, int column) {
        return (getLinearIndex(row, column) == getJollyIndex());
    }

    /**
     * Return the maximum amount of equal numbers between two cards found for
     * this card using the
     * {@linkplain TCard#compareByCard(harrygpotter.tombola.tombolalib.TCard)}
     * method since the card creation or a call to
     * {@linkplain TCard#resetCompareResult()}.
     *
     * @return the maximum amount of equal numbers between two cards found
     * <i>until now</i> for this card.
     */
    public int getCurrentMaxEPC() {
        return this.maxepc;
    }

    /**
     * Return the maximum amount of equal numbers between each row of two cards
     * found for this card rows using the
     * {@linkplain TCard#compareByRow(harrygpotter.tombola.tombolalib.TCard)}
     * method since the card creation or a call to
     * {@linkplain TCard#resetCompareResult()}.
     *
     * @return the maximum amount of equal numbers between the rows of two cards
     * found <i>until now</i> for this card.
     */
    public int getCurrentMaxEPR() {
        return this.maxepr;
    }

    /**
     * Helper method that operates converting the [row,column] coordinates of a
     * number on the card to the 'linear' index we have when considering the 15
     * number of the card as stored on a linear array with [0, 14] positions.
     * Returns -1 if in [row, column] there is no number.
     *
     * @param row The row index of the number you want the 'linear index'. It
     * must be in the [0..2] range.
     * @param column The column index of the number you want the 'linear index'.
     * It must be in the [0..8] range.
     * @return The linear index corresponding to the number in [row, column]
     * position if it exists, -1 otherwise.
     */
    public int getLinearIndex(int row, int column) {
        for (int k = 0; k < 5; k++) {
            if (TUtils.decina(numbers[row * 5 + k]) == column) {
                return (row * 5 + k);
            }
        }
        return -1;
    }

    /**
     * Return a string of notes related to this card if previously set by
     * <code>setNotes()</code>
     *
     * @return textual notes eventually associated to this card
     */
    public String getNotes() {
        return this.notes;
    }

    /**
     * Set a text field containing free supporting info related to this card
     *
     * @param notes the string of text you want to associate to this card
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Helper TCard property aimed to hold a simply textual name representing
     * the player that eventually owns the card during a Tombola game.
     *
     * @return the player name currently owning this card
     */
    public String getOwner() {
        return this.owner;
    }

    /**
     * Set a simply text field aimed to hold the name of the player that owns
     * the card during a Tombola game.
     *
     * @param owner and identifying name of the person owning the card during a
     * tombola game
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * Return a compact but still human readable String representing the card
     * instance. The string comes in a CSV fashion line, containing:<ul>
     * <li>The card label, comprised between string delimiters;</li>
     * <li>The 15 numbers of the cards (first row; second row; third row);</li>
     * <li>The linear index of the Jolly number, closed in circular
     * brackets</li>
     * <li>A checksum code, useful to control correctness when data are
     * transferred and/or manipulated outside the TombolaLib library. This
     * checksum is evaluated using {@linkplain TCard#evaluateCheckSum(long)}
     * whit zero as offset value.</li>
     * </ul>
     * Here follows an example:
     * <p>
     * <code>&nbsp;&nbsp;"TestCard";05;10;22;73;83;25;44;55;66;76;14;34;40;62;89;(7);"KT"</code>
     * <p>
     *
     * This is just an helper method used mainly for test/control purposes.
     * Other card "conversion" and "formatting" methods are provided by
     * {@linkplain TCardFormatter} class.
     *
     * @return a compact, human readable String representing the card instance.
     * @see TCardFormatter#cardToString(TCard)
     */
    public String asString() {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(TUtils.STRING_DELIMITER).append(this.getLabel()).append(TUtils.STRING_DELIMITER);
        sb.append(TUtils.FIELD_DELIMITER);
        for (int i : numbers) {
            sb.append(String.format("%02d", i)).append(TUtils.FIELD_DELIMITER);
        }
        sb.append(String.format("(%02d)", jollyIndex)).append(TUtils.FIELD_DELIMITER);
        sb.append(TUtils.STRING_DELIMITER).append(this.evaluateCheckSum(0)).append(TUtils.STRING_DELIMITER);
        return sb.toString();
    }

    /**
     * Return a compact but still human readable String that, aligned with the
     * String returned by {@linkplain TCard#asString()}, indicates which numbers
     * have been checked or not during a Tombola game. The string comes in a CSV
     * fashion line, containing:<ul>
     * <li>The card label, comprised between string delimiters;</li>
     * <li>15 "Visual indicators": white space corresponding to numbers on the
     * card not extracted jet, asterisks corresponding to numbers of the card
     * already extracted and exclamation mark if the Jolly number has been
     * extracted.</li>
     * <li>A checksum code, useful to control correctness when data are
     * transferred and/or manipulated outside the TombolaLib library. This
     * checksum is evaluated using {@linkplain TCard#evaluateCheckSum(long)}
     * whit zero as offset value. It must be equal to the checksum returned by
     * {@linkplain TCard#asString()} method.</li>
     * </ul>
     * Here follows an example (let's suppose that 22, 55, 14 and 62 have been
     * already extracted):
     * <p>
     * <code>&nbsp;&nbsp;c.asString():&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;"TestCard";05;10;22;73;83;25;44;55;66;76;14;34;40;62;89;(7);"KT"</code><br>
     * <code>&nbsp;&nbsp;c.matchsAsString():&nbsp;&nbsp;&nbsp;"TestCard";&nbsp;&nbsp;;&nbsp;&nbsp;;&nbsp;*;&nbsp;&nbsp;;&nbsp;&nbsp;;&nbsp;&nbsp;;&nbsp;&nbsp;;!!;&nbsp;&nbsp;;&nbsp;&nbsp;;&nbsp;*;&nbsp;&nbsp;;&nbsp;&nbsp;;&nbsp;*;&nbsp;&nbsp;;"KT"</code>
     * <p>
     * This is just an helper method used mainly for test/control purposes.
     * Other card "conversion" and "formatting" methods are provided by
     * {@linkplain TCardFormatter} class.
     *
     * @return a compact, human readable String representing the card instance.
     * @see TCardFormatter#cardToString(TCard)
     */
    public String matchsAsString() {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(TUtils.STRING_DELIMITER).append(this.getLabel()).append(TUtils.STRING_DELIMITER);
        sb.append(TUtils.FIELD_DELIMITER);
        for (int i = 0; i < 15; i++) {
            if (matched[i]) {
                if (i == this.jollyIndex) {
                    sb.append("!!");
                } else {
                    sb.append(" *");
                }
            } else {
                sb.append("  ");
            }
            sb.append(TUtils.FIELD_DELIMITER);
        }
        sb.append(this.evaluateCheckSum(0));
        return sb.toString();
    }

    /**
     * Evaluate a checksum of the Card using a simple formulas applied on the
     * its numbers. It can be used to verify that nobody has changed in an
     * uncontrolled way any number of the card.
     *
     * @param offset A value that inserted into the checksum evaluation, so to
     * give variance and control possibilities to the users of this library.
     * @return A String composed by two chars belonging to this charset:
     * <code>"0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ"</code>
     */
    public String evaluateCheckSum(long offset) {
        //                 012345678901234567890123456789012345
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        long sum = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                sum += (this.numbers[i * 5 + j] * 10 ^ i);
            }
        }
        sum += offset;
        int index1 = (int) (sum % (36 * 36)) / 36;
        int index2 = (int) (sum % 36);
        return "" + alphabet.charAt(index1) + alphabet.charAt(index2);
    }

    /**
     * Reset the information (status) saved by <i>extraction checking</i>
     * methods during a Tombola Game. This method MUST be called on each TCARD
     * object before starting a game.
     */
    public final void resetGameStatus() {

        Arrays.fill(this.matched, false);
        rowScores[0] = rowScores[1] = rowScores[2] = 0;
        this.jollyChecked = false;
        this.performedChecks = 0;
        this.lastMatchingRow = -1;
        this.lastMatchingScore = -1;
    }

    /**
     * This is the fundamental method to let check if a card gain an award
     * during a Tombola game. It must be invoked at each number extraction, for
     * all the card participating to the game. The extracted number is passed as
     * its unique argument. It returns:<ul>
     * <li>-1 if the extracted number passed as argument is outside the [1..90]
     * valid range</li>
     * <li>0 if the card does not contain the extracted number <b>OR</b> if the
     * card contains the extracted number but it has already been checked with a
     * previous invocation to this method.</li>
     * <li>The total score reached on the row where the extracted number has
     * been found, <b>PLUS</b>
     * the scores of eventually other rows where a 5 score (<i>quintina
     * award</i>) has been already reached before. In this way, for instance,
     * the method return 10 when quintina is reached on two rows and 15 when
     * Tombola is reached (all numbers are checked). Otherwise the method
     * return, for instance, 3 for a new "<i>terno</i>" even if on another row
     * of the card is already present a <i>quaterna</i>.
     * </ul>
     *
     * @param extractedNumber the last number extracted and to be checked on the
     * card.
     * @return The resulting "score" of the card after the check, see
     * description above.
     *
     * @see TGame
     * @see TCard#uncheckExtraction(int)
     * @see TCard#getExtractionCheckCount()
     */
    public int checkExtraction(int extractedNumber) {
        // TODO(2.0) Cards object do not have memory of all extracted or not extracted numbers. So in
        // case of rollback or other non-sequential operation it is impossible to completely rebuild
        // the status of the cards (and the game) as if it is if all the actions are executed in the
        // rigth sequence. This consideration is worth to be written somewhere in the public javadoc
        // help.
        if (extractedNumber < 1 || extractedNumber > 90) {
            return -1;
        }
        performedChecks++;
        int result = 0;
        int matchedRow = -1;
        int matchedPosition = -1;
        for (int i = 0; i < 15; i++) {
            if ((numbers[i] == extractedNumber) && !matched[i]) {
                matched[i] = true;
                matchedPosition = i;
                matchedRow = i / 5;
                rowScores[matchedRow]++;
                result = rowScores[matchedRow];
                if (0 != matchedRow && rowScores[0] == 5) {
                    result += 5;
                }
                if (1 != matchedRow && rowScores[1] == 5) {
                    result += 5;
                }
                if (2 != matchedRow && rowScores[2] == 5) {
                    result += 5;
                }
            }
        }
        if (result != 0) {
            this.lastMatchingRow = matchedRow;       //TODO(2.0) I'm not sure which use I can do of it!
            this.lastMatchingScore = result;         //TODO(2.0) I'm not sure which use I can do of it!            
        }
        if (result != 0 && result != 15) {
            //for(int i= matchedRow*5; i< matchedRow*5+5; i++) {
            //    jollyChecked |= (matched[i] && i==jollyIndex);
            //}
            // In this way the jolly is considered only when used itself for the first time on a line.
            jollyChecked = (matchedPosition >= 0 && matchedPosition == jollyIndex);
        } else {
            jollyChecked = false;
        }
        return result;
    }

    /**
     * This method returns the value of matched numbers on the current best row
     * of the card, that is the row where the highest value of matched numbers
     * is reached.
     *
     * @return the highest score reached on a single card row, within [0,5]
     * range.
     *
     */
    public int getBestRowScore() {
        return Math.max(Math.max(rowScores[0], rowScores[1]), rowScores[2]);
    }

    /**
     * Use this method to check if in the last positive result checking numbers
     * on the card with {@linkplain  TCard#checkExtraction(int)}, jolly number
     * was involved (except if the extraction lead to the Tombola award, of
     * course).
     *
     * @return true if the last positive {@linkplain  TCard#checkExtraction(int)}
     * result on a card has involved its jolly number. False otherwise. False is
     * returned even if the card is doing Tombola (all 15 numbers are checked so
     * it is obvious that also the jolly number is involved).
     */
    public boolean hasLastScoreUsedJolly() {
        return jollyChecked;
    }

    /**
     * Return the index within [0..2] range of the row where for the last time
     * has been checked a number using the
     * {@linkplain TCard#checkExtraction(int)} method, -1 if on the card has
     * never been checked a number before or if
     * {@linkplain TCard#uncheckExtraction(int)} has been recently used.
     *
     * @return the index within [0..2] range of the row where for the last time
     * has been checked a number
     *
     * @see TCard#checkExtraction(int)
     * @see TCard#uncheckExtraction(int)
     * @see TCard#getLastScore()
     */
    public int getLastScoringRow() {
        return this.lastMatchingRow;
    }

    /**
     * Return the last "score" verified on the card by an
     * {@linkplain TCard#checkExtraction(int)} method invocation. -1 if on the
     * card has never been checked a number before or if
     * {@linkplain TCard#uncheckExtraction(int)} has been recently used.
     *
     * @return Return the last "score" verified on the card
     *
     * @see TCard#checkExtraction(int)
     * @see TCard#uncheckExtraction(int)
     * @see TCard#getLastScoringRow()
     */
    public int getLastScore() {
        return this.lastMatchingScore;
    }

    /**
     * Return the number of <i>correct</i> (i.e. with a valid extracted number
     * passed as parameter) {@linkplain TCard#checkExtraction(int)} invocations
     * since the Card creation or the last {@linkplain TCard#resetGameStatus()}
     * invocation. Attention! the checked extraction counter is incremented by
     * each invocation of {@linkplain TCard#checkExtraction(int)}, even if it is
     * wrongly called more times with the same number as argument. It is
     * decremented by {@linkplain TCard#uncheckExtraction(int) } invocations.
     *
     * @return the count of invocations to the check extraction method.
     *
     * @see TCard#checkExtraction(int)
     * @see TCard#uncheckExtraction(int)
     */
    public int getExtractionCheckCount() {
        return this.performedChecks;
    }

    /**
     * Revert game status parameters of the card as if the number passed in
     * input has never been extracted.&nbsp;Return -3 if the argument is outside
     * of the valid [1..90] range; -2 if the number is on the card but has not
     * already been checked (a mistake?!?); -1 if no number has been unchecked,
     * and, finally, the (linear) position of the unchecked number
     * otherwise.&nbsp;<b>Attention! The use of this method invalidate results
     * for<br>
     * {@linkplain TCard#hasLastScoreUsedJolly()}, {@linkplain TCard#getLastScore()}
     * and {@linkplain TCard#getLastScoringRow()} results.</b>
     *
     * @param number the number pushed back into the "sacchetto"
     * @return the (linear) position of the unchecked number in the [0..14]
     * range, a negative value otherwise.
     */
    public int uncheckExtraction(int number) {
        if (number < 1 || number > 90) {
            return -3;
        }
        if (performedChecks > 0) {
            performedChecks--;
        }
        for (int i = 0; i < 15; i++) {
            if (numbers[i] == number) {
                if (matched[i]) {
                    matched[i] = false;
                    rowScores[i / 5]--;
                    this.lastMatchingRow = -1;
                    this.lastMatchingScore = -1;
                    return i;
                } else {
                    // The number is on the card but it hasn't been already checked... WTF?
                    return -2;
                }
            }
        }
        return -1;
    }

    /**
     * Checks if a number has been already checked on a card, WITH NO CHANGES to
     * the status of the card.
     *
     * @param number Extracted number to be verified on the card
     * @return true is the number is present on the card and has been previously
     * checked, false otherwise.
     */
    public boolean isMatched(int number) {
        int i = 0;
        while (i < 15) {
            if (number == numbers[i]) {
                return matched[i];
            } else {
                i++;
            }
        }
        return false;
    }

    /**
     * Checks if a number has been already checked on a card, WITH NO CHANGES to
     * the status of the card.
     *
     * @param row the row index of the number you want to check if is jolly
     * @param column the column index of the number you want to check if is
     * jolly
     * @return true is the number is present on the card and has been previously
     * checked, false otherwise.
     */
    public boolean isMatched(int row, int column) {
        return this.isMatched(this.getLinearIndex(row, column));
    }

    /**
     * Return the current <i>total score</i> of the card, as the sum of the
     * scores reached on all of the three rows.
     *
     * @return the current <i>total score</i> of the card, as the sum of the
     * scores of the three rows.
     */
    public int getTotalScore() {
        return rowScores[0] + rowScores[1] + rowScores[2];
    }

    /**
     * Return the score on the row of the card identified by the <i>rowIndex</i>
     * parameters, -1 if the passed argument is outside the [0..2] valid range.
     *
     * @param rowIndex the index of the row you want to know the actual score.
     * @return the score on the row
     */
    public int getScoreOnRow(int rowIndex) {
        if (rowIndex < 0 || rowIndex > 2) {
            return -1;
        }
        return rowScores[rowIndex];
    }

    /**
     * Return a linear int array containing only the numbers that have been
     * already matched on the card.
     *
     * @return an int array containing only the numbers already matched on the
     * card.
     */
    public int[] getMatchedNumbers() {
        int matchedArray[] = null;
        if (this.getExtractionCheckCount() > 0) {
            matchedArray = new int[this.getTotalScore()];
            int p = 0;
            for (int i = 0; i < 15; i++) {
                if (matched[i]) {
                    matchedArray[p] = numbers[i];
                    p++;
                }
            }
        }
        return matchedArray;
    }

    /**
     * This method return a bi-dimensional, 3 row by 9 column array of
     * {@linkplain TNumberCell} objects each containing, as simple JavaBean
     * properties, all info related to a single card number and needed during a
     * game.&nbsp;Consider this data grid as an alternate representation of the
     * card useful to be managed within web applications (i.e with Java
     * ServerFaces technologies, etc.).
     *
     * @return a [3,9] array of TNumberCell representing the card
     */
    public TNumberCell[][] getNumberGrid() {
        if (grid == null) {
            grid = new TNumberCell[3][9];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    grid[i][j] = new TNumberCell();
                    grid[i][j].setNumber(this.getNumber(i, j));
                    grid[i][j].setJolly(this.isJolly(i, j));
                    
                }
            }
        }
        // TODO(2.0) check if performance can be improved, that is if the following
        // cycle can be avoided at each access to this method, mantaining even the 
        // grid up to date directly at each checkExtraction invocation.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                // TODO(2.0) Optimize...
                grid[i][j].setMatched(this.isMatched(this.getNumber(i, j)));
            }
        }
        return grid;
    }

    //TODO(2.0) Add a clone() method? or a copy() one?
    // From here on, package only or private only accessible methods...
    // ----------------------------------------------------------------------------------
    /**
     * More or less a "Copy Constructor" used by other TombolaLib classes to
     * prepare TCard objects when read from external sources (files, databases,
     * etc.). The controlConformity should be set always to true, as it force
     * the check of conformity for the 'imported' numbers.
     *
     * @param label
     * @param numbers
     * @param jollyIndex
     */
    TCard(String label, int[] numbers, int jollyIndex, boolean controlConformity) {
        this.label = label;
        this.numbers = numbers;
        if (controlConformity) {
            if (checkConformity() >= 0) {
                throw new IllegalArgumentException("Provided numbers array does not respect card rules.");
                //TODO(2.0) Use Resource Bundles instead of cabled strings?
            }
            if (jollyIndex < 0 || jollyIndex > 14) {
                throw new IllegalArgumentException("Provided Jolly Index is outside the [0..14] valid range.");
            }
        }
        this.jollyIndex = jollyIndex;
        this.resetGameStatus();
    }

    /**
     * Allows the update of the label of the card.
     *
     * @param newLabel new label to set for the card.
     */
    void changeLabel(String newLabel) {
        this.label = newLabel;
    }

    /**
     * Sort in ascending order the number of each row of the card.&nbsp;Order on
     * internal array is just matter of aesthetic when cards are saved on text
     * files: all the algorithm does not make assumptions on the order of the
     * numbers.
     */
    final void sortEachRow() {
        Arrays.sort(numbers, 0, 5);
        Arrays.sort(numbers, 5, 10);
        Arrays.sort(numbers, 10, 15);
    }
}           // End Of File - Rel.(1.1)
