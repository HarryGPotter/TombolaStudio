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
 * TBillboardCard objects are a specialization of TCard objects, that is they
 * are tombola cards representing the six special cards traditionally composing
 * the tombola billboard.&nbsp;They all have 15 numbers each, but they are
 * usually disposed on three rows with 5 number cells each, with no
 * spaces.&nbsp;Numbers are also displaced following a criteria that is
 * substantially different from "normals" cards.
 *
 * @author Harry G Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TCard
 * @see TCardList
 * @see TGame
 * @since 1.8
 */
public class TBillboardCard extends TCard {

    private static final String BILLBOARD_LABEL = "BillBoard";

    private static int[][] prepared = new int[][]{
        {1, 2, 3, 4, 5, 11, 12, 13, 14, 15, 21, 22, 23, 24, 25},
        {6, 7, 8, 9, 10, 16, 17, 18, 19, 20, 26, 27, 28, 29, 30},
        {31, 32, 33, 34, 35, 41, 42, 43, 44, 45, 51, 52, 53, 54, 55},
        {36, 37, 38, 39, 40, 46, 47, 48, 49, 50, 56, 57, 58, 59, 60},
        {61, 62, 63, 64, 65, 71, 72, 73, 74, 75, 81, 82, 83, 84, 85},
        {66, 67, 68, 69, 70, 76, 77, 78, 79, 80, 86, 87, 88, 89, 90}
    };

    /**
     * Create a special card object representing one of the six cards composing
     * the standard Tombola Billboard.&nbsp;TBillboard objects are special cases
     * of TCard objects, so that they, and all their TCard methods, can be used
     * during a tombola game managed by a {@linkplain  TGame} object.
     *
     * @param index the index of the card to be created. Considering the
     * classical billboard displacement, card are numbered from left to right,
     * from top to down, on a table of two columns by three rows.
     */
    public TBillboardCard(int index) {
        super(BILLBOARD_LABEL + (index + 1), prepared[index], -1, false);
    }

    /**
     * Create a special card object representing one of the six cards composing
     * the standard Tombola Billboard.&nbsp;TBillboard objects are special cases
     * of TCard objects, so that they, and all their TCard methods, can be used
     * during a tombola game managed by a {@linkplain  TGame} object.
     *
     * @param label the prefix to be used to give a name to the billboard card.
     * Its name will be formed by the passed string followed by the ordinal
     * number of the card, a single digit number in the range [1,6].
     * @param index the index of the card to be created. Considering the
     * classical billboard displacement, card are numbered from left to right,
     * from top to down, on a table of two columns by three rows.
     */
    public TBillboardCard(String label, int index) {
        super(label, prepared[index], -1, false);
    }

    /**
     * Return the number on the billboard card at the specified (row, column)
     * position, using a row index within [0,2] range, as any other TCard
     * object, but a column index within [0,4] range that is different from
     * other TCard object
     *
     * @param row index of the requested number within the [0,2] range
     * @param column index of the requested number within the [0,4] range
     * @return the requested number (always present in that position if (row,
     * column) indexes are within proper ranges)
     */
    @Override
    public int getNumber(int row, int column) {
        return ((column > 4) ? 0 : getNumber(row * 5 + column));
    }

    /**
     * This method simply doesn't work for billboard cards, so it return a
     * negative value as a recognizable error code.
     *
     * @return always -2
     */
    @Override
    public int checkConformity() {
        return -2;
    }

    /**
     * Given the classical couple of (row, column) index for a billboard card
     * number, this method returns the linearized index of the same number, that
     * is its position, within the [0, 14] range, on the underlying linear
     * vector.
     *
     * @param row the row index within the [0,2] range
     * @param column the column index within the [0,4] range
     * @return the linearized index of the number in (row, column) position
     */
    @Override
    public int getLinearIndex(int row, int column) {
        return ((column > 4) ? -1 : (row * 5 + column));
    }

    /**
     * This method return a bi-dimensional, 3 row by 5 column array of
     * {@linkplain TNumberCell} objects each containing, as simple JavaBean
     * properties, all info related to a single card number and needed during a
     * game.&nbsp;Consider this data grid as an alternate representation of the
     * billboard card useful to be managed within web applications (i.e with
     * Java ServerFaces technologies, etc.).
     *
     * @return a [3,5] array of TNumberCell representing the billboard card
     */
    @Override
    public TNumberCell[][] getNumberGrid() {
        if (grid == null) {
            grid = new TNumberCell[3][5];
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 5; j++) {
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
            for (int j = 0; j < 5; j++) {
                grid[i][j].setMatched(this.isMatched(this.getNumber(getLinearIndex(i, j))));
            }
        }
        return grid;
    }

    // TCard methods I've verified they works well even on TBillboardCard objects
    // getLabel() --> OK
    // compareByRow() --> OK
    // compareByCard() --> OK
    // resetCompareResult() --> OK
    // gegJolly() --> OK
    // getJollyIndex() --> OK
    // isJolly() --> OK (because use the overriden getLinearIndex())
    // getCurrentMaxEPC() --> OK
    // getCurrentMaxEPR() --> OK
    // getNotes() --> OK
    // setNotes() --> OK
    // getOwner() --> OK
    // setOwner() --> OK
    // asString() --> OK
    // matchAsString() --> OK
    // evaluateCheckSum() --> OK
    // resetGameStatus() --> OK
    // checkExtraction() --> OK
    // getBestRowScore() --> OK
    // hasLastScoreUsedJolly() --> OK
    // getLastScoringRow() --> OK
    // getLastScore() --> OK
    // getExtractionCheckCount() --> OK
    // uncheckExtraction() --> OK
    // isMAtched() --> OK
    // isMatched() --> OK (because use the overriden getLinearIndex())
    // getTotalScore() --> OK
    // getScoreOnRow() --> OK
    // getMatchedNumbers() --> OK
    
    /**
     * Return a list of new cards containing the six "special cards" that
     * compose the classical tombola billboard.&nbsp;You can add this list of
     * TBillboardCard objects to a {@linkplain  TCardList} list object using the
     * standard addAll() method, so that even billboard cards can can
     * participate to a tombola game, just like any other normal card,
     * eventually be awarded of prizes, etc.
     *
     * @param label the label prefix to be used for each of the six billboard
     * cards
     * @return a TCardList instance containing the six billboard cards.
     */
    public static TCardList getWholeBillboard(String label) {
        TCardList tabellone = new TCardList();
        for (int i = 0; i < 6; i++) {
            tabellone.add(new TBillboardCard(label+(i+1), i));
        }
        return tabellone;
    }
}           // End Of File - Rel.(1.1)
