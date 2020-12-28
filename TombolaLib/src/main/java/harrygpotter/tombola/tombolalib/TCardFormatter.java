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

import java.util.Random;
import java.util.StringTokenizer;

/**
 * This class implements a set of methods aimed to convert a TCard objects to
 * serialized text strings, in several different formats, that can be used to be
 * stored in files, databases or transferred over networks.&nbsp;Supported
 * format are mainly based on standard text formats, such as comma separated
 * value (CSV) strings, or JSON or XML.&nbsp;See also
 * {@linkplain TFileFormatter}, the main user of this class as it stores files
 * cards serialized using methods here implemented.
 *
 * @author Harry G. Potter harry.g.potter@gmail.com
 * @version 1.1
 * @see TCard
 * @see TFileFormatter
 * @since 1.8
 */
public class TCardFormatter {

    private static final String NUM_FMT = "%02d";
    private static final String NUM_EXTRACTED_FMT = "*%02d*";
    private static final String JOLLY_FMT = "(%02d)";
    private static final String JOLLY_EXTRACTED_FMT = "!%02d!";

    private String csv_delimiter = ";";
    private String sql_delimiter = ",";
    private String sql_field_delimiter = "'";
    private String string_delimiter = "\"";
    private TCardFormat format = TCardFormat.CSV;
    private boolean useJolly = true;

    /**
     * This is the most versatile constructor for a TCardFormat object.&nbsp;It
     * allows to specify the format to use to serialize cards in strings, if
     * jollyIndex values must be used or not, which delimiter to use between
     * numbers (default: ";") and which delimiter to use to enclose text fields
     * like card labels (default: """).
     *
     * @param format a TCardFormat value specifying the format to use when
     * converting a card in a string
     * @param useJolly true to store in the cards strings the index of the Jolly
     * number, false otherwise
     * @param csvDelimiter specify the separator character to be inserted
     * between numbers and other fields
     * @param textDelimiter specifies the character to be used to enclose text
     * fields in the strings.
     */
    public TCardFormatter(TCardFormat format, boolean useJolly, String csvDelimiter, String textDelimiter) {
        if (format != null) {
            this.format = format;
        }
        this.useJolly = useJolly;
        if (csvDelimiter != null) {
            // A null value is not allowed, a default value is silently provided.
            this.csv_delimiter = csvDelimiter;
        }
        if (textDelimiter != null) {
            // A null value is not allowed, a default value is silently provided.
            this.string_delimiter = textDelimiter;
        }
    }

    /**
     * Constructor for a TCardFormat object, allowing to specify the format to
     * be used use to serialize cards in strings, if jollyIndex values must be
     * used or not, which delimiter to use between numbers (default: ";"). Text
     * fields delimiter is set to its default value (""").
     *
     * @param format a TCardFormat value specifying the format to use when
     * converting a card in a string
     * @param useJolly true to store in the cards strings the index of the Jolly
     * number, false otherwise
     * @param csvDelimiter specify the separator character to be inserted
     * between numbers and other fields
     */
    public TCardFormatter(TCardFormat format, boolean useJolly, String csvDelimiter) {
        this(format, useJolly, csvDelimiter, null);
    }

    /**
     * Constructor for a TCardFormat object, allowing to specify the format to
     * be used use to serialize cards in strings and if jollyIndex values must
     * be used or not. Delimiters to use between numbers and to enclose text
     * fields are set to their default values (";" and """ respectively).
     *
     * @param format a TCardFormat value specifying the format to use when
     * converting a card in a string
     * @param useJolly true to store in the cards strings the index of the Jolly
     * number, false otherwise
     *
     * @see TCardFormatter#TCardFormatter(TCardFormat, boolean, String, String)
     */
    public TCardFormatter(TCardFormat format, boolean useJolly) {
        this(format, useJolly, null, null);
    }

    /**
     * Constructor for a TCardFormat object, allowing to specify just the format
     * to be used use to serialize cards in strings. All other possible
     * parameters are set to their default values.
     *
     * @param format a TCardFormat value specifying the format to use when
     * converting a card in a string
     *
     * @see TCardFormatter#TCardFormatter(TCardFormat, boolean, String, String)
     */
    public TCardFormatter(TCardFormat format) {
        this(format, true, null, null);
    }

