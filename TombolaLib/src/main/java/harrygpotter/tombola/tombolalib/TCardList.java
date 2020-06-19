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

import java.util.ArrayList;

/**
 * TCardList is an {@linkplain ArrayList} specialization class helping to
 * collect together set of cards and specifically to manage them during tombola
 * games.&nbsp;During the cards generation processes, it is preferable to manage
 * cards in groups of six of them, called series, thus {@linkplain TSeriesList}
 * objects target this need.&nbsp;During a game, instead, cards must be managed
 * one at a time and TCardList objects are here to serve.&nbsp;In TCardList
 * class are also implemented import methods and constructors to convert a
 * TSeriesList object to a TCardList one.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TGame
 * @see TSeriesList
 * @since 1.8
 */
public class TCardList extends ArrayList<TCard> {

    /**
     * Default constructor. It returns a new, empty TCardList object.
     */
    public TCardList() {
        super();
    }

    /**
     * Create a new TCardList object and fill it using the cards within the
     * TSeriesList object passed in input.&nbsp;Pay attention: cards are not
     * copied, just passed by reference.
     *
     * @param seriesList The TSeriesList object containing the cards to import
     * in the new TCardList object.
     */
    public TCardList(TSeriesList seriesList) {
        if (seriesList == null) {
            throw new IllegalArgumentException("Source series list cannot be null to create a card list.");
        }
        seriesList.forEach((s) -> {
            for (int i = 0; i < 6; i++) {
                this.add(s.getCard(i));
            }
        });
    }

    /**
     *
     * Reset, for each card in the list, all the parameters maintaining status
     * information related to a tombola game, such as extracted and matched
     * numbers, eventually won awards, etc.&nbsp;Use this method to prepare a
     * list of cards before start a new tombola game.
     *
     * @see TGame
     */
    public void resetGameStatus() {
        this.forEach(c -> c.resetGameStatus());
    }

}           // End Of File - Rel.(1.1)
