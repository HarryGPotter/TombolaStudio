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

import java.util.StringTokenizer;
import harrygpotter.tombola.tombolalib.TSeriesList;
import java.util.Arrays;
import harrygpotter.tombola.tombolalib.ITSetFactory;

/**
 * This class, used when TombolaCards is in interactive mode, implements the
 * "ENV" command allowing the user to display on the console the current value
 * of all relevant environment variables and parameters, used to prepare and,
 * generally speaking, to work with series of cards.
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTCommandEnv extends TTAbstractCommand {

    /**
     * Execute the ENV command
     *
     * @param st tokenizer containing the remaining part of the command line.
     * If present, it allow to specify a filter for the displayed environment
     * variable. Only parameters starting like specified parameter will be displayed.
     *
     * @return ad integer indicating the outcome of the execution. ENV returns
     * always 0, no other error codes.
     */
    @Override
    public int execute(StringTokenizer st) {
        String[] params = this.parseParameter(st);
        if (params == null || params.length == 0) {
            System.out.println("\n Current environment parameters:");
            for (String key : envMap.keySet()) {
                int iPadding = 21 - key.length();
                char[] cPadding = new char[iPadding];
                Arrays.fill(cPadding, ' ');
                System.out.println("   " + key + ": " + new String(cPadding) + envMap.get(key));
            }
            System.out.println();

            TSeriesList tsl = (TSeriesList) this.internals.get("seriesList");
            ITSetFactory isf = (ITSetFactory) this.internals.get("setFactory");

            System.out.printf(" There are %d series in memory.\n", tsl.size());
            if (isf != null) {
                System.out.println(" Series factory engine is " + isf.getStatus());
            } else {
                System.out.println(" Series factory engine has not been initialized jet.");
                System.out.println(" Use the RUN command to start the engine.");
            }
            System.out.println();
        } else {
            for (String p : params) {
                p = p.replace("*", "");
                for (String key : this.envMap.keySet()) {
                    if (key.toLowerCase().startsWith(p.toLowerCase())) {
                        int iPadding = 21 - key.length();
                        char[] cPadding = new char[iPadding];
                        Arrays.fill(cPadding, ' ');
                        System.out.println("   " + key + ": " + new String(cPadding) + envMap.get(key));
                    }
                }
            }
        }
        return 0;
    }
}           // End Of File - Rel.(1.1)
