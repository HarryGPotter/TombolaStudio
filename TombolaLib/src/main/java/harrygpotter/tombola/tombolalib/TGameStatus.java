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
 * Enum class used by {@linkplain TGame} to maintain the status of the game,
 * thus allowing or disabling possible game operations in each status.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TGame
 * @see TGameResultCode
 * @since 1.8
 */
public enum TGameStatus {

    /**
     * The tombola match is still not started and mandatory elements (the
     * 'sacchetto', the playing cards, the tombola billboard) are going to be
     * prepared.
     */
    INITIALIZING,
    /**
     * Tombola match has still not been started, but all mandatory elements of
     * the game have been correctly prepared and everything is read to start and
     * extract the first number.
     */
    READY,
    /**
     * Game is ongoing, the first number has been extracted and there are still
     * numbers in the ballot box to be extracted and prizes available to be won.
     */
    PLAYING,
    /**
     * A number has been extracted and the TGame object is busy because checks
     * are performed on all playing cards and game status must be properly
     * update.
     */
    BUSY,
    /**
     * A number has been extracted, checks have been performed and MORE THAN ONE
     * CARD has matched the proper amount of hits to be candidate to the next
     * available award. The TombolaLib user must decide the winner (or the
     * winners) thus resolving the conflict before proceed with next number
     * extraction
     */
    RESOLVING,
    /**
     * Tombola game is over, even the last 'tombolino' award has been assigned,
     * you have just to say hello to your playing friends or to start a new
     * match!
     */
    ENDED
}           // End Of File - Rel.(1.1)
