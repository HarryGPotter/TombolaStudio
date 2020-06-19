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

import harrygpotter.tombola.tombolalib.TCardFormat;
import harrygpotter.tombola.tombolalib.TCardFormatter;
import harrygpotter.tombola.tombolalib.TSeriesList;
import harrygpotter.tombola.tombolalib.TCard;
import java.util.StringTokenizer;

/**
 * This class, used when TombolaCards is in interactive mode, implements the "SHOW" 
 * command allowing the user to display on monitor a card or a group of cards that
 * have been previously generated or loaded in memory from a card file.
 * 
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTCommandShow extends TTAbstractCommand {
    
    /**
     * Execute the SHOW command, enabling the user to display on the screen series
     * or cards currently in memory. Here follow the supported sintax:<br>
     * SHOW ALL | Sx | Cx | Sx Sy | Cx Cy<br>
     *
     * @param st tokenizer containing the remaining part of the command line,
     * enabling the retrieval of optional command parameters.
     *
     * @return an integer indicating the outcome of the execution. SHOW returns 0
     * if one or more cards have been displayed, -1 if there are no cards in memory,
     * -2 if no parameter are specified after the command, -3 if parameter for 
     * starting and endind cards to be displayed are incorrect or not in the right
     * order.
     */
    @Override
    public int execute(StringTokenizer st) {
        String[] params = parseParameter(st);
        int cStart = -1;
        int cEnd = -1;
        TSeriesList tsl = (TSeriesList) internals.get("seriesList");
        if (tsl==null || tsl.size()<1) {
            echo("<ERROR> There are no cards in memory to display.\n");
            return -1;
        }
        if (params.length==0) {
            echo("<ERROR> You should indicate which card(s) or series do you need to display.\n");
            echo("        Type HELP SHOW to get more information.\n");
            return -2;
        }        
        params[0] = params[0].toUpperCase();
        if (params.length == 2) {
            params[1] = params[1].toUpperCase();
            if (params[0].startsWith("S")) {
                cStart = 6 * Integer.parseInt(params[0].substring(1));
            } else if (params[0].startsWith("C")) {
                cStart = Integer.parseInt(params[0].substring(1));
            } else {
                cStart = Integer.parseInt(params[0]);
            }

            if (params[1].startsWith("S")) {
                cEnd = 6 + 6 * Integer.parseInt(params[1].substring(1));
            } else if (params[1].startsWith("C")) {
                cEnd = 1 + Integer.parseInt(params[1].substring(1));
            } else {
                cEnd = 1 + Integer.parseInt(params[1]);
            }

            
        } else if (params.length == 1) {
                    
            if (params[0].equals("ALL")){
                cStart = 0;
                cEnd = tsl.size()*6;
            } else {
                if (params[0].startsWith("S")) {
                    cStart = 6*Integer.parseInt(params[0].substring(1));
                    cEnd = cStart+6;
                } else if (params[0].startsWith("C")) {
                    cStart = Integer.parseInt(params[0].substring(1));
                    cEnd = cStart+1;
                } else {
                    cStart = Integer.parseInt(params[0]);
                    cEnd = cStart+1;
                }
            }         
        }
        TCardFormat format;
        if (st.hasMoreElements()) {
            format = TCardFormat.valueOf(st.nextToken());
        } else {
            format = TCardFormat.PRETTY;
        }
        TCardFormatter tcf = new TCardFormatter(format, (boolean)envMap.get("useJolly"));
        System.out.println();
        if (cStart<0 || cStart> tsl.size()*6-1) {
            echo("<ERROR> Index for firt card to show is either incorrect or out of available cards range.\n");
            return -3;            
        }
        if (cEnd<1 || cEnd> tsl.size()*6) {
            echo("<ERROR> Index for last card to show is either incorrect or out of available cards range.\n");
            return -3;            
        }
        if (cStart >= cEnd) {
            echo("<ERROR> Show start index must be lesser or equal to the show end index.\n");
            return -3;            
        }
        for(int i=cStart; i<cEnd; i++) {
            TCard c = tsl.get(i/6).getCard(i%6);
            System.out.println("--------------------------------------------- [" + c.getLabel() + "] (n"+i+" s"+i/6+"c"+i%6+") ---");
            System.out.println(tcf.cardToString(c));            
        }
        return 0;
    }
}