    /**
     * Constructor for a TCardFormat object, where all other possible parameters
     * are set to their default values.
     *
     * @see TCardFormatter#TCardFormatter(TCardFormat, boolean, String, String)
     */
    public TCardFormatter() {
    }

    /**
     * Return the format currently configured fir this TCardFormat object
     *
     * @return the format currently configured fir this TCardFormat object
     */
    public TCardFormat getCardFormat() {
        return format;
    }

    /**
     * Allows the change of the format chose to initialize this TCardFormatter
     * object, letting it to be reused to serialize cards in different format.
     *
     * @param format the new desired format for TCards object serialization
     */
    public void changeCardFormat(TCardFormat format) {
        if (format != null) {
            this.format = format;
        }
    }

    /**
     * Return true if the formatter will print symbols to highlight jolly
     * numbers on cards, false otherwise
     *
     * @return true if the formatter will print symbols to highlight jolly
     * numbers on cards, false otherwise
     */
    public boolean isJollyEnabled() {
        return useJolly;
    }

    /**
     * Enable or disable the display of symbols (i.e. asterisks, exclamation
     * marks) to highlights jolly numbers on cards.
     *
     * @param useJolly true to enable the jolly number highlighting, false
     * otherwise
     */
    public void changeUseOfJolly(boolean useJolly) {
        this.useJolly = useJolly;
    }

    /**
     * Return the character(s) used to separate (delimit) fields of cards when
     * they are serialized in text strings. The default delimiter is the ";"
     * semicolon character, as in standard comma separated values (CSV) text
     * files.
     *
     * @return the character(s) used to separate (delimit) fields of cards.
     */
    public String getCsvDelimiter() {
        return this.csv_delimiter;
    }

    /**
     * Set the character you prefer to use to separate fields (numbers, labels,
     * etc.) within the text strings representing serialized TCard
     * objects.&nbsp;Default value is ";".
     *
     * @param csvDelimiter the character(s) you want to use to separate fields
     * within a serialized card
     */
    public void setCsvDelimiter(String csvDelimiter) {
        if (csvDelimiter != null) {
            this.csv_delimiter = csvDelimiter;
        }
    }

    /**
     * Return the character(s) used to delimit textual fields within a
     * serialized card (default value is the " character)
     *
     * @return the character(s) used to delimit textual fields within a
     * serialized card
     */
    public String getTextDelimiter() {
        return this.string_delimiter;
    }

    /**
     * Set the character(s) used to delimit textual fields within a serialized
     * card (default value is the " character)
     *
     * @param textDelimiter the character(s) used to delimit textual fields
     */
    public void setTextDelimiter(String textDelimiter) {
        if (textDelimiter != null) {
            this.string_delimiter = textDelimiter;
        }
    }

    /**
     * Convert a TCard object in a textual representation contained in a string,
     * using the default parameters (format, use of jolly, delimiters) set for
     * this TCardFormat object.
     *
     * @param card the TCard object to be serialized in a string
     * @return a string containing the serialized version of the TCard object
     */
    public String cardToString(TCard card) {
        return cardToString(card, -1, -1, this.format, this.useJolly);
    }

    /**
     * Convert a TCard object in a textual representation contained in a
     * string.&nbsp;Several format are supported, see {@linkplain TCardFormat}
     * values for further information.&nbsp;If the card is participating in a
     * Tombola game, matched numbers are highlighted using asterisks of, for the
     * jolly number, exclamation marks.
     *
     * @param card the TCard object to be serialized in a string
     * @param cardId an integer representing the unique id of the card, used mainly
     *               for SQL format
     * @param cardSetId an integer representing the unique id of the card set 'phater'
     *                  entity, used mainly for SQL format
     * @param format the format to be used to serialize the string
     * @param jollyOn true to highlight on the string the jolly number, false
     * otherwise
     * @return a string containing the serialized version of the TCard object
     * @see TCardFormat
     * @see TCardFormatter#stringToCard(String, TCardFormat)
     */
    public String cardToString(TCard card, int cardId, int cardSetId, TCardFormat format, boolean jollyOn) {
        switch (format) {
            case CSV:
                return csvStringFromCard(card, jollyOn, false);
            case CSV_PLUS:
                return csvStringFromCard(card, jollyOn, true);
            case CSV_PACKED:
                return packedStringFromCard(card, jollyOn, false);
            case CSV_PACKED_PLUS:
                return packedStringFromCard(card, jollyOn, true);
            case PRETTY:
                return prettyStringFromCard(card);
            case SQL:
                return sqlStringFromCard(card, cardId, cardSetId, jollyOn, true);
            case MSWORD_MAILMARGE:
                return csv4WordStringFromCard(card, jollyOn, false);
            //TODO(2.0) XML, JSON, ASCII_ART?...
        }
        return "UNSUPPORTED JET";
    }

