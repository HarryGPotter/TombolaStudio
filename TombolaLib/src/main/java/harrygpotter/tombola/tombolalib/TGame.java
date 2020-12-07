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

/**
 * TombolaGame class is a funny piece of code, where a Tombola game match can be
 * simulated, monitored, controlled and analyzed.&nbsp;I like it very much! It
 * is another piece of magic for me!
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TAward
 * @see TAwardList
 * @since 1.8
 */
public class TGame implements Serializable {

    private String gameID;
    private TSacchetto sacchetto;
    private TCardList tabellone;
    private TGameStatus status = TGameStatus.INITIALIZING;
    private TGameResultCode lastResultCode;
    private TCardList gamingCards;
    private ILogger logger;
    private TAwardList awards;
    private boolean jollyOn = true;         // TODO(2.0) Still not used.
    private boolean gamingBillboard = false;
    private int exCount = 0;
    // TODO(2.0) Still not used. private int resolveCount = 0;           
    // TODO(2.0) Still not used. private int concurCandidatesCount = 0;  
    private int lastEx = 0;
    private int lastMatchsXExtraction = 0;
    private long ts_start, ts_end;
    
    // TODO(1.1) Describe winVector[i][j]    
    private final int[][] winVector = new int[6][90];
    
    // TODO(1.1) Describe conflictvector[i][j]
    // conflictVector[i] is 0 if there are no conflicts, otherwise the amount of candidates for the same award resulting at the i-th extraction
    private final int[][] conflictVector = new int[6][90];
    
    // just like winVector above, but only considering winners involving the jolly numbers
    // TODO(2.0) Still not used!!!
    private final int[][] usingJolly = new int[6][90];
    // private int fastTombolaBar = 90;         // TODO(2.0) Still not used.
    // private TFunFacts funFactsEval = null;   // TODO(2.0) Still not used.

    /**
     * Unique constructor for TGame object.&nbsp;All you need to specify is a
     * name that will help to identify this specific game.&nbsp;A brand new
     * TGame object is in 'Initializing' state and all <i>components</i> needed
     * to start a game MUST be created apart and specified using respective
     * setter methods: a sacchetto, a list of gaming cards, a tabellone, a
     * logger.
     *
     * @param gameID a name to identify this specific tombola match.
     */
    public TGame(String gameID) {
        if (gameID == null || gameID.length() < 1) {
            throw new IllegalArgumentException("<ERROR> Each Tombola Game must be identifiable using a not null and not empty name");
        }
        this.gameID = gameID;
        for (int i=0; i<6; i++){
            Arrays.fill(winVector[i], 0);
            Arrays.fill(conflictVector[i], 0);
            Arrays.fill(usingJolly[i], 0);
        }
    }

    /**
     * Return the name that identify this specific tombola game object.
     *
     * @return the name that identify this specific tombola game object.
     */
    public String getGameID() {
        return this.gameID;
    }

    /**
     * Return the current status of this tombola object (see
     * {@linkplain TGameStatus})
     *
     * @return the current status of this tombola object (see
     * {@linkplain TGameStatus})
     */
    public TGameStatus getStatus() {
        return this.status;
    }

    /**
     * Set the <i>sacchetto</i> object (that is, the ballot box) to be used
     * during the tombola game.&nbsp; This method must be invoked only when the
     * TGame object is in 'Initializing' status, that is before a match has been
     * started (the first number has been extracted).&nbsp;Besides, the
     * sacchetto object must be fresh new and contains all 90 numbers. When the
     * sacchetto is set, the 'Tabellone' object for this game is also created or
     * re-initialized.
     *
     * @param sacchetto A brand new sacchetto, still containing all 90 numbers
     * to play.
     * @see TSacchetto
     */
    public void setSacchetto(TSacchetto sacchetto) {
        if (status != TGameStatus.INITIALIZING) {
            throw new TTombolaRuntimeException("Ballot box to extract numbers can be set only during the initialization phase.");
        } else if (sacchetto.getExtractedCount() > 0) {
            throw new TTombolaRuntimeException("Ballot box to extract numbers must be fresh new and contains all 90 numbers to be used in this game!");
        }
        this.sacchetto = sacchetto;
        if (this.tabellone == null) {
            this.tabellone = TBillboardCard.getWholeBillboard(gameID + "-BillBoard-");
        } else {
            tabellone.forEach((boardCard) -> {
                boardCard.resetGameStatus();
            });
        }
        this.checkInitialization();
    }

