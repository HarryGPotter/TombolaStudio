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
import java.util.List;

/**
 * TombolaGame class is a funny piece of code, where a Tombola game match can be
 * simulated, monitored, controlled and analyzed.&nbsp;I like it very much! It
 * is another piece of magic for me!
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.2
 * @see TCard
 * @see TSacchetto
 * @see TAward
 * @see TAwardList
 * @since 1.8
 */
public class TGame implements Serializable {

    public static int DEFAULT_SUPERBOMBOLA = 40;

    private String id;
    private String label;
    private TGameStatus status = TGameStatus.INITIALIZING;
    private TGameResultCode lastResultCode;
    private TSacchetto sacchetto;
    private TCardList tabellone;
    private TCardList cards;
    private TAwardList awards;
    private boolean jollyOn = true;
    private boolean confirmCandidateOn = false;
    private int exCount = 0;
    private int lastEx = 0;
    private int lastMatchingCount = 0;
    private long ts_start, ts_end;
    private ITLogger logger;

    // As the game proceeds, winVector stores the extration count at which each
    //  award category is won. Example: winVector[0][7] will contain 1 if the Ambo has been
    //  won at the seventh extraction, etc. It is used for data analysis and statistics
    //  purposes.
    private final int[][] winVector = new int[6][90];

    // As the game proceeds, conflictVector stores the extration count at which more than
    //  one card are candidates to win the same award. Example: conflictVector[0][7] will
    //  contain 3 if three cards are ready to win the Ambo after the seven-th extraction, etc.
    //  It is used for data analysis and statistics purposes.
    private final int[][] conflictVector = new int[6][90];

    // TODO(2.0) private int superTombola = 91;
    // TODO(2.0) private ITGameObserver funFactsEval = null;
    // TODO(2.0) private TFunFacts funFactsEval = null; * DELETE *

    /**
     * Quick constructor that requires just an id String to give an identifiable
     * name to your TGame object.&nbsp;The id is also used as descriptive name
     * (label) for the game.&nbsp;See {@link TGame#TGame(String, String)}
     *
     * @param id a name to identify this specific tombola game.
     */
    public TGame(String id) {
        this(id, id);
    }

    /**
     * Main constructor for TGame object.&nbsp;All you need to specify is an id
     * that will help to identify this specific game, and a label, possibly with
     * a more descriptive or funny name.&nbsp;A brand new TGame object is in
     * 'Initializing' state and all <i>components</i> needed to start a game
     * MUST be created apart and specified using proper setter methods: a
     * sacchetto, a list of gaming cards, a tabellone, the list of available
     * awards and a logger.
     *
     * @param id a name to identify this specific tombola game.
     * @param label a more descriptive or funny name for the tombola game
     */
    public TGame(String id, String label) {
        if (id == null || id.length() < 1) {
            throw new IllegalArgumentException("<ERROR> Each Tombola Game must be identifiable using a not null and not empty name");
        }
        this.id = id;
        this.label = label;
        for (int i = 0; i < 6; i++) {
            Arrays.fill(winVector[i], 0);
            Arrays.fill(conflictVector[i], 0);
        }
    }

    /**
     * Return the name that identify this specific tombola game object.
     *
     * @return the name that identify this specific tombola game object.
     */
    public String getId() {
        return this.id;
    }

    /**
     * Return the descriptive, possibly funny, name for this game object.
     *
     * @return the descriptive, possibly funny, name for this game object.
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Return the current status of this tombola object (see
     * {@linkplain TGameStatus})
     *
     * @return the current status of this tombola object
     */
    public TGameStatus getStatus() {
        return this.status;
    }
    
    /**
     * Associate to this TGame instance the six special cards forming the tombola 
     * billboard (i.e.&nbsp;the 'tabellone').&nbsp;They to not participate to award
     * assignment, unless they are included in the TCardList object passed using the
     * {@link TGame#setCards(TCardList)} method, but are equally updated at each extraction,
     * so they can be used to display the game status.
     * 
     * @param tabellone a TCardList object containing the special six cards of a tombola
     * billboard. They must be six and of {@link TBillboardCard} subclass of TCard
     */
    public void setTabellone(TCardList tabellone) {
        if (status != TGameStatus.INITIALIZING && status != TGameStatus.READY) {
            throw new TTombolaRuntimeException("Billboard cards can be added only before the game has started.");
        }
        if (tabellone != null) {
            if(tabellone.size() !=6 ) {
                throw new TTombolaRuntimeException(String.format("Found %d billboard cards. They bust be just six.", tabellone.size()));
            }
            for(TCard b: tabellone) {
                if(!(b instanceof TBillboardCard)) {
                    throw new TTombolaRuntimeException("Billboard TCard(s) must all be of TBillBoard type.");
                } else {
                    b.resetGameStatus();
                }
            }
        }
        this.tabellone = tabellone;
    }

    /**
     * Set the <i>sacchetto</i> object (that is, the cloth sack) used to draw
     * numbers during the tombola game.&nbsp; This method must be invoked only
     * when the TGame object is in 'Initializing' status, that is before a match
     * has been started (the first number has been extracted).&nbsp;Besides, the
     * sacchetto object must be fresh new and contain all 90 numbers.
     *
     * @param sacchetto A brand new sacchetto, containing all 90 numbers to
     * play.
     *
     * @see TSacchetto
     */
    public void setSacchetto(TSacchetto sacchetto) {
        if (status != TGameStatus.INITIALIZING) {
            throw new TTombolaRuntimeException("Cloth sach to extract numbers can be set only during the initialization phase.");
        } else if (sacchetto.getExtractedCount() > 0) {
            throw new TTombolaRuntimeException("Cloth sach to extract numbers must be fresh new and contain all 90 numbers to be used in this game!");
        }
        this.sacchetto = sacchetto;
        this.checkInitialization();
    }

    /**
     * Return the <i>sacchetto</i> object (the cloth sack) associated to this
     * game.
     *
     * @return the <i>sacchetto</i> object (the cloth sack) associated to this
     * game.
     *
     * @see TSacchetto
     */
    public TSacchetto getSacchetto() {
        return this.sacchetto;
    }

