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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * This class has the responsibilities to help tombolalib library users to print
 * on paper tombola cards.&nbsp;The design principles should be as simple as
 * effective.&nbsp;It uses several template prepared in html+css format having
 * inside some placeholders marked with specific character sequences.&nbsp;To
 * print series of cards, placeholders are substituted by actual numbers, card
 * labels, etc.&nbsp;and output file are generated in plain html+css
 * format.&nbsp;Such file can thus be printed using any modern browser
 * (i.e.&nbsp;Microsoft Edge, Google Chrome, Safari, Firefox,
 * etc.).&nbsp;Standard format are provided to print one or more card per pages,
 * as well as to make available different styles and graphic layouts.&nbsp; User
 * can also prepare and use their own templates, following instruction provided
 * within the library user documentation.
 *
 * @author Harry G Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TFileFormatter
 * @see TCard
 * @since 1.8
 */
public class THtmlPrinter {

    private static final String TAG_START = "${";
    private static final String TAG_END = "}";
    private static final String TAG_BLANK = "&nbsp;";

    private static final String TAG_CARDBLOCK_START = "CARDBLOCK-START";
    private static final String TAG_CARDBLOCK_END = "CARDBLOCK-END";

    private static final String TAG_TEMPLATE_HEADER_IDENTIFIER = "TombolaLib;1.1;HTML-PRINT-CARDS-TEMPLATE;";

    private DateTimeFormatter dt_formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private DateTimeFormatter ts_formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    private final Charset standard_charset = Charset.forName("UTF-8");

    private THtmlTemplate tht = null;
    private String templateFilePath = null;
    private final Map<String, String> params = new HashMap<>();

    private String noNumberTag = TAG_BLANK;
    private boolean highlightMatched = true;        // TODO(2.0)
    private boolean highlightJolly = true;
    private boolean oneSingleFile = true;           // TODO(2.0)

    /**
     * The constructor requires the file name (complete path9 where the template
     * to use is located.&nbsp; A not null nor empty string must be
     * provided.&nbsp;Check to control file presence and validity is not
     * performed at construction time, but just before the parsing and output
     * process starts.
     *
     * @param templateFilePath relative or absolute complete path specifying the
     * valid template file to use.
     */
    public THtmlPrinter(String templateFilePath) {
        if (templateFilePath == null || templateFilePath.length() < 1) {
            throw new IllegalArgumentException("<ERROR> Template file cannot be null or empty.");
        }
        this.templateFilePath = templateFilePath;
        tht = new THtmlTemplate();
    }

