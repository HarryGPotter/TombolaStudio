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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A TAward object has a category (<b>AMBO</b> = two number in a row,
 * <b>TERNO</b> = three number in a row, ...&nbsp;up to <b>TOMBOLA</b>, 15 numbers 
 * matched on a card) and represent a prize that cards can win playing the game.&nbsp;TAward
 * objects should be prepared before a game starts and collected in a
 * {@linkplain TAwardList} object, where they must be arranged in ascending
 * order to let {@linkplain TGame} methods work properly.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TAwardList
 * @see TGame
 * @since 1.8
 */
public class TAward implements Serializable {

    /**
     * A card wins the AMBO if it is the first card on the game to match 2
     * numbers on the same a row.
     */
    public static final int AMBO = 2;
    /**
     * A card wins the TERNO if it is the first card on the game to match 3
     * numbers on the same a row.
     */
    public static final int TERNO = 3;
    /**
     * A card wins the QUATERNA if it is the first card on the game to match 4
     * numbers on the same a row.
     */
    public static final int QUATERNA = 4;
    /**
     * A card wins the QUINTINA if it is the first card on the game to match all
     * the 5 numbers on a row.
     */
    public static final int QUINTINA = 5;
    /**
     * A card wins the TOMBOLA if it is the first card on the game to match all
     * the 15 numbers on the card.
     */
    public static final int TOMBOLA = 15;

    private static final String[] CATEGORY_LABELS = {"", "", "Ambo", "Terno",
        "Quaterna", "Quintina", "", "", "", "", "", "", "", "", "", "Tombola"};

    /**
     * Possible award statuses used by {@linkplain TGame} methods while checking
     * is one or more cards have won an award following each number extraction.
     */
    public static enum TAwardStatus {

        /**
         * The award has not been assigned jet by any playing card.
         */
        AVAILABLE,
        
        /**
         * The award has 'candidate' cards (see CONTENDED TAwardStatus) but they
         * need to be manually validated/accepted by the Game croupier.&nbsp;See
         * also {@link TGame#confirmCandidate(TCard)}
         */
        VALIDATING,
        
        /**
         * After the extraction of a number, there are more than one card
         * matching this award score, so they are all 'candidates' among which a
         * winner must be chosen.&nbsp;During this choice, the award will be in
         * this 'contended' status, pausing (blocking) the extraction of new
         * numbers until the award will be assigned.
         */
        CONTENDED,
        
        /**
         * The award has been already assigned to card within the tombola game.
         */
        ASSIGNED
    }

    private String label;
    private int category;
    private TAwardStatus status = TAwardStatus.AVAILABLE;

    private final List<TCard> validating = new ArrayList<>();
    private final List<TCard> candidates = new ArrayList<>();
    private final List<TCard> winners = new ArrayList<>();
    private final List<Boolean> winWithJollies = new ArrayList<>();

    private int winningNumber = -1;     // Stores the extraction count that led to the assignment of this award to a card.
    private int winningOrdinal = -1;    // Stores the extraction count that led to the assignment to a card.
    private int conflictOrdinal = -1;   // Stores the extraction count that led multiple cards to be simultaneously contender of the same award. 
    private int conflictCount = 0;      // Sum up the number of contending cards (if they are more than one) for the award

    private BigDecimal value;
    private String extraInfo;
    // private String giftSet;  I just was wondering if it should be funny to manage "gifts" associated to each prizes

    /**
     * Instantiate an award for a game. You have just to name it and associate a
     * category to it.
     *
     * @param label The symbolic label identifying the award (you can use
     * "AMBO", "TERNO", etc.).
     * @param category The integral score associated to each prize (2 = AMBO, 3
     * = TERNO, ..., 15 = TOMBOLA). Valid values are within the [2,15] range.
     */
    public TAward(String label, int category) throws IllegalArgumentException {
        if ((category < 2 || category > 15) || (category > 5 && category != 15)) {
            throw new IllegalArgumentException("<Error!> Tombola Award values must be in the [2..5, 15] range.");
        }
        // TODO(2.0) Add "Two rows" or "Rampazzo" award support?
        this.label = label;
        this.category = category;
    }

