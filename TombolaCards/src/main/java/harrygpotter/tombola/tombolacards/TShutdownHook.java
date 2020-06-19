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
package harrygpotter.tombola.tombolacards;

import harrygpotter.tombola.tombolalib.TCardFormat;
import harrygpotter.tombola.tombolalib.TFileFormatter;
import harrygpotter.tombola.tombolalib.TSeriesList;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

/**
 * This class implements ad hook to have the possibility to execute 'emergency' code 
 * when TombolaCards is interrupted during its card generation process. In this way
 * it will be possible, for instance, to save partial generated list of series.
 * 
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
class TShutdownHook extends Thread {
    
    private Map<String, Object> envMap;
    private Map<String, Object> internalMap;
    
    TShutdownHook(Map<String, Object> env, Map<String, Object> internal) {
        this.envMap = env;
        this.internalMap = internal;
    }
    
    public void run() {
        // System.out.print("TombolaCard is cheking memory before shutting down...");
        boolean somethingToSave = (boolean) internalMap.get("unsavedWork");
        if (somethingToSave) {
            TSeriesList tsl = (TSeriesList) internalMap.get("seriesList");
            if (tsl!= null && tsl.size()>0) {
                System.out.print("\nIt seems there are unsaved cards in memory. Do you wanna to save them [Yes|No]? ");
                String readLine = (new Scanner(System.in)).nextLine();
                boolean toSave = !(readLine.equalsIgnoreCase("No") || readLine.equalsIgnoreCase("N"));
                if (toSave) {
                    String fileName = (String) envMap.get("fileName");
                    System.out.print("\nPlease enter the filename [current: "+fileName+"]: ");
                    readLine = (new Scanner(System.in)).nextLine();
                    if (readLine != null && readLine.length()>0) {
                        fileName = readLine;
                    }
                    TFileFormatter tff = new TFileFormatter(TCardFormat.CSV_PLUS);
                    tsl.compareByCard();
                    tsl.compareByRow();
                    tsl.sortBestToWorstByCard();
                    tsl.setLabelPrefix((String)envMap.get("cardLabelPrefix"));
                    tsl.prepareLabels(0, tsl.size(), (String)envMap.get("cardLabelSeparator"), (TSeriesList.TLabelingModes)envMap.get("cardLabelMode"), (boolean) envMap.get("cardLabelChecksum"));                    
                    try {
                        tff.writeSeriesFile(fileName, tsl);
                        System.out.print("\n<OK!> File has been properly saved.\n");
                    } catch (IOException ex) {
                        System.err.print("<ERROR!> " + ex.getMessage());
                    }
                }
            }                
        }     
    }    
}
