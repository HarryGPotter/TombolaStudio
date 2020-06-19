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

import harrygpotter.tombola.tombolalib.TSeriesList;
import java.util.StringTokenizer;

/**
 * This class, used when TombolaCards is in interactive mode, implements the
 * "LABEL" command allowing the user to re-apply automatic labels to each card in 
 * memmory.
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTCommandLabel extends TTAbstractCommand {
    
    /**
     * Execute the LABEL command, enabling the user to re-apply automatic generated 
     * labels to each card in memory.
     *
     * @param st tokenizer containing the remaining part of the command line,
     * enabling the retrieval of optional command parameters
     *
     * @return an integer indicating the outcome of the execution. RUN returns 0
     * if XXX TODO(1.1)
     */
    @Override
    public int execute(StringTokenizer st) {
        TSeriesList tsl = (TSeriesList) internals.get("seriesList");
        if (tsl!=null && tsl.size()>0) {
            // if verbose... we could write down the label prefix and the numbering method
            if (askYesNoQuestion("Are you sure you want re-label all "+(tsl.size()*6)+" cards in memory (Previous labels will be overwritten)?", false)) {
                tsl.compareByCard();
                tsl.compareByRow();
                tsl.sortBestToWorstByCard();
                tsl.setLabelPrefix((String)envMap.get("cardLabelPrefix"));
                tsl.prepareLabels(0, tsl.size(), (String)envMap.get("cardLabelSeparator"), (TSeriesList.TLabelingModes)envMap.get("cardLabelMode"), (boolean) envMap.get("cardLabelChecksum"));
                sResult = "<!> Ok, all cards have been re-labelled";
            }
        } else {
            sResult = "<!> There are no card series to re-label!\n";
            return -1;
        }
        return 0;
    }
}
