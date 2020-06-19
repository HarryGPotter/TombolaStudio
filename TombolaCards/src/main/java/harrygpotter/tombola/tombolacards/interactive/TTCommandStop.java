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
package harrygpotter.tombola.tombolacards.interactive;

import harrygpotter.tombola.tombolalib.ISetFactory;
import java.util.StringTokenizer;

/**
 * This class, used when TombolaCards is in interactive mode, implements the
 * "STOP" command, allowing the user to pause or stop the series of cards generation
 * process.
 * 
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTCommandStop extends TTAbstractCommand {

    @Override
    public int execute(StringTokenizer st) {
        ISetFactory isf = (ISetFactory) this.internals.get("setFactory");
        if (isf != null && isf.getStatus()== ISetFactory.TStatus.RUNNING) {
            isf.requestStop();
            sResult = "<!> OK, Generation Stop has been requested.\n<< Check the status of the process using STATUS command.\n<< You can re-start the processing using RUN Command.";
            return 0;
        } else {
            sResult = "<!> Ehi, There is no generation process running!";
            return -1;
        }
    }
}
