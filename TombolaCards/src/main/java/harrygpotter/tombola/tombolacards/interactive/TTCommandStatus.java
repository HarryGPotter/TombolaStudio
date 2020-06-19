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
import harrygpotter.tombola.tombolalib.TMakeSix;
import harrygpotter.tombola.tombolalib.TSeriesList;
import harrygpotter.tombola.tombolalib.TUtils;
import java.util.StringTokenizer;

/**
 * This class, used when TombolaCards is in interactive mode, implements the "STATUS" 
 * command allowing the user to show up on the console the status of the generation
 * algorithm and the series of cards in memory.
 * 
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTCommandStatus extends TTAbstractCommand {
    
    @Override
    public int execute(StringTokenizer st) {
        ISetFactory isf = (ISetFactory) internals.get("setFactory");
        if (isf != null) {
            echo("\n");
            echo("<-> Generation algorithm " + isf.getMethodName()+" is "+isf.getStatus()+".\n");
            if (isf.getStatus() == ISetFactory.TStatus.RUNNING) {
                echo(String.format("<-> Time elapsed: %s\n", TUtils.prettyMilliseconds(isf.getElapsedMillisecs())));
                echo(String.format("<-> Limit at: %s (now at %3.1f%%)\n", TUtils.prettyMilliseconds(isf.getTimeLimit()),(((float) isf.getElapsedMillisecs())/isf.getTimeLimit())));
            }
        } else {
            echo("<-> Generation algorithm ("+(String)envMap.get("method")+") has not initialized jet.\n");        
        }
        int nSeries = ((TSeriesList) internals.get("seriesList")).size();
        int nDesired = (int) envMap.get("desiredSeries");
        float fProgress = ((float)nSeries)/nDesired;
        echo(String.format("<-> There are %d series (%d cards) in memory, agaist %d series (%d cards) to be generated (%3.1f%%).%n",nSeries,nSeries*6,nDesired,nDesired*6,fProgress*100));
        echo(String.format("<-> Max equal number per card limit: %d\n", (int)envMap.get("maxepc")));
        echo(String.format("<-> Max equal number per row limit: %d\n", (int)envMap.get("maxepr")));
        if ((boolean)envMap.get("verbose")) {
            echo("<-> Series combination algorithm: " + TMakeSix.MAKESIX_METHOD_NAME+"\n");
            if (isf!= null) {
                echo(String.format("<-> Starting Random seed: %,d.\n",isf.getSeriesBuilder().getRandomSeed()));
            }
            echo("<-> Avoid Empty Column is set to: " + (boolean) envMap.get("avoidEmptyColumn")+"\n");
        }
        echo("\n");
        if ((boolean) internals.get("unsavedWork")) {
            echo("<!> Attention! There are unsaved cards in memory!\n");
            echo("\n");
        }
        return 0;
    }
}           // End Of File - Rel.(1.1)