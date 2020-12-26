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
 * Enum class used mainly by {@linkplain TGame#extractNumber(int)} and
 * {@linkplain TGame#resolveCandidates(int)} methods to notify the result of
 * their invocation to the TombolaLib users in a defined, controlled and finite
 * way.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TGame
 * @see TGameStatus
 * @since 1.8
 */
public enum TGameResultCode {

    /**
     * Extraction of conflict resolving method has been invoked when the game is
     * in a wrong status, that is something is still not ready and the game
     * cannot still be started.
     */
    NOT_READY,
    
    /**
     * Number passed as input argument is invalid, because it is out of the
     * valid range [1..90]
     */
    WRONG_NUMBER,
    
    /**
     * The 'resolve candidates' method has been invoked, but there is no
     * contention by more than one card for the same award.
     */
    NOT_RESOLVING,
    
    /**
     * Extraction method as been invoked with a number as input argument that
     * has been already checked previously in the same game, thus this is an
     * invalid situation.
     */
    ALREADY_CHECKED,
    
    /**
     * The required number has been extracted, all card checks have been
     * performed and there is no card hitting required scores to get an award,
     * the game can continue with another number extraction.
     */
    NOWINNER,
    
    /**
     * The required number has been extracted, all card checks have been
     * performed and there is just a single winner for an available award that
     * as been automatically appointed to the award.
     */
    WINNER,
    
    /**
     * TODO(2.0)
     */
    ACCEPT_OR_DENY,
    
    /**
     * The required number has been extracted, all card checks have been
     * performed and there are two or more cards hitting scores required to win
     * the same available award, so the 'resolve candidates' method must be
     * invoked as next action and before a new number extraction for the game.
     */
    MULTICANDIDATES,
    
    /**
     * Last available award has been won and assigned to a card, the match can
     * be considered finished
     */
    GAME_OVER
}           // End Of File - Rel.(1.1)
