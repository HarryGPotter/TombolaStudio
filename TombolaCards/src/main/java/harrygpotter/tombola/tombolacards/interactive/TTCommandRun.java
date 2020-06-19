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

import harrygpotter.tombola.tombolalib.ILogger;
import harrygpotter.tombola.tombolalib.ISetFactory;
import harrygpotter.tombola.tombolalib.TMakeSix;
import harrygpotter.tombola.tombolalib.TSeriesList;
import harrygpotter.tombola.tombolalib.TTombolaRuntimeException;
import harrygpotter.tombola.tombolalib.TUtils;
import java.util.StringTokenizer;

/**
 * This class, used when TombolaCards is in interactive mode, implements the
 * "RUN" command allowing the user to start the series of cards generation
 * process.
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTCommandRun extends TTAbstractCommand {

    /**
     * Execute the RUN command, enabling the user to start the series of cards
     * generation process.
     *
     * @param st tokenizer containing the remaining part of the command line,
     * enabling the retrieval of optional command parameters
     *
     * @return an integer indicating the outcome of the execution. RUN returns 0
     * if XXX TODO(1.1)
     */
    @Override
    public int execute(StringTokenizer st) {
        ILogger logger = (ILogger) internals.get("logger");
        ISetFactory isf = (ISetFactory) internals.get("setFactory");
        echo("Preparing the series set factory...\n");
        if (isf == null) {
            // SetFactory has not been initialized yet, so this is the first run...
            // isf must be prepared from zero.
            isf = TUtils.getSetFactoryByType((String) this.envMap.get("method"));
            internals.put("setFactory", isf);
            logger.info("Series Set Factory just created [" + isf.getClass().getSimpleName() + "]");
        }
        isf.setSeriesList((TSeriesList) this.internals.get("seriesList"));
        isf.setDesiredSeries((int) this.envMap.get("desiredSeries"));
        isf.setMaxEqualPerCard((int) this.envMap.get("maxepc"));
        isf.setMaxEqualPerRow((int) this.envMap.get("maxepr"));
        isf.setTimeLimit((long) this.envMap.get("timeLimit"));
        isf.setIterationsLimit((long) this.envMap.get("iteractionLimit"));
        if (isf.getStatus() != ISetFactory.TStatus.STOPPED && isf.getStatus() != ISetFactory.TStatus.COMPLETED)
            isf.setLogger((ILogger) this.internals.get("logger"));
        if (isf.getSeriesBuilder()==null) {
            TMakeSix builder;
            if (this.envMap.get("randomSeed") == null) {
                builder = new TMakeSix((boolean)envMap.get("avoidEmptyColumn"));
            } else {
                builder = new TMakeSix((long)this.envMap.get("randomSeed"),(boolean)envMap.get("avoidEmptyColumn"));
            }
            isf.setSeriesBuilder(builder);
            logger.info("MakeSix method just prepared [" + TMakeSix.MAKESIX_METHOD_NAME + "]");
        }

        try {
            isf.requestStart();
            internals.put("notifyConclusion",true);
            internals.put("unsavedWork", true);
            sResult = "Process Started. Hit enter or use the STATUS command to check progress. Good Luck!";            
        } catch (TTombolaRuntimeException trex) {
            sResult = trex.getMessage();
            return -1;
        }
        return 0;
    }
}