    /**
     * Return the <i>sacchetto</i> object (the ballot box) associated to this
     * game.
     *
     * @return the <i>sacchetto</i> object (the ballot box) associated to this
     * game.
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
        // Tabellone do not need to be manually set. It is created new when setting the sacchetto.
        return tabellone;
    }

    /**
     * Set the logger object that will be used during the game to spool all
     * relevant messages regarding the status of the match. It MUSt be set
     * during the initialization phase of the game and cannot be changed when
     * the match is already started.&nbsp;See {@linkplain ILogger} and
     * {@linkplain TSimpleLogger} to check all possibilities you have to catch
     * tombola game messages or to define your custom logger to spool messages
     * in the way you prefer.
     *
     * @param logger the logger object that will be used during the game to
     * spool all relevant game messages
     */
    public void setLogger(ILogger logger) {
        if (status != TGameStatus.INITIALIZING) {
            throw new TTombolaRuntimeException("Logger cannot be changed during the game. It can be set only during the initialization phase.");
        }
        this.logger = logger;
        this.checkInitialization();
    }

    /**
     * Return the ILogger interface implementing object associated to this game.
     *
     * @return the ILogger interface implementing object associated to this
     * game.
     */
    public ILogger getLogger() {
        return this.logger;
    }

    /**
     * Set the flag indicating is jolly numbers configured on cards must be
     * considered when playing the game.
     *
     * @param useJolly true to let this TGame object consider the jolly numbers
     * on card, false to ignore them (that is considering them as any other
     * number on cards)
     */
    public void setJollyOn(boolean useJolly) {
        if (status != TGameStatus.INITIALIZING) {
            throw new TTombolaRuntimeException("Use of Jolly numbers cannot be changed during the game. It can be set only during the initialization phase.");
        }
        this.jollyOn = useJolly;
    }

    /**
     * Return true is jolly numbers are considered 'special' and highlighted in
     * log messages during the game, false is they are ignored, that is
     * considered as any other normal number on cards.
     *
     * @return is jolly numbers are considered 'special' or not.
     */
    public boolean getJollyOn() {
        return this.jollyOn;
    }

    /**
     * Set the list containing all the cards that will be used during the
     * tombola game. &nbsp;TCardList objects can be prepared starting from
     * {@linkplain TSeriesList} objects, that are usually used to store and load
     * series of cards to and from files.
     *
     * @param cardList the list containing all the cards that will be used
     * during the tombola game.
     */
    public void setGamingCards(TCardList cardList) {
        if (status != TGameStatus.INITIALIZING) {
            throw new TTombolaRuntimeException("Gaming cards set cannot be changed during the game. It can be set only during the initialization phase.");
        }
        this.gamingCards = cardList;
        if (this.gamingCards != null) {
            this.gamingCards.resetGameStatus();
        }
        this.checkInitialization();
    }

    /**
     * Return the card list object containing all the cards participating to the
     * game.
     *
     * @return the card list object containing all the cards participating to
     * the game
     */
    public TCardList getGamingCards() {
        return this.gamingCards;
    }

    public void letBillBoardPlay() {
        // TODO(2.0)!!!
    }