    /**
     * Use this method to generate the HTML file to be used to print cards. It
     * took in input a TSeriesList object, the indexes of the first and last
     * cards to be printed (belonging to the [0, series.size()*6] range) and the
     * name of the output file to generate (template filename is specified
     * within the constructor). Standard options for the output file (i.e.
     * overwrite, append, etc.) can also be specified.
     *
     * @param tsl the whole list of series to be printed
     * @param first the index of the first card within the series list to be
     * printed
     * @param last the index of the last card within the series list to be
     * printed
     * @param outFilename the absolute or relative name of the html output file
     * to be generated.
     * @param options specify file writing options (i.e. overwrite, append,
     * etc.)
     * @return the number of printed cards if everything goes ok, a negative
     * value indicating the kind of error otherwise: -1 if the list of series
     * object is null or empty -2 the output filename is null or empty -3 the
     * html template set at construction time is not valid or has formal errors
     * raising during parsing.
     * @throws IOException is something goes wrong when writing the file.
     */
    public int printHtml(TSeriesList tsl, int first, int last, String outFilename, OpenOption options) throws IOException {
        if (tsl == null || tsl.size() < 1) {
            return -1;
        }
        if (outFilename == null || outFilename.length() < 1) {
            return -2;
        }
        int count = 0;
        Path templateToRead = Paths.get(templateFilePath);
        try (BufferedReader br = Files.newBufferedReader(templateToRead, this.standard_charset)) {
            String line;
            String parsedLine;
            StringBuilder headerTarget = new StringBuilder();
            StringBuilder footerTarget = new StringBuilder();
            StringBuilder bodySource = new StringBuilder();

            // Cycle 1. Read and parse the first 'fixed part' of the template file.
            initParamMap(tsl);
            boolean exitFlag = false;
            boolean valid_template = false;
            boolean skip_line = false;
            while (!exitFlag && ((line = br.readLine()) != null)) {
                if (line.contains(TAG_START)) {
                    // There is something to parse
                    if (line.contains(TAG_START + TAG_CARDBLOCK_START)) {
                        // Skip the line, but change template section
                        // TODO(2.0) Read here the repetition value?
                        exitFlag = true;
                        skip_line = true;
                    } else if (line.contains(TAG_START + TAG_TEMPLATE_HEADER_IDENTIFIER)) {
                        valid_template = parseTemplateHeader(line);
                    }
                    parsedLine = parseLine(line);
                } else {
                    parsedLine = line;
                }
                if (skip_line) {
                    skip_line = !skip_line;
                } else {
                    headerTarget.append(parsedLine).append("\n");
                }
            }

            if (!valid_template) {
                return -3;
            }

            // headerSource is ready to be flushed to the printed file.
            Path path = Paths.get(outFilename);
            BufferedWriter bw = Files.newBufferedWriter(path, standard_charset, options);
            bw.write(headerTarget.toString());

            // Cycle 2.a // Just read the body source to be repeated
            exitFlag = false;
            skip_line = false;
            while (!exitFlag && ((line = br.readLine()) != null)) {
                if (line.contains(TAG_START + TAG_CARDBLOCK_END)) {
                    // Skip the line, but change template section
                    // Read here the repetition value?
                    exitFlag = true;
                    skip_line = true;
                }
                if (skip_line) {
                    skip_line = !skip_line;
                } else {
                    bodySource.append(line).append("\n");
                }
            }

            // Cycle 2.b Here are within the 'repeatable body' of the template
            if (first < 0) {
                first = 0;
            }
            if (last >= tsl.size() * 6) {
                last = tsl.size() * 6 - 1;
            }
            // int totalCards = tsl.size() * 6;
            int totalCards = last - first + 1;
            int numBlocks;
            int cardsPerBlock = tht.getCardsPerBlock();
            if ((totalCards % cardsPerBlock) == 0) {
                numBlocks = totalCards / cardsPerBlock;
            } else {
                numBlocks = 1 + (totalCards / cardsPerBlock);
            }
            if (numBlocks > 0) {
                for (int i = 0; i < numBlocks; i++) {
                    StringBuilder bodyTarget = new StringBuilder();
                    updateParamMap(tsl, i, first);
                    bodyTarget.append(parseLine(bodySource.toString()));
                    bw.write(bodyTarget.toString());
                    count += cardsPerBlock;
                }   // End for numBlocks
            }   // End if (numBlocks>0)

            // Cycle 3. Read, parse and write the last 'fixed part' of the template file.
            exitFlag = false;
            while (!exitFlag && (line = br.readLine()) != null) {
                if (line.contains(TAG_START)) {
                    // There is something to parse
                    footerTarget.append(parseLine(line)).append("\n");
                } else {
                    footerTarget.append(parseLine(line)).append("\n");
                }
            }
            bw.write(footerTarget.toString());
            bw.close();
        }
        return count;
    }

    /**
     * Allow to define which number, string, placeholder or html encoding must
     * be used to put a "not a number" on one of the 18 cell composing a
     * card.&nbsp;Default value is the "Non breakable Space" html tag, that is
     * &amp;nbsp;
     *
     * @param tag the String to use in place of a missing number on the tombola
     * grid.
     */
    public void setNoNumberTag(String tag) {
        this.noNumberTag = tag;
    }

    /**
     * Return the string used to print a "no number" cell on the tombola card.
     *
     * @return the string used to print a "no number" cell on the tombola card
     */
    public String getNoNumberTag() {
        return this.noNumberTag;
    }

    /**
     * Set the format to use to print "date" fields within the html output file.
     *
     * @param dateFormat the string expressing, using standard Java format tags,
     * the format to use
     */
    public void setDateFormatter(String dateFormat) {
        this.dt_formatter = DateTimeFormatter.ofPattern(dateFormat);
    }

    /**
     * Set the format to use to print "timestamp" fields within the html output
     * file.
     *
     * @param tsFormat the string expressing, using standard Java format tags,
     * the format to use
     */
    public void setTineStampFormatter(String tsFormat) {
        this.ts_formatter = DateTimeFormatter.ofPattern(tsFormat);
    }
    
    /**
     * Allow the highlights on printed paper of jolly numbers.
     * 
     * @param highlightJolly true to apply jolly CSS styles to the number marked
     * as jolly on each card, false to suppress the highlight of jolly numbers.
     */
    public void enableJolly(boolean highlightJolly) {
        this.highlightJolly = highlightJolly;
    }

    /** 
     * Return true is jolly are marked with a specific CSS style, false otherwise.
     * 
     * @return true is jolly are marked with a specific CSS style, false otherwise. 
     */
    public boolean isJollyEnabled() {
        return this.highlightJolly;
    }
    // ----------------------------------------------------------------------
    // Prepare the initial "document scoped" tag-parameter couples that could be 
    //  potentially used within the template
    private void initParamMap(TSeriesList tsl) {
        params.put("Author", "Harry G. Potter");
        params.put("SeriesListName", tsl.getName());
        params.put("SeriesListTitle", tsl.getName());
        params.put("CurrentDate", ZonedDateTime.now().format(dt_formatter));
        params.put("CurrentTimeStamp", ZonedDateTime.now().format(ts_formatter));
        params.put("CreationTimeStamp", tsl.getCreationTimeStamp().format(ts_formatter));
        params.put("GenerationTotalCounter", "" + tsl.getMakeSixCounter());
        params.put("SeriesGenerationMethod", tsl.getMakeSixMethod());
        params.put("GenerationSeed", "" + tsl.getMakeSixSeed());
        params.put("GenerationMethod", tsl.getSetFactoryMethod());
        params.put("GenerationDuration", TUtils.prettyMilliseconds(tsl.getCreationElapsedMillis()));
    }