    /**
     * Return a list of card object containing the six "special" cards forming
     * the tombola billboard associated to this game. The bill board is
     * automatically created during the initialization phase, when a sacchetto
     * object is set for the game.
     *
     * @return the list of six billboard cards associated to this game
     *
     * @see TBillboardCard#getWholeBillboard(String)
     */
    public TCardList getTabellone() {
        return tabellone;
    }

    /**
     * Set the logger object that will be used to spool all relevant messages
     * regarding the status of the game. It MUST be set during the
     * initialization phase of the game and cannot be changed after the first
     * number has been already extracted.&nbsp;See {@link ITLogger} and
     * {@linkplain TSimpleLogger} to check all possibilities you have to catch
     * tombola game messages or to define your custom logger to spool messages
     * in the way you prefer (on files, on databases, etc.).
     *
     * @param logger the logger object that will be used during the game to
     * spool all relevant game messages
     */
    public void setLogger(ITLogger logger) {
        if (status != TGameStatus.INITIALIZING) {
            throw new TTombolaRuntimeException("Logger cannot be changed during the game. It can be set only during the initialization phase.");
        }
        this.logger = logger;
        this.checkInitialization();
    }

    /**
     * Return the ITLogger interface implementing object associated to this game.
     *
     * @return the ITLogger interface implementing object associated to this
 game.
     *
     * @see TGame#setLogger(ITLogger)
     */
    public ITLogger getLogger() {
        return this.logger;
    }

    /**
     * Set the flag indicating if jolly numbers configured on cards must be
     * considered when playing the game.
     *
     * @param useJolly true to let this TGame object consider the jolly numbers
     * on cards, false to ignore them (that is considering them as any other
     * number on cards)
     */
    public void setJollyOn(boolean useJolly) {
        if (status != TGameStatus.INITIALIZING && status != TGameStatus.READY) {
            throw new TTombolaRuntimeException("Use of Jolly numbers cannot be changed during the game. It can be set only during the initialization phase.");
        }
        this.jollyOn = useJolly;
    }

    /**
     * Return true if jolly numbers are considered 'special' and highlighted in
     * log messages during the game, false if they are ignored, that is
     * considered as any other normal number on cards.
     *
     * @return if jolly numbers are considered 'special' or not.
     */
    public boolean getJollyOn() {
        return this.jollyOn;
    }

    /**
     * Set the list containing all the cards that will be used during the
     * tombola game.&nbsp;TCardList objects can be prepared starting from
     * {@linkplain TSeriesList} objects, that are usually used to store and load
     * series of cards to and from files.
     *
     * @param cards the list containing all the cards that will be used during
     * the tombola game.
     */
    public void setCards(TCardList cards) {
        if (status != TGameStatus.INITIALIZING) {
            throw new TTombolaRuntimeException("Gaming cards set cannot be changed during the game. It can be set only during the initialization phase.");
        }
        if (null == cards || cards.size() < 1) {
            throw new TTombolaRuntimeException("Gaming cards set cannot be null and must contain at least one card.");
        }
        this.cards = cards;        
        this.cards.resetGameStatus();
        this.checkInitialization();
    }

    /**
     * Return the card list object containing all the cards participating to the
     * game.
     *
     * @return the card list object containing all the cards participating to
     * the game
     */
    public TCardList getCards() {
        return this.cards;
    }

    /**
     * When set to true (default is false), this parameter stops the evaluation
     * process executed after each number extraction and allows the user to
     * explicitly accept or deny cards that have reached the score for an
     * available award.&nbsp;So, for example, it is possible to exclude (deny) a
     * card if the player do not claim her/his award.
     *
     * @param confirmCandidateOn true to allow for explicit confirmation of all
     * cards candidate to win an award after a number extraction, false to let
     * TGame object to proceed automatically considering all cards that have
     * reached proper score.
     *
     * @see TGame#assign()
     * @see TGame#confirmCandidate(TCard)
     * @see TGame#denyCandidate(TCard)
     */
    public void setConfirmCandidateOn(boolean confirmCandidateOn) throws TTombolaRuntimeException {
        if (status != TGameStatus.INITIALIZING && status != TGameStatus.READY) {
            throw new TTombolaRuntimeException("Candidate Confirmation flag can be modified only before the game has started.");
        }
        this.confirmCandidateOn = confirmCandidateOn;
    }

    /**
     * Return the elapsed time, in milliseconds, since the beginning of this
     * tombola game.&nbsp;If the game hasn't already been started, this method
     * raise a runtime exception.&nbsp;If the game has already finished, this
     * method returns the duration of the game.
     *
     * @return the elapsed time, in milliseconds, since the beginning of this
     * tombola game.
     *
     * @see TUtils#prettyMilliseconds(long)
     */
    public long getElapsedTime() throws TTombolaRuntimeException {
        if (status == TGameStatus.INITIALIZING || status == TGameStatus.READY) {
            throw new TTombolaRuntimeException(String.format("Tombola Game <%s> has not been started. No number has been extracted jet.", this.id));
        }
        if (status == TGameStatus.ENDED) {
            return (ts_end - ts_start);
        }
        return (System.currentTimeMillis() - ts_start);
    }

    /**
     * Return the timestamp (a long value indicating the standard 'system time')
     * of the beginning of the game. &nbsp;Raise a runtime exception if the game
     * hasn't already been started.
     *
     * @return the 'system time' timestamp of the begin of the game
     */
    public long getStartTimestamp() throws TTombolaRuntimeException {
        if (status == TGameStatus.INITIALIZING || status == TGameStatus.READY) {
            throw new TTombolaRuntimeException(String.format("Tombola Game <%s> has not been started. No number has been extracted jet.", this.id));
        }
        return this.ts_start;
    }