    /**
     * Return the elapsed time, in milliseconds, since the beginning of this
     * tombola game.&nbsp; If the game hasn't already been started, this method
     * raise a runtime exception.&nbsp;If the game has already finished, this
     * method return the duration of the game.
     *
     * @return the elapsed time, in milliseconds, since the beginning of this
     * tombola game.
     *
     * @see TUtils#prettyMilliseconds(long)
     */
    public long getElapsedTime() {
        if (status == TGameStatus.INITIALIZING || status == TGameStatus.READY) {
            throw new TTombolaRuntimeException(String.format("Tombola Game <%s> has not been started. No number has been extracted jet.", this.gameID));
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
    public long getGameStartTS() {
        if (status == TGameStatus.INITIALIZING || status == TGameStatus.READY) {
            throw new TTombolaRuntimeException(String.format("Tombola Game <%s> has not been started. No number has been extracted jet.", this.gameID));
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
    public long getGameEndTS() {
        if (status != TGameStatus.ENDED) {
            throw new TTombolaRuntimeException(String.format("Tombola Game <%s> has not been finished jet. Be patient...", this.gameID));
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
    public void setAwardList(TAwardList awards) {
        if (status != TGameStatus.INITIALIZING) {
            throw new TTombolaRuntimeException("Awards set cannot be changed during the game. It can be set only during the initialization phase.");
        }
        this.awards = awards;
        checkInitialization();
    }

    /**
     * Return the complete list of award objects currently used by this game. It
     * will contain all prizes, both already assigned and still to be appointed
     * ones.
     *
     * @return the complete list of award objects currently used by the game.
     */
    public TAwardList getAwardList() {
        return this.awards;
    }

    // TODO(2.0) Check if this is used or not.
    public int[][] getWinVector() {
        return this.winVector;
    }
    
    /**
     * Used to do statistical analysis on tombola matches.
     * TODO(2.0) Update comments.
     * 
     * @return an int matrix containing, at position [i][j] the number of conflicts
     * for prize of value j at i-th extraction.
     */
    public int[][] getConflictVector() {
    // TODO(2.0) Check if this is used or not.
        return this.conflictVector;
    }

    // TODO(2.0) Check if this is used or not.
    public int[][] getJollyUsageVector() {
        return this.usingJolly;
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
     * Return the number of cards that have matched the last number extracted.
     *
     * @return the number of cards that have matched the last number extracted.
     */
    public int getLastMatchsXExtraction() {
        return lastMatchsXExtraction;
    }

    // TODO(2.0) Check if this is used or not.
    //    public void addFunFactsEvaluator(TFunFacts factsEvaluator) {
    //        this.funFactsEval = factsEvaluator;
    //        funFactsEval.setCurrentGame(this);
    //    }

    // TODO(2.0)
    public boolean addCardToTheGame(TCard newCard) {
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
     * All relevant events are properly logged on the {@linkplain ILogger}
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
            logger.warning(gameLogEntry("Attention! New number NOT extracted. You have to solve the contention before continue playing."));
            lastResultCode = TGameResultCode.MULTICANDIDATES;
            return lastResultCode;
        }        
        if (status == TGameStatus.ENDED) {
            // Les jeux sont faits, rien ne va plus
            logger.warning(gameLogEntry("Attention! Game is OVER! Do not try to extract other numbers... come on..."));
            lastResultCode = TGameResultCode.GAME_OVER;
            return lastResultCode;
        }        
        if (status != TGameStatus.READY && status != TGameStatus.PLAYING) {        // Wrong status
            lastResultCode = TGameResultCode.NOT_READY;
            return lastResultCode;
        }
        if (number < 0 || number > 90) {                        // Number out of range
            logger.error(gameLogEntry("Attention! Extracted number is out of range, that's impossible."));
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
            logger.error(gameLogEntry("Number, " + number + " has already been checked before, no action or control performed."));
            lastResultCode = TGameResultCode.ALREADY_CHECKED;                      // Number already extracted
            return lastResultCode;
        }
        // Now <extracted> contains a good Tombola number to work with
        boolean thereIsLoneWinner = false;
        boolean thereAreContenders = false;
        status = TGameStatus.BUSY;
        lastMatchsXExtraction = 0;

        // If the six billboard cards ARE NOT included in the gamingCard grount becouse they are no considered as gaming cards,
        // they need to be checked a part here.
        if (!this.gamingBillboard) {
            tabellone.forEach((boardCard) -> {
                boardCard.checkExtraction(extracted);
            });
        }
        if (exCount == 0) {
            // This is the first extracted number of the game...
            this.ts_start = System.currentTimeMillis();
            logger.info(gameLogEntry("--------------------------------------------------"));
            logger.info(gameLogEntry(String.format("Ok guys, let's start the game! The match <%s> is warming up!", this.gameID)));
            if (gamingBillboard) {
                logger.info(gameLogEntry("Billboard cards are going to participate to the game."));
            } else {
                logger.info(gameLogEntry("Billboard cards are going to NOT participate to the game."));
            }
// TODO(2.0)
//            if (funFactsEval != null) {
//                logger.verbose(gameLogEntry(String.format("Fantastic, fun figures crowler <<%s>> has been activated.", "MISSING")));
//            }
        }
        exCount++;
        lastEx = extracted;
        logger.info(gameLogEntry(String.format("Number <<%2d>> has been extracted", extracted)));

        // Step 0: All lists of cards candidate to be appointed an award are cleaned
        awards.forEach(aw -> aw.getCandidatesList().clear());
        // Step 1: Look for card(s) to candidate to an available award
        int firstAvailableValue = awards.getFirstAvailableAward().getCategory();
        for (TCard c : gamingCards) {
            int result = c.checkExtraction(extracted);
            lastMatchsXExtraction += (result > 0 ? 1 : 0);
            if (result >= firstAvailableValue) {
                // On the c card has been checked the number just extracted.
                // Let's check if there is an award to grab
                for (TAward aw : awards.getAvailableAwards()) {
                    if (aw.getCategory() == result || aw.getCategory() == (result - 5) || aw.getCategory() == (result - 10)) {
                        // c card is a candidate to win the aw award.
                        aw.setStatus(TAward.TAwardStatus.CONTENDED);
                        aw.getCandidatesList().add(c);
                        aw.setWinningNumber(lastEx);
                        aw.setWinningOrdinal(exCount);
                        String owner = c.getOwner();
                        if (owner != null && owner.length() > 0) {
                            owner = ", owned by " + owner + ",";
                        } else {
                            owner = "";
                        }
                        logger.info(gameLogEntry("Card <<" + c.getLabel() + ">>" + owner + " is candidate to the award <<" + aw.getLabel() + ">>"));
                        // !!! Very important "break" instruction: so this card cannot participate to other award check whitin this same xtraction.
                        break;
                    }
                }
            }
        }
        //logger.verbose(gameLogEntry(String.format("%d cards out of %d have matched the extracted number.",lastMatchsXExtraction, gamingCards.size())));

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
                logger.info(gameLogEntry(logRecord));
                aw.setStatus(TAward.TAwardStatus.ASSIGNED);
                int index = 0;
                if (aw.getCategory()<6) {
                    index=aw.getCategory()-2;
                } else {
                    index=4;
                }
                winVector[index][exCount-1]++;
                winVector[5][exCount-1]++;
                thereIsLoneWinner = true;
                aw.getWinnerList().add(winner);
                aw.getWithJollyList().add(winner.hasLastScoreUsedJolly());
                // TODO(2.0) Some considerations...
                // What about to register whitin the card the info that it is won?
                // What about to store somewhere the card that has won, the extraction count, the number that let it to win?
                // Remove here from candidates list of leave for permormance reason (candidate list will be reset at next cycle)?
            }
        }

        // At this point, there are no awards in contended status whit just one candidate.
        // Step 3: Look for multiple candidate for the same award and manage/resolve conflicts
        for (TAward aw : awards) {
            int numCandidates = aw.getCandidatesList().size();
            if (aw.getStatus() == TAward.TAwardStatus.CONTENDED && numCandidates > 1) {
                //concurrentNominations += numCandidati;
                int index=0;
                if (aw.getCategory()<6) {
                    index=aw.getCategory()-2;
                } else {
                    index=4;
                }
                conflictVector[index][exCount - 1] += (numCandidates-1);
                conflictVector[5][exCount - 1] += (numCandidates-1);
                logger.info(gameLogEntry("Award <<" + aw.getLabel() + ">> has " + numCandidates + " candidates. Resolver needed before extract other numbers."));
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
            logger.info(gameLogEntry(String.format("Congratulations my dear, Tombola Game <<%s>> is over after %s. Thanks a lot and see you again.", gameID, TUtils.prettyMilliseconds(this.getElapsedTime()))));
            this.lastResultCode = TGameResultCode.GAME_OVER;
            return this.lastResultCode;
        }

        this.status = TGameStatus.PLAYING;
        // This status change is here because it is executed only when the extraction cycle can be considered closed.
        // If there are more than one contenders the flow will not arrive here and a call to resolveCandidates() will be necessary.
        // Change of status to PLAYING therefore will be made within the resolveCandidates() process.

// TODO(2.0)
//        if (this.funFactsEval != null) {
//            funFactsEval.postExtractionEvaluation();
//            logger.verbose(gameLogEntry("FunFacts evaluator has been executed."));
//        }

        if (thereIsLoneWinner) {
            this.lastResultCode =  TGameResultCode.WINNER;
            return this.lastResultCode;
        }
        
        this.lastResultCode = TGameResultCode.NOWINNER;
        return this.lastResultCode;
    }   // End of ExtractNumber

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
            logger.error(String.format("On game <<%s>> someone is trying to resulve an award contention that does not exist!", gameID));
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
                logger.info(gameLogEntry("User has resolved in favor of card <<" + aw.getWinnerList().get(winIdx).getLabel() + ">>"));

                String logRecord = "Award <<" + aw.getLabel() + ">> has been won by card <<" + aw.getWinnerList().get(winIdx).getLabel() + ">> ";
                if (aw.getWinnerList().get(winIdx).hasLastScoreUsedJolly()) {
                    logRecord += "Using Jolly ";
                }
                logger.info(gameLogEntry(logRecord));
                winIdx++;
            }
            aw.setStatus(TAward.TAwardStatus.ASSIGNED);

            int index = 0;
            if (aw.getCategory()<6) {
                index=aw.getCategory()-2;
            } else {
                index=4;
            }
            winVector[index][exCount-1]++;
            winVector[5][exCount-1]++;

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
                        logger.info(gameLogEntry("Card <<" + c.getLabel() + ">> has been moved as candidate to the award <<" + awards.get(i + 1).getLabel() + ">>"));
                    }
                    resolveResult = TGameResultCode.MULTICANDIDATES;
                } else {
                    // Here it can be supposed the candidate list contains just one card
                    awards.get(i + 1).getWinnerList().add(aw.getCandidatesList().get(0));
                    awards.get(i + 1).getWithJollyList().add(aw.getCandidatesList().get(0).hasLastScoreUsedJolly());
                    awards.get(i + 1).setStatus(TAward.TAwardStatus.ASSIGNED);

                    index = 0;
                    if (aw.getCategory()<6) {
                        index=aw.getCategory()-2;
                    } else {
                        index=4;
                    }
                    winVector[index][exCount-1]++;
                    winVector[5][exCount-1]++;

                    resolveResult = TGameResultCode.WINNER;

                    String logRecord2 = "Award <<" + awards.get(i + 1).getLabel() + ">> has been won by card <<" + awards.get(i + 1).getWinnerList().get(0).getLabel() + ">> ";
                    if (awards.get(i + 1).getWinnerList().get(0).hasLastScoreUsedJolly()) {
                        logRecord2 += "Using Jolly ";
                    }
                    logger.info(gameLogEntry(logRecord2));
                }
            }
            aw.getCandidatesList().clear();
        } else {
            logger.error(gameLogEntry("Too many proposed winners to this award!"));
        }