    // Update the "substitution map object at each card block iteration, so that proper numbers
    //  are used to prepare cards.
    private void updateParamMap(TSeriesList tsl, int blockNum, int offset) {
        int cardsPerBlock = tht.getCardsPerBlock();
        for (int i = 0; i < cardsPerBlock; i++) {
            int cardIndex = (blockNum * cardsPerBlock) + i + offset;
            int seriesIndex = cardIndex / 6;
            int cardPos = cardIndex % 6;
            TCard cCard = tsl.get(seriesIndex).getCard(cardPos);
            params.put("C[" + i + "].L", cCard.getLabel());
            for (int row = 0; row < 3; row++) {
                for (int col = 0; col < 9; col++) {
                    if (cCard.getNumber(row, col) != 0) {
                        params.put("C[" + i + "].N[" + row + "," + col + "]", "" + cCard.getNumber(row, col));
                    } else {
                        params.put("C[" + i + "].N[" + row + "," + col + "]", this.noNumberTag);
                    }
                    String numberStyle = "number-normal";
                    if (cCard.getNumber(row, col) == 0) {
                        numberStyle = "number-empty";
                    }
                    if (cCard.isMatched(row, col)) {
                        numberStyle = "number-marked";
                    }
                    if (cCard.isJolly(row, col) && highlightJolly) {
                        numberStyle = "jolly-cell";
                        if (cCard.isMatched(row, col)) {
                            numberStyle = "jolly-marked";
                        }
                    }
                    // TODO(2.0) Number styles alternative or additive?
                    // TODO(2.0) Use costants instead of literals for better maintenance?
                    params.put("C[" + i + "].Style[" + row + "," + col + "]", numberStyle);
                }
            }
        }
    }

    // Parse the initial part (header) of the template, that is the section that is NOT
    //  repeated at each card block, but printet only at the very beginning of the html file.
    // ${"LibraryName"; "Library Version"; "GENERAL TEMPLATE TYPE NAME"; "Template name"; "Cards x block"}
    private boolean parseTemplateHeader(String hLine) {
        boolean result = false;
        StringTokenizer st = new StringTokenizer(hLine, ";");
        int counter = 0;
        while (st.hasMoreTokens()) {
            String sToken = st.nextToken();
            if ((sToken != null) && sToken.length() > 0) {
                if (sToken.contains(TAG_START)) {
                    // Remove characters preceeding the initial tag
                    sToken = sToken.substring(sToken.indexOf(TAG_START) + 2);
                }
                if (sToken.contains(TAG_END)) {
                    // Remove all characters after the ending tag
                    sToken = sToken.substring(0, sToken.indexOf(TAG_END));
                }
                switch (counter) {
                    case 0:
                        tht.setVersion(sToken);
                        break;
                    case 1:
                        tht.setVersion(tht.getVersion() + "//" + sToken);
                        break;
                    case 3:
                        tht.setName(sToken);
                        break;
                    case 4:
                        tht.setType(THtmlTemplate.THtmlTemplateType.valueOf(sToken));
                        switch (tht.getType()) {
                            case A4_ONECARD_L:
                                tht.setCardsPerBlock(1);
                                break;
                            case A4_TWOCARDS_P:
                                tht.setCardsPerBlock(2);
                                break;
                            case A4_THREECARDS_P:
                                tht.setCardsPerBlock(3);
                            case A4_FOURCARDS_L:
                                tht.setCardsPerBlock(4);
                            case A4_SIXCARDS_P:
                                tht.setCardsPerBlock(6);
                        }
                        result = (tht.getCardsPerBlock() > 0);
                        break;
                    default:
                        break;
                }
                counter++;
            }

        }
        return result;
    }

    // Parse a single line of the template, substituting all tags found in it
    private String parseLine(String line) {
        String parsedLine = line;
        // String parsedLine = new String(line); ???
        StringTokenizer st = new StringTokenizer(parsedLine, "${");
        while (st.hasMoreTokens()) {
            String sToken = st.nextToken();
            if (sToken != null && sToken.contains("}")) {
                sToken = sToken.substring(0, sToken.indexOf("}"));
                String newStr = params.get(sToken);
                if (newStr != null) {
                    parsedLine = parsedLine.replace("${" + sToken + "}", newStr);
                }
            }
        }
        return parsedLine;
    }

}           // End Of File - Rel.(1.1)
