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

import harrygpotter.tombola.tombolalib.TFileFormatter;
import harrygpotter.tombola.tombolalib.TSeries;
import harrygpotter.tombola.tombolalib.TSeriesList;
import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

/**
 * This class, used when TombolaCards is in interactive mode, implements the
 * "READ" command allowing the user to load a card series file into memory.
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */public class TTCommandRead extends TTAbstractCommand {

    /**
     * Execute the READ command, enabling the user to read from a file series of
     * cards.
     * Usage: READ fileName
     *
     * @param st tokenizer containing the remaining part of the command line,
     * enabling the retrieval of optional command parameters
     *
     * @return 
     */
    public int execute(StringTokenizer st) {
        if (st.hasMoreElements()) {
            String sFileToRead = st.nextToken();
            File fileToRead = new File(sFileToRead);
            if (!(fileToRead.exists() && !fileToRead.isDirectory())) {
                sResult = "<ERROR!> File to read does not exist or is inaccessible. Please check.\n";
                return -1;
            }
            TSeriesList tsl = (TSeriesList) internals.get("seriesList");
            boolean inputOverWrite = false;
            if (tsl != null && tsl.size() > 0) {
                echo(String.format("<Attention!> There are already %d series in memory.\n", tsl.size()));
                inputOverWrite = askYesNoQuestion("Overwrite the existing series? ", false);
            }
            TFileFormatter tff = new TFileFormatter();
            TSeriesList justRead = null;
            try {
                justRead = tff.readSeriesFile(sFileToRead);
            } catch (IOException ioex) {
                ioex.printStackTrace();
                // TODO(1.2) Improve exception management...
            }
            if (justRead != null) {
                echo(String.format("Just read %d series (%d cards).%n", justRead.size(), justRead.size() * 6));
                if (justRead.size()>0) {
                    if (inputOverWrite) {
                        tsl = justRead;
                        echo("Read series are now in memory just as they have been read.\n");                        
                    } else {
                        for(TSeries s : justRead) {
                            tsl.add(s);
                        }
                        echo(String.format("Read series have been added. In memory there are now %d series, %d cards.%n", tsl.size(), tsl.size()*6));
                    }
                }
            }            
        } else {
            echo("<ERROR> Input file name must be specified after READ keyword.\n");
            String prevInputFile = (String) envMap.get("inputFileName");
            if (prevInputFile != null) {
                echo("File previously read: " + prevInputFile+"\n");
            }
        }
        return 0;
    }
}