        if (resolveResult == TGameResultCode.WINNER) {
            status = TGameStatus.PLAYING;
            // Here the number extraction can be considered concluded. If there are still other candidates
            // the flow will never arrive here and another call to resolveCadidates() is required.

// TODO(2.0)            
//            if (this.funFactsEval != null) {
//                funFactsEval.postExtractionEvaluation();
//                logger.verbose(gameLogEntry("Call to Fun facts evaluator just ended."));
//            }
        }

        if (this.isGameOver()) {
            this.status = TGameStatus.ENDED;
            this.ts_end = System.currentTimeMillis();
            logger.info(gameLogEntry(String.format("Congratulation my dears, Tombola Game <%s> is Over after %s. Thanks you and see you again.", gameID, TUtils.prettyMilliseconds(this.getElapsedTime()))));
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
        logger.info(gameLogEntry(String.format("Oh my God, that's a twist! We're going to rollback the last extraction. Number %d is going back into the ballot box.", lastEx)));
        // Step 1: Roolback the last number from all playing cards
        int cardCount = 0;
        for (TCard c : gamingCards) {
            int result = c.uncheckExtraction(lastEx);
            if (result == -2) {
                logger.warning(gameLogEntry(String.format("It seemes that on card <<%s>> owned by <<%s>> the number %d is present but not already marked. It was a mistake?", c.getLabel(), c.getOwner(), lastEx)));
            } else if (result >= 0) {
                logger.verbose(gameLogEntry(String.format("Last extracted number, %d, has been rolled back on card <<%s>> owned by <<%s>>", lastEx, c.getLabel(), c.getOwner())));
                cardCount++;
            }
        }
        logger.info(gameLogEntry(String.format("Number %d has been unmarked on %d cards out of %d total playing cards.", lastEx, cardCount, gamingCards.size())));

        // Step 2: Eventually Undo assiged / deciding awards with the last number
        for (TAward aw : awards) {
            if (aw.getWinningOrdinal() == exCount && aw.getWinningNumber() == lastEx) {
                // It must be rolled back
                if (aw.isAssigned()) {
                    aw.setStatus(TAward.TAwardStatus.AVAILABLE);
                    aw.setWinningNumber(-1);
                    aw.setWinningOrdinal(-1);
                    for (TCard w : aw.getWinnerList()) {
                        logger.info(gameLogEntry(String.format("Award <<%s>> is available again. Sorry card <<%s>> owned by <<%s>>, you have to give it back now.", aw.getLabel(), w.getLabel(), w.getOwner())));
                    }
                    aw.getWinnerList().clear();
                }
                if (aw.isContended()) {
                    aw.setStatus(TAward.TAwardStatus.AVAILABLE);
                    aw.setWinningNumber(-1);
                    aw.setWinningOrdinal(-1);
                    for (TCard c : aw.getCandidatesList()) {
                        logger.info(gameLogEntry(String.format("Award <<%s>> is no more contended by card <<%s>> owned by <<%s>>", aw.getLabel(), c.getLabel(), c.getOwner())));
                    }
                    aw.getCandidatesList().clear();
                }
            }
        }

        // Step 3: Finally re-put the extracted number in the sacchetto again
        // and unmark also from the tabbellone.
        if (!this.gamingBillboard) {
            tabellone.forEach((boardCard) -> {
                boardCard.uncheckExtraction(lastEx);
            });
        }
        sacchetto.rollBack();
        int temp = lastEx;
        lastEx = sacchetto.getLastExtracted();
        logger.info(String.format("Now everithing has been restored as %d has never been extracted, I hope!", temp));
        // TODO(2.0) Verify the return codes. Maybe it is preferable to use new and ad hoc TGame statuses...
        this.lastResultCode = TGameResultCode.NOWINNER;
        return this.lastResultCode;
    }
    