    /**
     * Return the timestamp (a long value indicating the standard 'system time')
     * of the ending moment of the game. &nbsp;Raise a runtime exception if the
     * game hasn't already been completed.
     *
     * @return the 'system time' timestamp of the end of the game
     */
    public long getEndTimestamp() throws TTombolaRuntimeException {
        if (status != TGameStatus.ENDED) {
            throw new TTombolaRuntimeException(String.format("Tombola Game <%s> has not been finished jet. Be patient...", this.id));
        }
        return this.ts_end;
    }

    /**
     * Set the (previously prepared, see {@linkplain TAwardList}) list of awards
     * that will be available during the game.&nbsp;As usual, even this setter
     * method can be invoked only during the INITIALIZING status of the tombola
     * game object.
     *
     * @param awards the list of awards that will be used during the game.
     */
    public void setAwards(TAwardList awards) throws TTombolaRuntimeException {
        if (status != TGameStatus.INITIALIZING) {
            throw new TTombolaRuntimeException("Awards set cannot be changed during the game. It can be set only during the initialization phase.");
        }
        this.awards = awards;
        checkInitialization();
    }

    /**
     * Return the complete list of award objects currently used by this
     * game.&nbsp;It will contain all prizes, both already assigned and still to
     * be appointed.
     *
     * @return the complete list of award objects currently used by the game.
     */
    public TAwardList getAwards() {
        return this.awards;
    }

    /**
     * As the game proceeds, winVector stores the extraction count at which each
     * award category is won.&nbsp;Example: winVector[0][7] will contain 1 if
     * the Ambo has been won at the seventh extraction, etc.&nbsp;It is used for
     * data analysis and statistics purposes.
     *
     * @return A 'data analysis purpose' matrix filled during the game. Each
     * element [award_category-2][i] contains zero if after the i-th extraction
     * no award has been won, 1 if the award has been won (sixth row of the
     * matrix contains 1 for tombola).
     */
    @Deprecated
    public int[][] getWinVector() {
        return this.winVector;
    }

    /**
     * Used to do statistical analysis on tombola games.&nbsp;Return an int matrix containing,
     * at position [i][j] the number of conflicts for prize of value j at i-th extraction.
     *
     * @return an int matrix containing, at position [i][j] the number of
     * conflicts for prize of value j at i-th extraction.
     */
    @Deprecated
    public int[][] getConflictVector() {
        return this.conflictVector;
    }

    /**
     * Return the counter for extracted tombola number during the game. Its
     * value is 0 (zero) if the game is not started jet, a positive value
     * indicating how many numbers have already been extracted from eh ballot
     * box.
     *
     * @return the current count of already extracted numbers from the ballot
     * box.
     */
    public int getExtractionCount() {
        return this.exCount;
    }

    /**
     * Return the last number extracted from the {@link TSacchetto} associated
     * to this game. Basically is an equivalent method for
     * TGame#getSacchetto()#getLastExtracted.
     *
     * @return the last number extracted for this game. 0 if the game hasn't
     * started jet.
     */
    public int getLastExtracted() {
        return this.lastEx;
    }

    /**
     * Return the number of cards that have matched the last number extracted.
     *
     * @return the number of cards that have matched the last number extracted.
     */
    public int getLastMatchingCount() {
        return this.lastMatchingCount;
    }


    /**
     * !!!STILL NOT IMPLEMENTED!!! Allow to add a card late after the game as been
     * already started, recovering all numbers previously extracted.
     * 
     * @param newCard the card to be extracted.
     * @return true is the card has been really added, false otherwise
     */
    public boolean addLateCard(TCard newCard) {
        // TODO(2.0) Still not implemented
        // When a card could be added to an already started game?
        // You will need to check all already extracted numbers, without considering a win for the card
        // You have to compare with available prizes
        // You have to log the event...
        return false;
    }

    /**
     * This method acts just like {@link TGame#extractNumber(int)} but does not
     * require the extracted number as external input, providing automatically
     * to extract a random number that has not already been extracted before.
     *
     * @return a {@linkplain TGameResultCode} indicating the status of the game
     * after the extraction and the next action to perform to properly continue
     * the game.
     *
     * @see TTombolaExamples
     * @see TGameResultCode
     * @see TGameStatus
     */
    public TGameResultCode extractNumber() {
        return extractNumber(0);
    }

