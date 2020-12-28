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
package harrygpotter.tombola.tombolalib;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.StringTokenizer;

/**
 * TFileFormatter objects are responsible for serialize/de-serialize whole sets
 * of series of cards (i.e.&nbsp;{@linkplain TSeriesList} objects) in text
 * based, both human and machine readable files. Several 'formats' are available
 * to support both data exchange between modules of the tombolalib project
 * itself, and to/from external tools, such databases, other applications, etc.
 * Provided methods provide plenty of opportunities to write card files as you
 * prefer. Available parameters (i.e. file format, support for jolly numbers,
 * csv and strings delimiters, etc.) can be customized:<br>
 * <ul>
 * <li>Simply using the default configurations already built in the tombolib
 * library (configure
 * <i>by exception principle</i></li>
 * <li>Specifying preferred options using proper constructor when instantiating
 * the TFileFormatter object</li>
 * <li>After object instantiation, using proper setter/getter methods</li>
 * <li>Overwriting the configuration settings at objects level directly when
 * using the <code>writeSeriesFile(...)</code> or
 * <code>readSerieFile(...)</code>.</li>
 * </ul>
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @see TCardFormatter
 * @see TCardFormat
 * @since 1.8
 */
public class TFileFormatter {

    private boolean useJolly = true;
    private boolean skipComments = false;
    private boolean useHeader = true;
    private TCardFormat format = TCardFormat.CSV;
    private OpenOption fileOption = StandardOpenOption.CREATE_NEW;
    private String csv_delimiter = ";";
    private String commentPrefix = "#";

    private final String autoDetectSequence = commentPrefix + "#$#";
    private final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private final Charset standard_charset = Charset.forName("UTF-8");

    public TFileFormatter(TCardFormat format, boolean useJolly, boolean useHeader, boolean skipComments, String csvDelimiter, String commentPrefix) {
        if (format != null) {
            this.format = format;
        }
        this.useJolly = useJolly;
        this.useHeader = useHeader;
        this.skipComments = skipComments;

        if (csvDelimiter != null) {
            // Il valore null non è consentito, al suo posto si utilizza il valore di default con cui
            //  sono inizializzate le variabili di istanza
            this.csv_delimiter = csvDelimiter;
        }
        // Il valore null non è consentito, al suo posto si utilizza il valore di default con cui
        //  sono inizializzate le variabili di istanza
        if (commentPrefix != null) {
            this.commentPrefix = commentPrefix;
        }
    }

    public TFileFormatter(TCardFormat format, boolean useJolly, boolean useHeader, boolean skipComments) {
        this(format, useJolly, useHeader, skipComments, null, null);
    }

    public TFileFormatter(TCardFormat format, boolean useJolly) {
        this(format, useJolly, true, false, null, null);
    }

    public TFileFormatter(TCardFormat format) {
        this(format, true, true, false, null, null);
    }

    public TFileFormatter() {
    }

    public OpenOption getOpenOption() {
        return this.fileOption;
    }

    public void setOpenOption(OpenOption option) {
        this.fileOption = option;
    }

    public TCardFormat getUsedFormat() {
        return this.format;
    }

    public void setUsedFormat(TCardFormat format) {
        if (format != null) {
            this.format = format;
        }
    }

    public boolean getUseJolly() {
        return this.useJolly;
    }

    public void setUseJolly(boolean jollyOn) {
        this.useJolly = jollyOn;
    }

    public boolean getSkipComments() {
        return this.skipComments;
    }

    public void setSkipComments(boolean commentsOff) {
        this.skipComments = commentsOff;
    }

    public boolean getUseHeader() {
        return this.useHeader;
    }

    public void setUseHeader(boolean headerOn) {
        this.useHeader = headerOn;
    }

    public String getCsvDelimiter() {
        return this.csv_delimiter;
    }

    public void setCsvDelimiter(String csvDelimiter) {
        if (csvDelimiter != null) {
            this.csv_delimiter = csvDelimiter;
        } else {
            this.csv_delimiter = "";
        }
    }

    public String getCommentPrefix() {
        return this.commentPrefix;
    }

    public void setCommentPrefix(String commentPrefix) {
        if (commentPrefix != null) {
            this.commentPrefix = commentPrefix;
        } else {
            this.skipComments = true;
            this.commentPrefix = "";
        }
    }

    /**
     * This method streams a set of series of cards to a file. It uses passed
     * parameters to write a textual, both human and machine readable file
     * containing tombola cards arranged as csv record lines, mixed with
     * <i>comment lines</i> reporting useful, extra information: date/time of
     * series generation, statistics about 'distance' between cards, random seed
     * used, etc. Several formats are possible, see {@linkplain TCardFormat} for
     * some details.
     *
     * @param fileName the file name (or relative/absolute path) of the file you
     * want to write.
     * @param cardSet the set of series you want write into the file
     * @return the number of series written in the file, -1 in case of error.
     * @throws IOException is there are issues during the spool on the text file
     * @see TCardFormat
     * @see TCardFormatter
     */
    public int writeSeriesFile(String fileName, TSeriesList cardSet) throws IOException {
        return writeSeriesFile(fileName, cardSet, this.getUsedFormat(), this.getOpenOption());
    }