    /**
     * Return the resultCode reached by the last call to {@link TGame#extractNumber()}, {@link TGame#extractNumber(int)}, 
     * {@link TGame#resolveCandidates(int)}, {@link TGame#resolveCandidates(int[])} or 
     * {@link TGame#rollback()} methods.
     * 
     * @return the last resultCode reached by one of the Tombola Game actions methods.
     */
    public TGameResultCode getLastResultCode() {
        return this.lastResultCode;
    }

    // ---------------------------------------------------------------------------------
    private String gameLogEntry(String msg) {
        // TGameLogEntry tgle = new TGameLogEntry();
        return String.format("%s] [%2d] [%2d] [%s", gameID, exCount, lastEx, msg);
    }

    private void checkInitialization() {
        if (status == TGameStatus.INITIALIZING) {
            if (sacchetto != null && tabellone != null && logger != null
                    && gamingCards != null && gamingCards.size() > 0) {
                status = TGameStatus.READY;
                logger.verbose(gameLogEntry(String.format("It seems everithing is ready to go! (%d numbers in the sacchetto, %d cards warming up)",90-sacchetto.getExtractedCount(), gamingCards.size() )));
            }
        }
    }

    private boolean isGameOver() {
        // A game is ended when there are no more awards to win        
        return awards.stream().noneMatch((aw) -> (!aw.isAssigned()));
    }
}           // End Of File - Rel.(1.1)
