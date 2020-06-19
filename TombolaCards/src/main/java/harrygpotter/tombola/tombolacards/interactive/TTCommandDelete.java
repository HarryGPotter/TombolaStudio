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
 * "DELETE" command allowing the user to delete from the memory one or more
 * series of cards.
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTCommandDelete extends TTAbstractCommand {

    public int execute(StringTokenizer st) {
        TSeriesList tsl = (TSeriesList) internals.get("seriesList");
        String[] params = this.parseParameter(st);
        if (tsl.size() == 0) {
            sResult = "<!> No series in memory to delete!\n";
            return -1;
        }
        if (params!=null) {
            if (params.length==1) {
                String sParam = params[0];
                if (sParam.toUpperCase().equals("ALL")) {
                    //TODO1.1 Please ask "Are you sure..."
                    if (askYesNoQuestion("Are you sure you want to delete all "+tsl.size()+" series in memory?", false)) {
                        tsl.clear();
                        sResult = "<!> All series have been removed from the memory!\n";
                    }
                } else {
                    int iTemp = Integer.parseInt(sParam);
                    if (iTemp<0 || iTemp>tsl.size()-1) {
                        sResult = "<ERROR!> Series index to delete must be between 0 and " + (tsl.size()-1) + ".\n";                    
                    }
                    if (askYesNoQuestion("Are you sure you want to delete series ["+iTemp+"]?", false)) {
                        tsl.remove(iTemp);
                        // tsl.prepareLabels();
                        tsl.compareByCard();
                        tsl.compareByRow();
                        sResult = "<!> Series at ["+iTemp+"] position has been removed from memory. Cards labels have not been updated!\n";                    
                    }
                }
            } else if (params.length==2) {
                int start = Integer.parseInt(params[0]);
                int stop = Integer.parseInt(params[1]);
                if (start<0 || start>tsl.size()) {
                    sResult = "<ERROR!> Start index must be within the [0,"+tsl.size()+"] range.\n";
                    return -2;
                }
                if (stop<0 || stop>tsl.size()) {
                    sResult = "<ERROR!> Stop index must be within the [0,"+tsl.size()+"] range.\n";
                    return -2;
                }
                if (start>stop) {
                    sResult = "<ERROR!> Start index must be lesser that stop index.\n";
                    return -2;
                }
                if(askYesNoQuestion("Are you sure you want to delete series from "+start+" to "+stop+"?", false)) {
                for(int i=start; i<(stop+1); i++) {
                    //TODO1.1 Some checks are needed
                    tsl.remove(i);
                    // tsl.prepareLabels();
                    tsl.compareByCard();
                    tsl.compareByRow();
                }
                sResult = "<!> Series from ["+start+"] to ["+stop+"] position has been removed from memory. Cards labels have not been updated!\n";                                    
                }
            } else {
                echo("<ERROR!> Wrong parameters for DELETE command.\n");
                echo("   Use DEL [nSeries | ALL | firstSeries lastSeries]\n");
                return -2;
            }
        } else {
                echo("<ERROR!> You have to specify what you want to delete.\n");
                echo("   Use DEL [nSeries | ALL | firstSeries lastSeries]\n");
                return -3;
        }
        return 0;
    }
}
