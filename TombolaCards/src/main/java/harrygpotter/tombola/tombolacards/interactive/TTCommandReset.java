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
import harrygpotter.tombola.tombolalib.TCardFormat;
import harrygpotter.tombola.tombolalib.TSeriesList;
import harrygpotter.tombola.tombolalib.TUtils;
import java.util.StringTokenizer;

/**
 * TODO(1.1) SCRIVERE IL JAVADOC QUI
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTCommandReset extends TTAbstractCommand {
    /**
     * 
     * @param st
     * @return 
     */
    public int execute(StringTokenizer st) {
        echo("<WARNING!>\n");
        echo("This command clear all TombolaCards memory and restore all environment\n");
        echo("parameters to their default values.\n\n");
        boolean mustReset = askYesNoQuestion("Are you sure you want to CLEAR TombolaCards memory?", false);
        if (mustReset) {
            envMap.put("fileName", "CardSeries001.csv");
            envMap.put("fileFormat", TCardFormat.CSV_PLUS);
            envMap.put("desiredSeries", 10);
            envMap.put("method", TUtils.AVAILABLE_GENERATION_METHODS[0]);
            envMap.put("maxepc", ISetFactory.MINIMUM_MAXEPC + 4);            // TODO(1.2) how to improve?
            envMap.put("maxepr", ISetFactory.MINIMUM_MAXEPR + 3);            // TODO(1.2) how to improve?
            envMap.put("avoidEmptyColumn", true);
            envMap.put("useJolly", true);
            envMap.put("verbose", false);
            envMap.put("unattended", false);
            envMap.put("fileOverwrite", false);
            envMap.put("cardLabelPrefix", "TT");
            envMap.put("cardLabelChecksum", true);
            envMap.put("cardLabelSeparator", "-");
            envMap.put("cardLabelMode", TSeriesList.TLabelingModes.BYCARDS);
            envMap.put("traceLogFileName", "TombolaCards.log");
            envMap.put("defaultSeriesTitle", "CardSeries001");
            envMap.put("randomSeed", null);
            envMap.put("timeLimit", ISetFactory.MAX_ITERATIONS_MILLISECS);
            envMap.put("iteractionLimit", ISetFactory.MAX_ITERATIONS);

            TSeriesList tsl = new TSeriesList("Initialized by TombolaCards!");
            internals.put("seriesList", tsl);
            internals.put("mustGenerate", false);
            internals.put("unsavedWork", false);
            internals.put("notifyConclusion", false);

            echo("Et voilà! All cleared!\n\n");
        } else {
        }
        return 0;
    }

}
