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
import harrygpotter.tombola.tombolalib.TFileFormatter;
import harrygpotter.tombola.tombolalib.TSeriesList;
import java.io.IOException;
import java.util.StringTokenizer;
import harrygpotter.tombola.tombolalib.ITSetFactory;

/**
 * This class, used when TombolaCards is in interactive mode, implements the
 * "SAVE" command allowing the user to store on a file generated series of cards.
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTCommandSave extends TTAbstractCommand {

    /**
     * Execute the SAVE command, enabling the user to store series of cards in files
     * Usage: SAVE fileName fileFormat
     * generation process.
     *
     * @param st tokenizer containing the remaining part of the command line,
     * enabling the retrieval of optional command parameters
     *
     * @return 0 is no errors occurr and cards are saved in file, -1 if there are
     *         no cards in memory to store, -2 if the card generation process is still
     *         running, -3 if the chosen card format is unreconized or unsupported.
     */    
    @Override
    public int execute(StringTokenizer st) {
        // Still not perfect...
        // TODO(1.2) SAVE filename [format] [first last]
        // TODO(1.2) Check for already existing file and customized message (append?)
        String[] params = this.parseParameter(st);
        TSeriesList tsl = (TSeriesList) internals.get("seriesList");
        if (tsl.size() < 1) {
            sResult = "<ERROR> There are no series of cards to save.";
            return -1;
        }
        ITSetFactory isf = (ITSetFactory) internals.get("setFactory");        
        if (isf !=null && isf.getStatus()==ITSetFactory.TStatus.RUNNING) {
            sResult = "<ERROR> The series generation process is still RUNNING. Please wait it finishes or use the STOP command before.";
            return -2;
        }
        
        String fileName = (String) envMap.get("fileName");
        TCardFormat cardFormat = (TCardFormat) envMap.get("fileFormat");
        if(params.length>0 && params[0] != null && params[0].trim().length()>0) {
            echo(String.format("Updating output filename from [%s] to [%s]%n", fileName, params[0]));
            fileName = params[0].trim();
            envMap.put("fileName", fileName);
            if(params.length>1 && params[1] != null && params[1].trim().length()>0) {
                cardFormat = TCardFormat.valueOf(params[1]);
                if (cardFormat != TCardFormat.AUTO && cardFormat != TCardFormat.JSON &&
                        cardFormat != TCardFormat.PRETTY && cardFormat != TCardFormat.SQL &&
                        cardFormat != TCardFormat.TINY && cardFormat != TCardFormat.TINY_SPACED) {
                    echo(String.format("Updating output file format from [%s] to [%s]%n", cardFormat, TCardFormat.valueOf(params[0])));
                    envMap.put("fileFormat", cardFormat);
                } else {
                    sResult = "<ERROR!> Card file format ["+params[1]+"] not supported.";
                    return -3;
                }
            }
        }
        
        echo(String.format("Going to save %d series (%d cards) on file %s using %s format.%n", tsl.size(), tsl.size() * 6, fileName, cardFormat.name()));
        TFileFormatter tff = new TFileFormatter(cardFormat);
        try {
            
            tsl.compareByCard();
            tsl.compareByRow();
            tsl.sortBestToWorstByCard();
            tsl.setLabelPrefix((String)envMap.get("cardLabelPrefix"));
            tsl.prepareLabels(0, tsl.size(), (String)envMap.get("cardLabelSeparator"), (TSeriesList.TLabelingModes)envMap.get("cardLabelMode"), (boolean) envMap.get("cardLabelChecksum"));
            
            tff.writeSeriesFile(fileName, tsl);
            sResult = "<OK!> File has been properly saved.";
            internals.put("unsavedWork", false);
        } catch (IOException ex) {
            echo("<ERROR!> " + ex.getMessage());
        }
        return 0;
    }
}
