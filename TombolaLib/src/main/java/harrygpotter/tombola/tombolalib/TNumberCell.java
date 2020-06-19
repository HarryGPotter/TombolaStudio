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
 * This is just a simple Java POJO or data object used to access tombola cards
 * elements in a "JavaBean friendly" environment, such as a JavaServer Faces
 * based web application.&nbsp;It simply stores a card number together with its
 * gaming attributes (if it has been already matched or not, if it is a jolly
 * number).&nbsp;TCard and TBillboardCard objects can provide a grid of
 * TNumberCell object as an alternative view of themselves just to be used in
 * such web application...
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @see TCard
 * @see TBillboardCard
 * @since 1.8
 */
public class TNumberCell {

    private int number;
    private boolean matched;
    private boolean jolly;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isJolly() {
        return jolly;
    }

    public void setJolly(boolean jolly) {
        this.jolly = jolly;
    }
}           // End Of File - Rel.(1.1)