    /**
     * This is the most versatile method available in tombolalib to stream a set
     * of series of cards to a file. It uses passed parameters to write a
     * textual, both human and machine readable file containing tombola cards
     * arranged as csv record lines, mixed with <i>comment lines</i>
     * reporting useful, extra information: date/time of series generation,
     * statistics about 'distance' between cards, random seed used, etc. Several
     * formats are possible, see {@linkplain TCardFormat} for some details.
     *
     * @param fileName the file name (or relative/absolute path) of the file you
     * want to write.
     * @param cardSet the set of series you want write into the file
     * @param format the TCardFormat value you to use to serialize cards
     * @param options specify the behavior to have when writing the file:
     * overwrite the file if already exists, go in append, raise an error. See
     * {@linkplain java.nio.file.StandardOpenOption}.
     * @return the number of series written in the file, -1 in case of error.
     * @throws IOException is there are issues during the spool on the text file
     * @see TCardFormat
     * @see TCardFormatter
     */
    public int writeSeriesFile(String fileName, TSeriesList cardSet, TCardFormat format, OpenOption options) throws IOException {
        int result = -1;
        if (fileName == null || fileName.length() < 1) {
            return result;
        }
        if (cardSet == null || cardSet.size() < 1) {
            return result;
        }
        TCardFormat writeFmt = format;
        if (writeFmt == null) {
            writeFmt = this.format;
        }
        // TODO(2.0) maybe some other controls are needed... what about a special fileName to stream to
        //  standard output or error?
        Path path = Paths.get(fileName);
        BufferedWriter bw = Files.newBufferedWriter(path, standard_charset, options);
        if (!this.getSkipComments() && writeFmt != TCardFormat.MSWORD_MAILMARGE) {
            bw.write(autoDetectSequence + TUtils.LIB_NAME + csv_delimiter + TUtils.LIB_VERSION + csv_delimiter + format + "\n");
            bw.write(commentPrefix + "\n");
            bw.write(String.format("%s %s %s%n", commentPrefix, TUtils.LIB_NAME, TUtils.LIB_VERSION));
            bw.write(String.format("%s File description: %s%n", commentPrefix, cardSet.getName()));
            bw.write(String.format("%s Creation timestamp: %s%n", commentPrefix, ZonedDateTime.now().format(timestampFormatter)));
            bw.write(String.format("%s File Format: %s. Use of Jolly numbers: %s%n", commentPrefix, writeFmt, (this.getUseJolly() ? "ON" : "OFF")));
            bw.write(String.format("%s %d Series (%d cards)%n", commentPrefix, cardSet.size(), cardSet.size() * 6));
            bw.write(String.format("%s Card generation tecnique: %s%n", commentPrefix, cardSet.getMakeSixMethod()));
            bw.write(String.format("%s Card set generation heuristic: %s%n", commentPrefix, cardSet.getSetFactoryMethod()));
            bw.write(commentPrefix + "\n");
        }
        result = 0;
        TCardFormatter tcf = new TCardFormatter(writeFmt, this.getUseJolly(), this.getCsvDelimiter());

        if (this.getUseHeader()) {
            bw.write(tcf.prepareHeader(writeFmt) + "\n");
        }

        int cardSetIdentifier = 10000 + (new Random().nextInt(90) * 1000);
        int cardCounter = cardSetIdentifier;

        for (TSeries s : cardSet) {
            for (int l = 0; l < 6; l++) {
                cardCounter++;  // This first card is numbered with 1.
                // bw.write(tcf.cardToString(s.getCard(l)) + "\n");
                // Next, more complex line has been introduced to support SQL files that need cardID and cardSetID.
                bw.write(tcf.cardToString(s.getCard(l), cardCounter, cardSetIdentifier, writeFmt, this.getUseJolly()) + "\n");
            }
            result++;
        }
        if (!this.getSkipComments() && writeFmt != TCardFormat.MSWORD_MAILMARGE) {
            bw.write(commentPrefix + "\n");
            bw.write(String.format("%s Series generator random seed: %,d%n", commentPrefix, cardSet.getMakeSixSeed()));
            bw.write(String.format("%s Generation heuristic approximately took %s%n", commentPrefix, TUtils.prettyMilliseconds(cardSet.getCreationElapsedMillis())));
            if (cardSet.getComments() != null) {
                for (String comment : cardSet.getComments()) {
                    bw.write(String.format("%s %s%n", commentPrefix, comment));
                }
            }
            bw.write(commentPrefix + "\n");
            TSeriesListStats tslsi = cardSet.seriesListStatistics();

            for (int i = 15; i >= 0; i--) {
                if (tslsi.getMaxEPCdistribution()[i] > 0) {
                    bw.write(String.format("%s %2d cards have %2d numbers equal to another card%n", commentPrefix, tslsi.getMaxEPCdistribution()[i], i));
                }
            }
            for (int i = 5; i >= 0; i--) {
                if (tslsi.getMaxEPRdistribution()[i] > 0) {
                    bw.write(String.format("%s %2d cards have at least a row with %2d numbers equal to a row in an another card%n", commentPrefix, tslsi.getMaxEPRdistribution()[i], i));
                }
            }
            bw.write(String.format("%s%n%s END OF CARD FILE%n", commentPrefix, commentPrefix));
        }
        bw.close();
        return result;
    }