    /**
     * You can consider this method as the "prince" of all TGame methods! It
     * executes all the logic and controls to simulate and manage the
     * consequences of the extraction of a number during a tombola game.&nbsp;It
     * requires as an external input the number to be extracted (of course, it
     * must be a valid number that has not been already extracted before) and
     * checks all cards participating to the game against the available awards
     * to verify if...
     * <ol>
     * <li>there is a new award winner (for example, a card that get "TERNO")
     * <li>there two or more cards reaching the same award score, thus
     * "contending it" and requiring a following
     * {@link TGame#resolveCandidates(int)} or
     * {@link TGame#resolveCandidates(int[])} call
     * <li>there is a card winning the last available award, typically a
     * Tombola, thus bringing the gate to its conclusion
     * </ol>
     * All relevant events are properly logged on the {@link ITLogger}
     * object set for the game.<p>
     * Finally, the methods return a {@linkplain TGameResultCode} indicating the
     * "consequences" of this extraction and therefore communicating to client
     * code which is the correct next action to perform and continue
     * appropriately the game.<p>
     * Please, <b>find an example</b> of the typical workflow needed to manage a
     * complete Tombola match within the {@link TTombolaExamples} class.
     *
     * @param number the number just extracted: of course, it must be a valid
     * number that has not been already extracted before.
     *
     * @return a {@linkplain TGameResultCode} indicating the status of the game
     * after the extraction and the next action to perform to properly continue
     * the game.
     *
     * @see TTombolaExamples
     * @see TGameResultCode
     * @see TGameStatus
     */
    public synchronized TGameResultCode extractNumber(int number) {
        if (status == TGameStatus.RESOLVING) {
            // We cannot do nothing, just wait that resolveCandidates() make its job.
            logger.gameLog(ITLogger.TLogLevel.WAR, id, exCount, lastEx,
                    "Attention! New number NOT extracted. You have to solve the contention before continue playing");
            lastResultCode = TGameResultCode.MULTICANDIDATES;
            return lastResultCode;
        }
        if (status == TGameStatus.ACCEPTING) {
            // We need to confirm/deny candidates. No other number can be extracted right now.
            logger.gameLog(ITLogger.TLogLevel.WAR, id, exCount, lastEx,
                    "Attention! New number NOT extracted. You have to accept/deny candidate cards before, assign awards and then extract a new number");
            lastResultCode = TGameResultCode.ACCEPT_OR_DENY;
            return lastResultCode;
        }
        if (status == TGameStatus.ENDED) {
            // Les jeux sont faits, rien ne va plus
            logger.gameLog(ITLogger.TLogLevel.WAR, id, exCount, lastEx,
                    "Attention! Game is OVER! Do not try to extract other numbers... come on...");
            lastResultCode = TGameResultCode.GAME_OVER;
            return lastResultCode;
        }
        if (status != TGameStatus.READY && status != TGameStatus.PLAYING) {        // Wrong status
            lastResultCode = TGameResultCode.NOT_READY;
            return lastResultCode;
        }
        if (number < 0 || number > 90) {                        // Number out of range
            logger.gameLog(ITLogger.TLogLevel.ERR, id, exCount, lastEx,
                    "Attention! Extracted number is out of range, that's impossible");
            lastResultCode = TGameResultCode.WRONG_NUMBER;
            return lastResultCode;
        }

        int extracted;
        if (number == 0) {
            extracted = sacchetto.extract();
        } else {
            extracted = sacchetto.manuallyExtract(number);
        }
        if (extracted == -1) {
            // Number already extracted
            logger.gameLog(ITLogger.TLogLevel.ERR, id, exCount, lastEx,
                    String.format("Number %2d had already been checked before, no action or control performed", number));
            lastResultCode = TGameResultCode.ALREADY_CHECKED;
            return lastResultCode;
        }

        // Now <extracted> contains a good Tombola number to work with
        status = TGameStatus.BUSY;
        lastMatchingCount = 0;

        // Tabellone cards are check apart here to assure they always display the current status
        // of the game, both if they participate to the game (are included in cards properties) or not.
        if (this.tabellone != null) {
            tabellone.forEach((boardCard) -> {
                boardCard.checkExtraction(extracted);
            });
        }
        if (exCount == 0) {
            // This is the first extracted number of the game...
            this.ts_start = System.currentTimeMillis();
            logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                    String.format("Ok guys, let's start the game! The match <%s> is warming up!", this.id));
            logger.gameLog(logger.getLevel(), id, exCount, lastEx, "Game logger level is <" + logger.getLevel() + ">");
            String startMessage = String.format("%d card(s) are ready to play to win %d awards", this.cards.size(), this.getAwards().size());
            if (this.jollyOn) {
                startMessage += ". Jollies are enabled";
            }
            logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx, startMessage);
            int billboardCount = 0;
            for(TCard c: cards) {
                billboardCount += (c instanceof TBillboardCard ? 1 : 0);
            }
            if (billboardCount>0) {
                logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx, "Found " + billboardCount + " Billboard cards participating to the game, so they CAN WIN awards");
            } else {
                logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx, "Billboard cards DO NOT participate to the game and CANNOT WIN awards");
            }
            logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                    "Explicit confirmation of candidate cards to win awards is set to <" + this.confirmCandidateOn + ">");

            // TODO(2.0) funFactsEvaluation...
            // if (funFactsEval != null) {
            //   logger.verbose(gameLogEntry(String.format("Fantastic, fun figures crowler <<%s>> has been activated.", "MISSING")));
            // }
        }

        exCount++;
        lastEx = extracted;
        logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                String.format("Number <<%2d>> has been extracted", extracted));

        // Step 0: All lists of cards candidate to be appointed an award are cleaned
        awards.forEach(aw -> aw.getCandidatesList().clear());
        awards.forEach(aw -> aw.getValidatingList().clear());
        // Step 1: Look for card(s) to candidate to an available award
        boolean candidateFlag = false;
        int firstAvailableValue = awards.getFirstAvailableAward().getCategory();
        for (TCard c : cards) {
            int result = c.checkExtraction(extracted);
            lastMatchingCount += (result > 0 ? 1 : 0);
            if (result >= firstAvailableValue) {
                // On the c card has been checked the number just extracted.
                // Let's check if there is an award to grab
                for (TAward aw : awards.getAvailableAwards()) {
                    if (aw.getCategory() == result || aw.getCategory() == (result - 5) || aw.getCategory() == (result - 10)) {
                        // c card is a candidate to win the aw award.
                        candidateFlag = true;
                        if (this.confirmCandidateOn) {
                            aw.setStatus(TAward.TAwardStatus.VALIDATING);
                            aw.getValidatingList().add(c);
                        } else {
                            aw.setStatus(TAward.TAwardStatus.CONTENDED);
                            aw.getCandidatesList().add(c);
                        }

                        String owner = c.getOwner();
                        if (owner != null && owner.length() > 0) {
                            owner = ", owned by " + owner + ",";
                        } else {
                            owner = "";
                        }
                        String sConfirmation = "";
                        if (this.confirmCandidateOn) {
                            sConfirmation = ", waiting for explicit acceptance or deny";
                        }
                        logger.gameLog(ITLogger.TLogLevel.CAN, id, exCount, lastEx,
                                String.format("Card <<%s>>%s is candidate to the award <<%s>>%s", c.getLabel(), owner, aw.getLabel(), sConfirmation));
                        // !!! Very important "break" instruction: so this card cannot participate to other award check whitin this same extraction.
                        break;
                    }
                }
            }
        }
        logger.gameLog(ITLogger.TLogLevel.VER, id, exCount, lastEx,
                String.format("%d cards out of %d have matched the extracted number.", lastMatchingCount, cards.size()));

        // If explicit candidates confirmation is set to true, award are not automatically assigned
        //  and explicit confirmation/deny of cards is required.
        if (candidateFlag && this.confirmCandidateOn) {
            this.status = TGameStatus.ACCEPTING;
            this.lastResultCode = TGameResultCode.ACCEPT_OR_DENY;
            return this.lastResultCode;
        }
        return assign();
    }   // End of ExtractNumber

    /**
     * When {@link TGame#setConfirmCandidateOn(boolean)} is set to true, all cards that
     * are candidate to be appointed an award after a number extraction require explicit
     * (manual) confirmation or deny.&nbsp;This method allows the TombolaLib client user
     * to confirm that the card passed as input parameter can contend the award (or directly 
     * win it, if it is the lone candidate card).
     *
     * @param card the candidate card to be confirmed
     * 
     * @return 1 if the card has been confirmed, 0 otherwise (the card passed as input parameter
     * is not in the current award 'wait for confirmation' list)
     * 
     * @see TGame#setConfirmCandidateOn(boolean)
     * @see TGame#assign()
     */
    public int confirmCandidate(TCard card) {
        int result = 0;
        if (status != TGameStatus.ACCEPTING) {
            return result;
        }
        for (TAward aw : this.getAwards().getValidatingAwards()) {
            result += (aw.accept(card) ? 1 : 0);
            String owner = card.getOwner();
            if (owner != null && owner.length() > 0) {
                owner = ", owned by " + owner + ",";
            } else {
                owner = "";
            }
            String logRecord = "Card <<" + card.getLabel() + ">>" + owner + " has been accepted for the award <<" + aw.getLabel() + ">>";
            logger.gameLog(ITLogger.TLogLevel.CAN, id, exCount, lastEx, logRecord);
        }
        if (this.getAwards().getValidatingAwards().isEmpty()) {
            status = TGameStatus.BUSY;
        }
        return result;
    }

    /**
     * Allows to confirm more than a card simultaneously.&nbsp;See
     * {@link TGame#confirmCandidate(TCard)} for further details.
     *
     * @param cards the candidate cards to be confirmed
     * 
     * @return the number of cards that have been effectively confirmed (cards passed 
     * as input parameter must be in the current award 'wait for confirmation' list)
     * 
     * @see TGame#confirmCandidate(TCard) 
     * @see TGame#setConfirmCandidateOn(boolean)
     * @see TGame#assign()
     */
    public int confirmCandidate(List<TCard> cards) {
        int result = 0;
        if (status != TGameStatus.ACCEPTING) {
            return result;
        }
        for (TCard card : cards) {
            for (TAward aw : this.getAwards().getValidatingAwards()) {
                result += (aw.accept(card) ? 1 : 0);
                String owner = card.getOwner();
                if (owner != null && owner.length() > 0) {
                    owner = ", owned by " + owner + ",";
                } else {
                    owner = "";
                }
                String logRecord = "Card <<" + card.getLabel() + ">>" + owner + " has been accepted for the award <<" + aw.getLabel() + ">>";
                logger.gameLog(ITLogger.TLogLevel.CAN, id, exCount, lastEx, logRecord);
            }
        }
        if (this.getAwards().getValidatingAwards().isEmpty()) {
            status = TGameStatus.BUSY;
        }
        return result;
    }

    /**
     * Delete the card passed as input parameter from the 'waiting for confirmation' list
     * of an award, so preventing the card could win the award.
     * 
     * @param card The card to be denied
     * 
     * @return 1 if the card has been effectively denied, 0 otherwise (the card passed as input 
     * parameter is not in the current award 'wait for confirmation' list)
     * 
     * @see TGame#confirmCandidate(TCard) 
     * @see TGame#setConfirmCandidateOn(boolean)
     * @see TGame#assign()
     */
    public int denyCandidate(TCard card) {
        int result = 0;
        if (status != TGameStatus.ACCEPTING) {
            return result;
        }
        for (TAward aw : this.getAwards().getValidatingAwards()) {
            result += (aw.deny(card) ? 1 : 0);
            String owner = card.getOwner();
            if (owner != null && owner.length() > 0) {
                owner = ", owned by " + owner + ",";
            } else {
                owner = "";
            }
            String logRecord = "Card <<" + card.getLabel() + ">>" + owner + " has been denied for the award <<" + aw.getLabel() + ">>";
            logger.gameLog(ITLogger.TLogLevel.CAN, id, exCount, lastEx, logRecord);
        }
        if (this.getAwards().getValidatingAwards().isEmpty()) {
            status = TGameStatus.BUSY;
        }
        return result;
    }

    /**
     * Allows to deny more than a card simultaneously.&nbsp;See
     * {@link TGame#denyCandidate(TCard)} for further details.
     *
     * @param cards the candidate cards to be denied
     * 
     * @return the number of cards that have been effectively denied (cards passed 
     * as input parameter must be in the current award 'wait for confirmation' list)
     * 
     * @see TGame#confirmCandidate(TCard) 
     * @see TGame#denyCandidate(TCard) 
     * @see TGame#setConfirmCandidateOn(boolean)
     * @see TGame#assign()
     */
    public int denyCandidate(List<TCard> cards) {
        int result = 0;
        if (status != TGameStatus.ACCEPTING) {
            return result;
        }
        for (TCard card : cards) {
            for (TAward aw : this.getAwards().getValidatingAwards()) {
                result += (aw.deny(card) ? 1 : 0);
                String owner = card.getOwner();
                if (owner != null && owner.length() > 0) {
                    owner = ", owned by " + owner + ",";
                } else {
                    owner = "";
                }
                String logRecord = "Card <<" + card.getLabel() + ">>" + owner + " has been denied for the award <<" + aw.getLabel() + ">>";
                logger.gameLog(ITLogger.TLogLevel.CAN, id, exCount, lastEx, logRecord);
            }
        }
        if (this.getAwards().getValidatingAwards().isEmpty()) {
            status = TGameStatus.BUSY;
            logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                    "Accepting phase has been completed.");
        }
        return result;
    }

    /**
     * After each number extraction, if {@link TGame#setConfirmCandidateOn(boolean)}
     * is set to true, you need to call this method to assign award to candidate cards.
     *      *
     * @return a {@linkplain TGameResultCode} indicating the status of the game
     * as well as the next action to perform to properly continue the game.
     */
    public TGameResultCode assign() {
        if (this.status != TGameStatus.BUSY) {
            logger.gameLog(ITLogger.TLogLevel.WAR, id, exCount, lastEx, "You should not proceed to assign award jet. Complete cards accept/deny phase before.");
            return this.lastResultCode;
        }
        boolean thereIsLoneWinner = false;
        boolean thereAreContenders = false;

        // ----------------------------
        // Step 2: Assign award where there is JUST ONE candidate card.
        for (TAward aw : awards) {
            if (aw.isContended() && aw.getCandidatesList().size() == 1) {
                // Award aw is contented and there is just one candidate card.
                TCard winner = aw.getCandidatesList().get(0);
                String owner = winner.getOwner();
                if (owner != null && owner.length() > 0) {
                    owner = ", owned by " + owner + ",";
                } else {
                    owner = "";
                }
                String logRecord = "Card <<" + winner.getLabel() + ">>" + owner + " won the award <<" + aw.getLabel() + ">>";
                if (winner.hasLastScoreUsedJolly()) {
                    logRecord += " using Jolly";
                }
                logger.gameLog(ITLogger.TLogLevel.WIN, id, exCount, lastEx, logRecord);
                aw.setStatus(TAward.TAwardStatus.ASSIGNED);
                int index;
                if (aw.getCategory() < 6) {
                    index = aw.getCategory() - 2;
                } else {
                    index = 4;
                }
                winVector[index][exCount - 1]++;
                winVector[5][exCount - 1]++;
                thereIsLoneWinner = true;
                aw.getWinnerList().add(winner);
                aw.getWithJollyList().add(winner.hasLastScoreUsedJolly());
                aw.setWinningNumber(lastEx);
                aw.setWinningOrdinal(exCount);

                // TODO(2.0) Some considerations...
                // What about to register whitin the card the info that it has won?
                // What about to store somewhere the card that has won, the extraction count, the number that let it to win?
            }
        }

        // At this point, there are no awards in contended status whit just one candidate.
        // Step 3: Look for multiple candidate for the same award and manage/resolve conflicts
        for (TAward aw : awards) {
            int numCandidates = aw.getCandidatesList().size();
            if (aw.getStatus() == TAward.TAwardStatus.CONTENDED && numCandidates > 1) {
                //concurrentNominations += numCandidati;
                int index;
                if (aw.getCategory() < 6) {
                    index = aw.getCategory() - 2;
                } else {
                    index = 4;
                }
                conflictVector[index][exCount - 1] += (numCandidates - 1);
                conflictVector[5][exCount - 1] += (numCandidates - 1);
                logger.gameLog(ITLogger.TLogLevel.CAN, id, exCount, lastEx,
                        String.format("Award <<%s>> has %d candidates. Resolution is needed before drawing next number", aw.getLabel(), numCandidates));
                thereAreContenders = true;
            }
        }
        if (thereAreContenders) {
            this.status = TGameStatus.RESOLVING;
            this.lastResultCode = TGameResultCode.MULTICANDIDATES;
            return this.lastResultCode;
        }

        if (this.isGameOver()) {
            this.status = TGameStatus.ENDED;
            this.ts_end = System.currentTimeMillis();
            logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                    String.format("Congratulations my dear, Tombola Game <<%s>> is over after %s. Thanks a lot and see you again", id, TUtils.prettyMilliseconds(this.getElapsedTime())));
            this.lastResultCode = TGameResultCode.GAME_OVER;
            return this.lastResultCode;
        }

        this.status = TGameStatus.PLAYING;
        // This status change is here because it is executed only when the extraction cycle can be considered closed.
        // If there are more than one contenders the flow will not arrive here and a call to resolveCandidates() will be necessary.
        // Change of status to PLAYING therefore will be made within the resolveCandidates() process.

        if (thereIsLoneWinner) {
            this.lastResultCode = TGameResultCode.WINNER;
            return this.lastResultCode;
        }

        this.lastResultCode = TGameResultCode.NOWINNER;
        return this.lastResultCode;
        // ----------------------------
    }

    /**
     * When, during the tombola game, an award is contended by more than one
     * card, that is, the game is paused in status "RESOLVING", this method
     * returns the first award object within the ordered award list that have
     * many contending cards.
     *
     * @return the first award object having more than one card contenting for
     * it.
     *
     * @see TTombolaExamples
     */
    public TAward getFirstAwardToResolve() {
        TAward result = null;
        if (this.status != TGameStatus.RESOLVING) {
            return result;
        }
        for (TAward tempAw : awards) {
            int numCandidates = tempAw.getCandidatesList().size();
            if (tempAw.isContended() && numCandidates > 1) {
                result = tempAw;
                break;
            }
        }
        return result;
    }

    /**
     * This method is a syntactic sugar method and acts just like
     * {@link TGame#resolveCandidates(int[])}, but allows to specify just a
     * single, unique winner card for the first award that is in "CONTENDING"
     * status.&nbsp;See the cited companion method for further details.
     *
     * @param winnerPos the index (i.e. the position) of the card you want to be
     * the unique winner of the first award in "CONTENDED" status (see also
     * {@linkplain TGame#getFirstAwardToResolve()}).
     *
     * @return the status of the game object after the contention has been
     * resolved, just like the status returned by a
     * {@linkplain TGame#extractNumber(int)} call. It is also possible that,
     * after the resolution of a contention, the game still remains in
     * "RESOLVING" status just because there are one or more loser that have
     * been shifted to the next award (i.e. a "SECOND TERNO") and another call
     * to resolveCandidates(...) is needed.
     *
     * @see TGame#resolveCandidates(int[])
     * @see TTombolaExamples
     */
    public TGameResultCode resolveCandidates(int winnerPos) {
        return resolveCandidates(new int[]{winnerPos});
    }

    /**
     * When, after the extraction of a number, more than one card reach the
     * score to be appointed to an available award, the game "pauses", enters
     * the "RESOLVING" status and requires that someone, <i>from the
     * external</i>, make a choice and resolves the conflicts.&nbsp;This method
     * helps to manage this very common situation, letting the user to adopt
     * whatever criteria she/he wants to take the choice and thus communicating
     * it to the TGame object by means of this method.
     *
     * @param winnerIndexes a int[] vector containing the index/indexes (i.e.
     * the positions) of the card/cards you want to be the unique winner or "ex
     * equo" winners of the first award in "CONTENDED" status (see also
     * {@linkplain TGame#getFirstAwardToResolve()}).
     *
     * @return the status of the game object after the contention has been
     * resolved, just like the status returned by a
     * {@linkplain TGame#extractNumber(int)} call. It is also possible that,
     * after the resolution of a contention, the game still remains in
     * "RESOLVING" status just because there are one or more loser that have
     * been shifted to the next award (i.e. a "SECOND TERNO") and another call
     * to resolveCandidates(...) is needed.
     *
     */
    public TGameResultCode resolveCandidates(int[] winnerIndexes) {
        TGameResultCode resolveResult = TGameResultCode.WINNER;
        if (this.status != TGameStatus.RESOLVING) {
            logger.gameLog(ITLogger.TLogLevel.ERR, id, exCount, lastEx,
                    String.format("On game <<%s>> someone is trying to resolve an award contention that does not exist!", id));
            this.lastResultCode = TGameResultCode.NOT_RESOLVING;
            return this.lastResultCode;
        }

        TAward aw = this.getFirstAwardToResolve();
        int numCandidati = aw.getCandidatesList().size();
        if (aw.getStatus() == TAward.TAwardStatus.CONTENDED && numCandidati >= winnerIndexes.length) {
            int winIdx = 0;
            for (int j : winnerIndexes) {
                aw.getWinnerList().add(aw.getCandidatesList().get(j));
                aw.getWithJollyList().add(aw.getCandidatesList().get(j).hasLastScoreUsedJolly());
                aw.getCandidatesList().remove(j);
                logger.gameLog(ITLogger.TLogLevel.CAN, id, exCount, lastEx,
                        String.format("User has resolved in favor of card <<%s>>", aw.getWinnerList().get(winIdx).getLabel()));

                String owner = aw.getWinnerList().get(winIdx).getOwner();
                if (owner != null && owner.length() > 0) {
                    owner = ", owned by " + owner + ",";
                } else {
                    owner = "";
                }
                String logRecord = "Award <<" + aw.getLabel() + ">>" + owner + " has been won by card <<" + aw.getWinnerList().get(winIdx).getLabel() + ">> ";
                if (aw.getWinnerList().get(winIdx).hasLastScoreUsedJolly()) {
                    logRecord += "Using Jolly ";
                }
                logger.gameLog(ITLogger.TLogLevel.WIN, id, exCount, lastEx, logRecord);
                winIdx++;
            }
            aw.setStatus(TAward.TAwardStatus.ASSIGNED);
            aw.setWinningNumber(lastEx);
            aw.setWinningOrdinal(exCount);

            int index = 0;
            if (aw.getCategory() < 6) {
                index = aw.getCategory() - 2;
            } else {
                index = 4;
            }
            winVector[index][exCount - 1]++;
            winVector[5][exCount - 1]++;

            resolveResult = TGameResultCode.WINNER;
            int i = awards.indexOf(aw);

            if (i < (awards.size() - 1) && awards.get(i + 1).getCategory() == aw.getCategory()) {
                // There is another following award with the same value, so the remaining candidate list
                // must be passed to it.
                // If the candidate is just one, it wins, otherwise the game status remains "RESOLVING",
                // the method call ends and another call to resolveCandidates() is going to be required
                // to properly continue the game.
                if (aw.getCandidatesList().size() > 1) {
                    awards.get(i + 1).getCandidatesList().addAll(aw.getCandidatesList());
                    awards.get(i + 1).setStatus(TAward.TAwardStatus.CONTENDED);
                    for (TCard c : awards.get(i + 1).getCandidatesList()) {
                        logger.gameLog(ITLogger.TLogLevel.CAN, id, exCount, lastEx,
                                String.format("Card <<%s>> has been moved as candidate to the award <<%s>>", c.getLabel(), awards.get(i + 1).getLabel()));
                    }
                    resolveResult = TGameResultCode.MULTICANDIDATES;
                } else {
                    // Here we can be supposed that candidate list contains just one card
                    awards.get(i + 1).getWinnerList().add(aw.getCandidatesList().get(0));
                    awards.get(i + 1).getWithJollyList().add(aw.getCandidatesList().get(0).hasLastScoreUsedJolly());
                    awards.get(i + 1).setStatus(TAward.TAwardStatus.ASSIGNED);
                    awards.get(i + 1).setWinningNumber(lastEx);
                    awards.get(i + 1).setWinningOrdinal(exCount);

                    index = 0;
                    if (aw.getCategory() < 6) {
                        index = aw.getCategory() - 2;
                    } else {
                        index = 4;
                    }
                    winVector[index][exCount - 1]++;
                    winVector[5][exCount - 1]++;

                    // Maybe this line can be deleted.
                    resolveResult = TGameResultCode.WINNER;
                    String owner = awards.get(i + 1).getWinnerList().get(0).getOwner();
                    if (owner != null && owner.length() > 0) {
                        owner = ", owned by " + owner + ",";
                    } else {
                        owner = "";
                    }
                    String logRecord2 = "Award <<" + awards.get(i + 1).getLabel() + ">> has been won by card <<" + awards.get(i + 1).getWinnerList().get(0).getLabel() + ">>" + owner + " ";
                    if (awards.get(i + 1).getWinnerList().get(0).hasLastScoreUsedJolly()) {
                        logRecord2 += "Using Jolly ";
                    }
                    logger.gameLog(ITLogger.TLogLevel.WIN, id, exCount, lastEx, logRecord2);
                }
            }
            aw.getCandidatesList().clear();
        } else {
            logger.gameLog(ITLogger.TLogLevel.ERR, id, exCount, lastEx, "Too many winners proposed to this award!");
        }

        if (resolveResult == TGameResultCode.WINNER) {
            status = TGameStatus.PLAYING;
            // Here the number extraction can be considered concluded. If there are still other candidates
            // the flow will never arrive here and another call to resolveCadidates() is required.
        }

        if (this.isGameOver()) {
            this.status = TGameStatus.ENDED;
            this.ts_end = System.currentTimeMillis();
            logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                    String.format("Congratulation my dears, Tombola Game <%s> is Over after %s. Thanks you and see you again.", id, TUtils.prettyMilliseconds(this.getElapsedTime())));
            this.lastResultCode = TGameResultCode.GAME_OVER;
            return this.lastResultCode;
        }
        this.lastResultCode = resolveResult;
        return this.lastResultCode;
    }

    /**
     * This method, as you can imagine, allows the rollback of the last
     * extracted number during a tombola game.&nbsp;Perhaps it requires to be
     * checked, tested and fixed a bit...
     *
     * @return WRONG_NUMBER TGameResultCode value if the call to rollback() is
     * invalid for some reason (no number has been already extracted, or the
     * game is already ended or not in a proper status), NOWINNER is the
     * rollback process "seems" to succeed.
     */
    public TGameResultCode rollback() {
        // Step 0: Correct status checking
        if (this.exCount < 1 || this.lastEx < 1 || this.lastEx > 90) {
            this.lastResultCode = TGameResultCode.WRONG_NUMBER;
            return this.lastResultCode;
        }
        if (this.getStatus() != TGameStatus.ENDED || this.getStatus() != TGameStatus.PLAYING || this.getStatus() != TGameStatus.RESOLVING) {
            this.lastResultCode = TGameResultCode.WRONG_NUMBER;
            return this.lastResultCode;
        }
        logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                String.format("Oh my God, that's a twist! We're going to rollback the last extraction. Number %d is going back into the ballot box.", lastEx));
        // Step 1: Roolback the last number from all playing cards
        int cardCount = 0;
        for (TCard c : cards) {
            int result = c.uncheckExtraction(lastEx);
            if (result == -2) {
                logger.gameLog(ITLogger.TLogLevel.WAR, id, exCount, lastEx,
                        String.format("It seemes that on card <<%s>> owned by <<%s>> the number %d is present but not already marked. It was a mistake?", c.getLabel(), c.getOwner(), lastEx));
            } else if (result >= 0) {
                logger.gameLog(ITLogger.TLogLevel.VER, id, exCount, lastEx,
                        String.format("Last extracted number, %d, has been rolled back on card <<%s>> owned by <<%s>>", lastEx, c.getLabel(), c.getOwner()));
                cardCount++;
            }
        }
        logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                String.format("Number %d has been unmarked on %d cards out of %d total playing cards", lastEx, cardCount, cards.size()));

        // Step 2: Eventually Undo assiged / deciding awards with the last number
        for (TAward aw : awards) {
            if (aw.getWinningOrdinal() == exCount && aw.getWinningNumber() == lastEx) {
                // It must be rolled back
                if (aw.isAssigned()) {
                    aw.setStatus(TAward.TAwardStatus.AVAILABLE);
                    aw.setWinningNumber(-1);
                    aw.setWinningOrdinal(-1);
                    for (TCard w : aw.getWinnerList()) {
                        logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                                String.format("Award <<%s>> is available again. Sorry card <<%s>> owned by <<%s>>, you have to give it back now.", aw.getLabel(), w.getLabel(), w.getOwner()));
                    }
                    aw.getWinnerList().clear();
                }
                if (aw.isContended()) {
                    aw.setStatus(TAward.TAwardStatus.AVAILABLE);
                    aw.setWinningNumber(-1);
                    aw.setWinningOrdinal(-1);
                    for (TCard c : aw.getCandidatesList()) {
                        logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                                String.format("Award <<%s>> is no more contended by card <<%s>> owned by <<%s>>", aw.getLabel(), c.getLabel(), c.getOwner()));
                    }
                    aw.getCandidatesList().clear();
                }
            }
        }

        // Step 3: Finally re-put the extracted number in the sacchetto again
        // and unmark also from the tabbellone.
        if (this.tabellone != null) {
            tabellone.forEach((boardCard) -> {
                boardCard.uncheckExtraction(lastEx);
            });
        }
        sacchetto.rollBack();
        int temp = lastEx;
        lastEx = sacchetto.getLastExtracted();
        logger.gameLog(ITLogger.TLogLevel.INF, id, exCount, lastEx,
                String.format("Now everithing has been restored as %d has never been extracted, I hope!", temp));
        // TODO(2.0) Verify the return codes. Maybe it is preferable to use new and ad hoc TGame statuses...
        this.lastResultCode = TGameResultCode.NOWINNER;
        return this.lastResultCode;
    }

    /**
     * Return the resultCode reached by the last call to {@link TGame#extractNumber()}, {@link TGame#extractNumber(int)},
     * {@link TGame#resolveCandidates(int)}, {@link TGame#resolveCandidates(int[])}
     * or {@link TGame#rollback()} methods.
     *
     * @return the last resultCode reached by one of the Tombola Game actions
     * methods.
     */
    public TGameResultCode getLastResultCode() {
        return this.lastResultCode;
    }

    // ---------------------------------------------------------------------------------

    private void checkInitialization() {
        if (status == TGameStatus.INITIALIZING) {
            if (sacchetto != null && logger != null
                    && cards != null && awards != null) {
                status = TGameStatus.READY;
                logger.gameLog(ITLogger.TLogLevel.VER, id, exCount, lastEx,
                        String.format("PreCHECK: It seems everything is ready to go! (%d numbers in the sacchetto, %d awards to win, %d cards warming up)",
                                90 - sacchetto.getExtractedCount(), awards.size(), cards.size()));
            }
        }
    }

    private boolean isGameOver() {
        // A game is ended when there are no more awards to win
        return awards.stream().noneMatch((aw) -> (!aw.isAssigned()));
    }
}           // End Of File - Rel.(1.1)