    /**
     * Instantiate an award for a game. You have just to name it and associate a
     * category to it.
     *
     * @param label The symbolic label identifying the award (you can use
     * "AMBO", "TERNO", etc.).
     * @param category The integral score associated to each prize (2 = AMBO, 3
     * = TERNO, ..., 15 = TOMBOLA). Valid values are within the [2,15] range.
     * @param value a numeric (monetary?) value associated
     * @param extraInfo a free string where to store addition info for the
     * award.
     */
    public TAward(String label, int category, BigDecimal value, String extraInfo) {
        this(label, category);
        this.value = value;
        this.extraInfo = extraInfo;
    }

    /**
     * Return the identifying label of the award.
     *
     * @return the symbolic label of the award
     */
    public String getLabel() {
        return label;
    }

    /**
     * Return the integral category of the prize (2 = AMBO, 3 = TERNO, ...)
     *
     * @return the integral category of the prize (2 = AMBO, 3 = TERNO, ...)
     */
    public int getCategory() {
        return category;
    }

    /**
     * Return a string for the award category
     *
     * @return Return a string for the award category
     */
    public String getCategoryName() {
        return TAward.CATEGORY_LABELS[this.category];
    }

    /**
     * Return the 'status' of an award object, used during a tombola game to
     * properly check cards and assign prizes in the correct order.
     *
     * @return the 'status' of the award objects (see
     * {@link TAwardStatus for allowed values}
     */
    public TAwardStatus getStatus() {
        return status;
    }

    /**
     * Set the 'status' of an award object, used during a tombola game to
     * properly check cards and assign prizes in the correct order.
     *
     * @param status new status you want to put the award (see
     * {@link TAwardStatus for allowed values}
     */
    public void setStatus(TAwardStatus status) {
        this.status = status;
    }

    /**
     * Return the number whose extraction led to the assignment of this award to
     * a card.
     *
     * @return the number whose extraction led to the assignment of this award
     * to a card.
     */
    public int getWinningNumber() {
        return winningNumber;
    }

    // Only TGame can set the winningNumber
    void setWinningNumber(int winningNumber) {
        this.winningNumber = winningNumber;
    }

    /**
     * Return the extractions counter at the moment this award as been
     * appointed to a card.
     *
     * @return the extractions counter category at the moment this award as been
     * appointed to a card.
     */
    public int getWinningOrdinal() {
        return this.winningOrdinal;
    }

    // Only TGame can set the winningOrdinal
    void setWinningOrdinal(int winningOrdinal) {
        this.winningOrdinal = winningOrdinal;
    }
    
    /**
     * Return the extractions counter at the moment for this award more than one
     * card have been candidate to win the award.&nbsp;Mainly used for statistics and
     * data analysis.
     * 
     * @return the extractions counter at the moment for this award more than one
     * card have been candidate to win the award.
     */
    public int getConflictOrdinal() {
        return this.conflictOrdinal;
    }
    
    // Only TGame can set the conflictOrdinal
    void setConflictOrdinal(int conflictOrdinal) {
        this.conflictOrdinal = conflictOrdinal;
    }
    
    /**
     * Return the number of cards that are simultaneously ready to win the 
     * award.&nbsp;Mainly used for statistics and data analysis.
     * 
     * @return the number of cards that are simultaneously ready to win the award.
     */
    public int getConflictCount() {
        return this.conflictCount;
    }
    
    // Only TGame can set the conflictCount
    void setConflictCount(int conflictCount) {
        this.conflictCount = conflictCount;
    }

    /**
     * To properly manage winners during a tombola game, to each award is
     * associated a list of cards that, as the game progresses, will maintain
     * references to the card or the cards winning the award. And... yes, the
     * list is needed because more than "a single card" can be associated to an
     * award. In Italy, if two players hit a prize (say TERNO) after the
     * extraction of the same number, usually they can decide to divide (that
     * is, "spartimm") the prize.
     *
     * @return the list of card(s) considered winner(s) of this prize
     * @see TGame#resolveCandidates(int)
     */
    public List<TCard> getWinnerList() {
        return this.winners;
    }