    /**
     * Convert a string representing a tombola card to a TCard object, trying to
     * auto-detect its format among the CSV, CSV_PLUS, CSV_PACKED and
     * CSV_PACKED_PLUS.
     *
     * @param cardString the text containing the card serialization to
     * re-convert in a TCard object.
     * @return the TCard object is the text is well-formed, null otherwise
     */
    public TCard stringToCard(String cardString) {
        TCard c = null;
        StringTokenizer st = new StringTokenizer(cardString, csv_delimiter);
        int autoDetect = st.countTokens();
        TCardFormat fmt;
        if (autoDetect == 16 || autoDetect == 17) {
            return stringToCard(cardString, TCardFormat.CSV_PACKED);
        } else if (autoDetect == 18 || autoDetect == 19) {
            return stringToCard(cardString, TCardFormat.CSV_PACKED_PLUS);
        } else if (autoDetect == 28 || autoDetect == 29) {
            return stringToCard(cardString, TCardFormat.CSV);
        } else if (autoDetect == 30 || autoDetect == 31) {
            return stringToCard(cardString, TCardFormat.CSV_PLUS);
        }
        return null;
    }

    /**
     * Convert a string representing a tombola card (see
     * {@linkplain TCardFormatter#cardToString(TCard)}) into a TCard object, NOT
     * considering information related to a Tombola game (i.e.&nbsp;extracted
     * numbers matched on the card).&nbsp;The TCard object is returned as it has
     * just created by a constructor.&nbsp;The input string must be in the
     * TCardFormat passed as second parameter.
     *
     * @param cardString the text containing the card serialization to
     * re-convert in a TCard object.
     * @param format the format used to serialize the card in the cardString
     * text
     * @return the TCard object is the text is well-formed, null otherwise
     */
    public TCard stringToCard(String cardString, TCardFormat format) {
        TCard c = null;
        StringTokenizer st = new StringTokenizer(cardString, csv_delimiter);
        String label = st.nextToken();
        if (label.startsWith(string_delimiter)) {
            label = label.substring(1);
        }
        if (label.endsWith(string_delimiter)) {
            label = label.substring(0, label.length() - 1);
        }
        int[] numbers = new int[15];
        if (format == TCardFormat.CSV_PACKED || format == TCardFormat.CSV_PACKED_PLUS) {
            for (int j = 0; j < 15; j++) {
                numbers[j] = Integer.parseInt(st.nextToken());
            }
        }
        if (format == TCardFormat.CSV || format == TCardFormat.CSV_PLUS) {
            int l = 0;
            for (int j = 0; j < 27; j++) {
                int n = Integer.parseInt(st.nextToken());
                if (n > 0) {
                    numbers[l] = n;
                    l++;
                }
            }
        }
        // Attention!:
        // If there is a number between rounded brackets, it is considered as Jolly index,
        // otherwise Jolly index is set a totally random number to respect the used TCard 
        // constructor contract.
        // MaxEpc and MaxEpr values are never read from files.
        int jollyIndex = -1;
        if (st.countTokens() > 0) {
            String js = st.nextToken();
            if (js.startsWith("(")) {
                js = js.substring(1, js.length() - 1);
                jollyIndex = Integer.parseInt(js);
            }
        }
        if (jollyIndex == -1) {
            jollyIndex = (new Random().nextInt(15));
        }
        return new TCard(label, numbers, jollyIndex, true);
    }