    /**
     * This method reads a list of series of cards from a file previously
     * created using
     * {@linkplain TFileFormatter#writeSeriesFile(String, TSeriesList, TCardFormat, OpenOption)}
     * companion method on this class.&nbsp;It returns a
     * {@linkplain TSeriesList} object ready to be managed by all other
     * TombolaLib classes and methods.&nbsp; File format is automatically
     * determined using tags on the file header lines.
     *
     * @param fileName String containing the full or absolute path and file name
     * to read
     * @return a TSeriesSet object containing all read cards. Null if something
     * goes wrong.
     * @throws java.io.IOException is issues raise when truing t read the
     * specified text file
     */
    public TSeriesList readSeriesFile(String fileName) throws IOException, TTombolaRuntimeException {
        return this.readSeriesFile(fileName, null);
    }

    /**
     * This method reads a list of series of cards from a file previously
     * created using
     * {@linkplain TFileFormatter#writeSeriesFile(String, TSeriesList, TCardFormat, OpenOption)}
     * companion method on this class.It returns a {@linkplain TSeriesList}
     * object ready to be managed by all other TombolaLib classes and methods.
     *
     * @param fileName String containing the full or absolute path and file name
     * to read
     * @param format a TCardReader.TForma value identifying the TombolaLib
     * specific file format used to save filename. Supported format are CSV,
     * CSV_SPACED, TINY, TINY_SPACED and AUTO. If format is set to AUTO,
     * readCardFile will try to figure out the real format checking tags in the
     * first comment line of the file.
     * @return a TSeriesSet object containing all read cards. Null if something
     * goes wrong.
     * @throws java.io.IOException is issues raise when truing t read the
     * specified text file
     */
    public TSeriesList readSeriesFile(String fileName, TCardFormat format) throws IOException, TTombolaRuntimeException {
        if (fileName == null) {
            return null;
        }
        if (format == null) {
            format = TCardFormat.AUTO;
        }
        Path fileToRead = Paths.get(fileName);
        TSeriesList set = null;
        int i = 0;
        BufferedReader br = Files.newBufferedReader(fileToRead, this.standard_charset);
        String line;
        if (format == TCardFormat.AUTO) {
            // First line should allow for file format detection
            if ((line = br.readLine()).length() > 0 && line.startsWith(autoDetectSequence)) {
                StringTokenizer st = new StringTokenizer(line, csv_delimiter);
                String libName = st.nextToken();
                String libVersion = st.nextToken();
                // TODO(2.0) Controllare compatibilità con versione di libreria.
                String sFileformat = st.nextToken();
                format = TCardFormat.valueOf(sFileformat);
            } else {
                throw new TTombolaRuntimeException("[ERROR] Impossible to AUTO detect input card series file format.");
            }
        }
        TCard[] tempSeries = new TCard[6];
        TCardFormatter fmt = new TCardFormatter(format, this.getUseJolly(), this.getCsvDelimiter());
        while ((line = br.readLine()) != null) {
            // Here line is not null for sure
            if (!line.startsWith(commentPrefix) && line.length() > 0) {
                TCard newCard = null;
                try {
                    newCard = fmt.stringToCard(line);
                    // System.out.println("\nDEBUG: " + newCard.asString());
                } catch (NumberFormatException nfe) {
                    // Do Nothing, simpli skip the lines;
                    // (TODO2.0) are we sure?
                    // Should we use the log to trace the error?
                    // nfe.printStackTrace();
                }

                if (newCard != null) {
                    tempSeries[i % 6] = newCard;
                    i++;
                }
                if ((i != 0) && (i % 6 == 0)) {
                    //System.out.println("i=" + i + " Aggiungo la serie al set");
                    if (set == null) {
                        set = new TSeriesList(fileName);
                    }
                    TSeries newSeriesObj = new TSeries(tempSeries);
                    int errorInSeries = newSeriesObj.verifySeries();
                    if (errorInSeries > -1) {
                        throw new TTombolaRuntimeException("[ERROR] Read cards do not form a correct series of six cards. Error in card [" + errorInSeries + "].");
                    };
                    set.add(newSeriesObj);
                    tempSeries = new TCard[6];
                }
            }
        } // End of while cycle to read cards.
        br.close();
        if (set != null) {
            set.compareByCard();
            set.compareByRow();
            set.setMakeSixMethod("Just read from another file");
            set.setSetFactoryMethod("Just read from another file");
        }
        return set;
    }
}           // End Of File - Rel.(1.1)
