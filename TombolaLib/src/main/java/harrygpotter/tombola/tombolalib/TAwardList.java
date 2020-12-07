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

import harrygpotter.tombola.tombolalib.TAward;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Helper list class maintaining the ascending ordered list of prizes used in a
 * tombola game.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TGame
 * @see TAward
 * @since 1.8
 */
public class TAwardList extends ArrayList<TAward> implements Serializable {
    
    /**
     * Add another award to the award list.&nbsp;It is just the standard
     * <code>add</code> method for a List object, overwritten in order to
     * maintain the award list always in ascending order by award value.
     *
     * @param e the TAward object to add to the list.
     * @return true is the list has been changed (the award has been added).
     */
    @Override
    public boolean add(TAward e) {
        // Attention! prizes must be alwais in ascending order to let TGame methods work properly.
        boolean result = super.add(e);
        this.sort((TAward one, TAward two) -> Integer.compare(one.getCategory(), two.getCategory()));
        return result;
    }

    /**
     * To be used during a tombola game, this method filters the current list of
     * awards returning a new list containing only the still available ones.
     *
     * @return a new list object containing only the awards that are still
     * available.
     */
    public List<TAward> getAvailableAwards() {
        List<TAward> avla;
        avla = new ArrayList<>();
        this.forEach((aw) -> {
            if (!aw.isAssigned()) {
                avla.add(aw);
            }
        });
        return avla;
    }

    /**
     * Return the first award object within the list that hasn't already been
     * won by a card, null if all awards have been already appointed to cards.
     *
     * @return the first award object within the list still available to be won
     * by a card.
     */
    public TAward getFirstAvailableAward() {
        for (TAward aw : this) {
            if (aw.isAvailable()) {
                return aw;
            }
        }
        return null;
    }

    /**
     * Return the last award that as been assigned to a card in time order, null
     * if no award has been already won by a card.
     *
     * @return the last award that as been assigned to a card in time order.
     */
    @Deprecated
    public TAward getLastWonAward() {
        TAward result = null;
        for (TAward aw : this) {
            if (aw.isAssigned()) {
                if (result == null) {
                    result = aw;
                } else if (aw.getWinningOrdinal() > result.getWinningOrdinal()) {
                    result = aw;
                }
            }
        }
        return result;
    }
    
    /**
     * Return a list of TAward objects containing all the award won with the extraction
     * of the number passed as input parameter
     * 
     * @param number the extracted tombola number to check for won awards
     * @return the list of TAward objects containing all the award won with the 
     * extraction of the number passed as input parameter.
     */
    public List<TAward> getWonsByExtractionNumber(int number) {
        List<TAward> result = null;
        for(TAward aw : this) {
            if (aw.isAssigned() && aw.getWinningNumber()==number) {
                if (result==null) {
                    result = new ArrayList<>();
                }
                result.add(aw);
            }
        }
        return result;
    }

    /**
     * Return a list of TAward objects containing all the award won at the count-th
     * extraction of the Tombola game.
     * 
     * @param count number of extracted numbers at with returned award where won.
     * @return the list of TAward objects containing all the award won at the count-th
     * extraction of the Tombola game.
     */
    public List<TAward> getWonsByExtractionCount(int count) {
        List<TAward> result = null;
        for(TAward aw : this) {
            if (aw.isAssigned() && aw.getWinningOrdinal()==count) {
                if (result==null) {
                    result = new ArrayList<>();
                }
                result.add(aw);
            }
        }
        return result;
    }

    /**
     * Helper static method providing a sample, standard award list containing a
     * single prize for values from AMBO to QUINTINA and then TOMBOLA.
     *
     * @return the pre-compiled list
     */
    public static TAwardList getSimpleSingleAwardList() {

        TAwardList singleAwards = new TAwardList();
        singleAwards.add(new TAward("Ambo", TAward.AMBO, new BigDecimal(1.0), "You won the Ambo!"));
        singleAwards.add(new TAward("Terno", TAward.TERNO, new BigDecimal(1.5), "You won the Terno!"));
        singleAwards.add(new TAward("Quaterna", TAward.QUATERNA, new BigDecimal(2.5), "You won the Quaterna!"));
        singleAwards.add(new TAward("Quintina", TAward.QUINTINA, new BigDecimal(5.0), "You won the Quintina!"));
        singleAwards.add(new TAward("Tombola", TAward.TOMBOLA, new BigDecimal(10.0), "You won the Tombola!"));
        return singleAwards;
    }

    /**
     * Helper static method providing a sample, standard award list containing a
     * two prizes (that is, "PRIMO" and "SECONDO") for each values from AMBO to
     * QUINTINA and then TOMBOLA.
     *
     * @return the pre-compiled list
     */
    public static TAwardList getSimpleDoubleAwardList() {

        TAwardList doubleAwards = new TAwardList();

        doubleAwards.add(new TAward("Primo Ambo", TAward.AMBO, new BigDecimal(1.0), "You won the First Ambo!"));
        doubleAwards.add(new TAward("Secondo Ambo", TAward.AMBO, new BigDecimal(0.7), "You won the Second Ambo!"));
        doubleAwards.add(new TAward("Primo Terno", TAward.TERNO, new BigDecimal(1.5), "You won the First Terno!"));
        doubleAwards.add(new TAward("Secondo Terno", TAward.TERNO, new BigDecimal(1.3), "You won the Second Terno!"));
        doubleAwards.add(new TAward("Prima Quaterna", TAward.QUATERNA, new BigDecimal(2.5), "You won the First Quaterna!"));
        doubleAwards.add(new TAward("Seconda Quaterna", TAward.QUATERNA, new BigDecimal(2.3), "You won the Second Quaterna!"));
        doubleAwards.add(new TAward("Prima Quintina", TAward.QUINTINA, new BigDecimal(5.0), "You won the First Quintina!"));
        doubleAwards.add(new TAward("Seconda Quintina", TAward.QUINTINA, new BigDecimal(4.5), "You won the Second Quintina!"));
        // simple.add(new TAward("Terza Quintina", TAward.QUINTINA));
        // simple.add(new TAward("Due Righe", 10)); //???
        doubleAwards.add(new TAward("Prima Tombola", TAward.TOMBOLA, new BigDecimal(10.0), "You won the First Tombola!"));
        doubleAwards.add(new TAward("Seconda Tombola", TAward.TOMBOLA, new BigDecimal(7.5), "You won the Second Tombola!"));
        return doubleAwards;
    }

}           // End Of File - Rel.(1.1)