    /**
     * Helper method aimed to prepare an header line containing the labels for
     * the fields arranged in a line by
     * {@linkplain TCardFormatter#cardToString(TCard, TCardFormat)}
     * method.&nbsp;It is mainly used by {
     *
     * @linplain TFileFormatter} methods to insert an header on top of produced
     * csv files containing set of series of cards.
     *
     * @param format the format determining which fields and in which orders
     * must be in the header
     * @return the header string
     * @see TFileUtils#writeCardFile(String, TSeriesList, TFileFormat)
     */
    String prepareHeader(TCardFormat format) {
        StringBuilder sb = new StringBuilder();
        sb.append("CardLabel").append(csv_delimiter);
        if (format == TCardFormat.CSV || format == TCardFormat.CSV_PLUS || format == TCardFormat.MSWORD_MAILMARGE) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 9; j++) {
                    sb.append("NumR").append(i).append("C").append(j).append(csv_delimiter);
                }
            }
        } else if (format == TCardFormat.CSV_PACKED || format == TCardFormat.CSV_PACKED_PLUS) {
            for (int i = 0; i < 15; i++) {
                sb.append("Num").append(i).append(csv_delimiter);
            }
        }
        if (this.useJolly) {
            if (format == TCardFormat.MSWORD_MAILMARGE) {
                sb.append("JollyNumber").append(csv_delimiter);
            } else {
                sb.append("JollyIndex").append(csv_delimiter);
            }
        }
        if (format == TCardFormat.CSV_PLUS || format == TCardFormat.CSV_PACKED_PLUS) {
            sb.append("MaxEqualPerCard").append(csv_delimiter);
            sb.append("MaxEqualPerRow").append(csv_delimiter);
        }
        return sb.toString();
    }

    // Private methods zone...
    // ------------------------------------------------------------------------------------------
    private String csvStringFromCard(TCard card, boolean jollyOn, boolean extraInfo) {
        if (card == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(string_delimiter).append(card.getLabel()).append(string_delimiter).append(csv_delimiter);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                String formatStr = NUM_FMT + csv_delimiter;
                if (card.getExtractionCheckCount() > 0) {
                    if (card.isMatched(card.getNumber(i, j))) {
                        formatStr = NUM_EXTRACTED_FMT + csv_delimiter;
                        if (card.isJolly(i, j) && this.useJolly) {
                            formatStr = JOLLY_EXTRACTED_FMT + csv_delimiter;
                        }
                    } else {
                        if (card.getLinearIndex(i, j) == card.getJollyIndex() && this.useJolly) {
                            formatStr = JOLLY_FMT + csv_delimiter;
                        }
                    }
                }
                sb.append(String.format(formatStr, card.getNumber(i, j)));
            }
        }
        if (jollyOn) {
            sb.append(String.format(JOLLY_FMT, card.getJollyIndex())).append(csv_delimiter);
        }
        if (extraInfo) {
            sb.append(String.format(NUM_FMT, card.getCurrentMaxEPC())).append(csv_delimiter);
            sb.append(String.format(NUM_FMT, card.getCurrentMaxEPR())).append(csv_delimiter);
        }
        return sb.toString();
    }

    private String packedStringFromCard(TCard card, boolean jollyOn, boolean extraInfo) {
        if (card == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(string_delimiter).append(card.getLabel()).append(string_delimiter).append(csv_delimiter);
        for (int i = 0; i < 15; i++) {
            String formatStr = NUM_FMT + csv_delimiter;
            if (card.getExtractionCheckCount() > 0) {
                if (card.isMatched(card.getNumber(i))) {
                    formatStr = NUM_EXTRACTED_FMT + csv_delimiter;
                    if (i == card.getJollyIndex() && this.useJolly) {
                        formatStr = JOLLY_EXTRACTED_FMT + csv_delimiter;
                    }
                } else {
                    if (i == card.getJollyIndex() && this.useJolly) {
                        formatStr = JOLLY_FMT + csv_delimiter;
                    }
                }
            }
            sb.append(String.format(formatStr, card.getNumber(i)));
        }
        if (jollyOn) {
            sb.append(String.format(JOLLY_FMT, card.getJollyIndex())).append(csv_delimiter);
        }
        if (extraInfo) {
            sb.append(String.format(NUM_FMT, card.getCurrentMaxEPC())).append(csv_delimiter);
            sb.append(String.format(NUM_FMT, card.getCurrentMaxEPR())).append(csv_delimiter);
        }
        return sb.toString();
    }

    private String sqlStringFromCard(TCard card, int cardId, int cardSetId, boolean jollyOn, boolean extraInfo) {
        if (card == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        sb.append("INSERT INTO `<db_name>`.`cards` (`CardID`, `Label`, `CardSetID`, `NumberString`, `Status`, `Checksum`, `MaxEPC`, `MAxEPR` ) VALUES (");
        sb.append(sql_field_delimiter).append(cardId).append(sql_field_delimiter).append(sql_delimiter).append(" ");
        sb.append(sql_field_delimiter).append(card.getLabel()).append(sql_field_delimiter).append(sql_delimiter).append(" ");
        sb.append(sql_field_delimiter).append(cardSetId).append(sql_field_delimiter).append(sql_delimiter).append(" ");
        String numbers = "";
        for (int i = 0; i < 15; i++) {
            numbers += card.getNumber(i) + csv_delimiter;
        }
        sb.append(sql_field_delimiter).append(numbers).append(sql_field_delimiter).append(sql_delimiter).append(" ");
        sb.append(sql_field_delimiter).append("0").append(sql_field_delimiter).append(sql_delimiter).append(" ");
        sb.append(sql_field_delimiter).append(card.evaluateCheckSum(0)).append(sql_field_delimiter).append(sql_delimiter).append(" ");
        sb.append(sql_field_delimiter).append(card.getCurrentMaxEPC()).append(sql_field_delimiter).append(sql_delimiter).append(" ");
        sb.append(sql_field_delimiter).append(card.getCurrentMaxEPR()).append(sql_field_delimiter);
        // Tra le altre cose manca la creation date
        sb.append(");");
        return sb.toString();
    }

    private String csv4WordStringFromCard(TCard card, boolean jollyOn, boolean extraInfo) {
        if (card == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(string_delimiter).append(card.getLabel()).append(string_delimiter).append(csv_delimiter);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                String formatStr = NUM_FMT + csv_delimiter;
                if (card.getExtractionCheckCount() > 0) {
                    if (card.isMatched(card.getNumber(i, j))) {
                        formatStr = NUM_EXTRACTED_FMT + csv_delimiter;
                        if (card.isJolly(i, j) && this.useJolly) {
                            formatStr = JOLLY_EXTRACTED_FMT + csv_delimiter;
                        }
                    } else {
                        if (card.getLinearIndex(i, j) == card.getJollyIndex() && this.useJolly) {
                            formatStr = JOLLY_FMT + csv_delimiter;
                        }
                    }
                }
                sb.append(String.format(formatStr, card.getNumber(i, j)));
            }
        }
        if (jollyOn) {
            sb.append(String.format(NUM_FMT, card.getNumber(card.getJollyIndex()))).append(csv_delimiter);
        }
        if (extraInfo) {
            sb.append(String.format(NUM_FMT, card.getCurrentMaxEPC())).append(csv_delimiter);
            sb.append(String.format(NUM_FMT, card.getCurrentMaxEPR())).append(csv_delimiter);
        }
        return sb.toString();
    }

    private String prettyStringFromCard(TCard card) {
        if (card == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        // The idea is to print only card numbers, without the label that can be gotten and printed 
        //  before by the TombolaLib user.
        // Rounded brakets for jolly number not extracted
        // Aterisks for extracted numbers, exclamation marks for extracted jolly number.
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                String formatStr = " %2d ";
                if (card.isJolly(i, j) && this.useJolly) {
                    formatStr = "(%2d)";
                }
                if (card.getExtractionCheckCount() > 0) {
                    if (card.isMatched(card.getNumber(i, j))) {
                        formatStr = "*%2d*";
                        if (card.isJolly(i, j) && this.useJolly) {
                            formatStr = "!%2d!";
                        }
                    }
                }
                if (card.getNumber(i, j) > 0) {
                    sb.append(String.format(formatStr, card.getNumber(i, j))).append(" ");
                } else {
                    sb.append("     ");
                }
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    // TODO(2.0) Add the jolly number management to this method
    private String tinyStringFromCard(TCard c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 15; i++) {
            sb.append(String.format(" %2d;", c.getNumber(i)));
            if ((i + 1) % 5 == 0 && i != 14) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    // TODO(2.0) Add the jolly number management to this method
    private String tinySpacedStringFromCard(TCard c) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                sb.append(String.format(" %2d;", c.getNumber(i, j)));
            }
            if (i != 2) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
// TODO(2.0) Substitute resource sparse literals within the code with resource bundle or constant strings initialized all in the same place
// TODO(2.0) Review the order of the methods in the class.
// TODO(2.0) Check comments' coherence.
// TODO(2.0) Complete formats support (i.e. TINY, etc.).

}           // End Of File - Rel.(1.1)