    /**
     * Helping list used to maintain cards hitting the score for the award while
     * checking all the playing cards within a game (@linkplain TGame}. When all
     * cards have been checked after a number extraction, the candidateList is
     * been used by invoking the {@linkplain TGame#resolveCandidates(int)}
     * method to choose one or more final winner for the award.
     *
     * @return the current list of cards candidate to be assigned the award.
     * @see TGame
     * @see TGame#extractNumber(int)
     */
    public List<TCard> getCandidatesList() {
        return this.candidates;
    }

    /**
     * Return the list of cards waiting to be explicitly accepted or denied to
     * contend for a tombola award. To be used when
     * {@link TGame#setConfirmCandidateOn(boolean)} is set to true. Return
     * null is the award isn't in VALIDATING status.
     *
     * @return the list of cards waiting to be explicitly accepted or denied to
     * contend for a tombola award. Null is the award isn't in VALIDATING
     * status.
     */
    public List<TCard> getValidatingList() {
        return this.validating;
    }

    /**
     * When {@link TGame#setConfirmCandidateOn(boolean)} is set to true, this
     * method allows to explicitly verify and accept a Card candidate for a
     * tombola award. Return true is the card has been properly accepted, false
     * if the card is not in the validating list for this award. If there are no
     * more card to validate after the last number extraction (see
     * {@link TGame#extractNumber(int)}) the award status is also changed from
     * VALIDATING to CONTENDED.
     *
     * @param card the TCard object that should be present in the current
     * "validating" List for this award.
     *
     * @return true if the card has been validated, false if it is not present
     * in the validating list or the award is not in VALIDATING status.
     */
    public boolean accept(TCard card) {
        if (status == TAwardStatus.VALIDATING) {
            if (validating.remove(card)) {
                candidates.add(card);
                if (validating.isEmpty()) {
                    status = TAwardStatus.CONTENDED;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * When {@link TGame#setConfirmCandidateOn(boolean)} is set to true, this
     * method allows to explicitly verify and DENY a Card candidate for a
     * tombola award. Return true is the card has been effectively denied, false
     * if the card is not in the validating list for this award. If there are no
     * more card to validate after the last number extraction (see
     * {@link TGame#extractNumber(int)}) the award status is also changed from
     * VALIDATING to CONTENDED.
     *
     * @param card the TCard object that should be present in the current
     * "validating" List for this award.
     *
     * @return true if the card has been denied, false if it is not present in
     * the validating list or the award is not in VALIDATING status.
     */
    public boolean deny(TCard card) {
        if (status == TAwardStatus.VALIDATING) {
            if (validating.remove(card)) {
                if (validating.isEmpty() && candidates.isEmpty()) {
                    status = TAwardStatus.AVAILABLE;
                }
                return true;
            }
        }
        return false;
    }

    /**
     * If a card wins this award using also its Jolly Number, that is is put
     * also in this list.
     *
     * @return the list containing all the cards that have been assigned this
     * award AND have won it using also their Jolly number.
     */
    public List<Boolean> getWithJollyList() {
        return this.winWithJollies;
    }

    /**
     * Helping method, return true if the award is still AVAILABLE to be won by
     * a card
     *
     * @return true if the award is still AVAILABLE to be won by a card
     */
    public boolean isAvailable() {
        return (status == TAwardStatus.AVAILABLE);
    }

    /**
     * Helping method, return true if the award has already been won by a card
     *
     * @return true if the award has already been won by a card
     */
    public boolean isAssigned() {
        return (status == TAwardStatus.ASSIGNED);
    }

    /**
     * Helping method, return true if the award actually is contended by two or
     * more cards hitting needed score after the extraction of the same number.
     *
     * @return true if the award actually is contended by two or more cards
     * hitting needed score after the same extraction
     */
    public boolean isContended() {
        return (status == TAwardStatus.CONTENDED);
    }

    public boolean isValidating() {
        return (status == TAwardStatus.VALIDATING);
    }

    public BigDecimal getValue() {
        return this.value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public String getExtrainfo() {
        return this.extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }
}           // End Of File - Rel.(1.1)
