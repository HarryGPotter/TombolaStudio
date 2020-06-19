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

import harrygpotter.tombola.tombolalib.ILogger;
import harrygpotter.tombola.tombolalib.ISetFactory;
import harrygpotter.tombola.tombolalib.TCardFormat;
import harrygpotter.tombola.tombolalib.TSeriesList;
import harrygpotter.tombola.tombolalib.TUtils;
import java.util.Iterator;
import java.util.StringTokenizer;

/**
 * This class, used when TombolaCards is in interactive mode, implements the
 * "SET" command allowing the user to change the value of environments variables
 * and parameters used to generate series of cards.
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTCommandSet extends TTAbstractCommand {

    /**
     * Execute the SET command, enabling the user to change values for a variety
     * of parameters conditioning the series of cards generation process and the
     * output produce in card files.
     *
     * @param st tokenizer containing the remaining part of the command line,
     * enabling the retrieval of optional command parameters
     *
     * @return an integer indicating the outcome of the execution. SET returns 0
     * if a parameter value has been effectively changed, -1 if all parameter
     * names are displayed, -2 if a parameter value is just displayed and not
     * changed, -3 is the factory is in running mode, thus no parameter changes
     * are allowed,-5 if there are an error parsing the specified value for a
     * valid parameter.
     */
    @Override
    public int execute(StringTokenizer st) {
        String[] params = this.parseParameter(st);
        ISetFactory isf = (ISetFactory) this.internals.get("setFactory");

        if (params.length < 1) {
            echo("Use SET command to change the value of a parameter.\n");
            echo("Use ENV command to display names and values for TombolaCardavailable parameters.\n");
            
            System.out.print("\n Available parameters:\n\n  ");
            int i=0;
            Iterator<String> keys = envMap.keySet().iterator();
            while (keys.hasNext()) {
                System.out.print(keys.next() + ", ");
                if (++i %5 == 0)
                    System.out.print("\n  ");
            }
            System.out.print("\n");
            sResult = "";
            return -1;
        } else if (params.length < 2) {
            String pName = params[0];
            boolean found = false;
            for(String k: envMap.keySet()) {
                if (k.toLowerCase().startsWith(pName.toLowerCase())) {
                    echo(String.format("Current value for %s is %s.\n", k, this.envMap.get(k)));
                    found = true;
                }
            }
            if (found) {
                sResult = ("Type SET <ParamName> <NewParamValue> to change its current value.");
            } else {
                sResult = ("Parameter ["+pName+"] NOT recognized. Use ENV command to display names and values for all environment parameters.");
            }
            return -2;
        } else if (isf != null && (isf.getStatus() == ISetFactory.TStatus.RUNNING || isf.getStatus() == ISetFactory.TStatus.STOPPING)) {
            sResult = "<WARNING> You cannot chage parameters while series generation is running. Please, STOP it before any changes.";
            return -3;
        } else {
            String sParam = params[0].toUpperCase();
            String sNewValue = params[1];
            sResult = "";
            int iTemp;
            long lTemp;
            switch (sParam) {
                case "AVOIDEMPTYCOLUMN":
                    this.envMap.put("avoidEmptyColumn", Boolean.valueOf(sNewValue));
                    sResult = "<OK!> avoidEmptyColumn set to " + this.envMap.get("avoidEmptyColumn");
                    break;
                case "CARDLABELCHECKSUM":
                    this.envMap.put("cardLabelChecksum", Boolean.valueOf(sNewValue));
                    sResult = "<OK!> cardLabelChecksum set to " + this.envMap.get("cardLabelChecksum");
                    break;
                case "CARDLABELMODE":
                    try {
                        this.envMap.put("cardLabelMode", TSeriesList.TLabelingModes.valueOf(sNewValue));
                        sResult = "<OK!> cardLabelMode set to " + this.envMap.get("cardLabelMode");
                    } catch (IllegalArgumentException iaex) {
                        sResult = "<ERROR!> cardLabelMode '"+ sNewValue +"' NOT recognized. Current value NOT changed.";
                    }
                    break;
                case "SERIES":
                case "DESIRED":
                case "NUMSERIES":
                case "DESIREDSERIES":
                    iTemp = Integer.parseInt(sNewValue);
                    if (iTemp < 1 || iTemp > TUtils.MAX_SERIES) {
                        sResult = "<ERROR> DesiredSeries value must be included in [1, " + TUtils.MAX_SERIES + "] range.";
                        return -5;
                    }
                    if (iTemp < ((TSeriesList) internals.get("seriesList")).size()) {
                        sResult = "<WARNING> There are already " + ((TSeriesList) internals.get("seriesList")).size() + " in memory. Desired series value has NOT been modified.";
                        return -5;
                    }
                    this.envMap.put("desiredSeries", iTemp);
                    sResult = "<OK!> Desired Number of Series has been set to "
                            + (int) this.envMap.get("desiredSeries") + " ("
                            + (int) this.envMap.get("desiredSeries") * 6 + " cards).";
                    break;
                case "MAXEPC":
                case "EPC":
                    iTemp = Integer.parseInt(sNewValue);
                    if (iTemp<ISetFactory.MINIMUM_MAXEPC || iTemp> 15) {
                        sResult = String.format("<ERROR> Max Equal Number between Cards must be within the [%d, 15] range.", ISetFactory.MINIMUM_MAXEPC);
                        return -5;
                    }
                    this.envMap.put("maxepc", iTemp);
                    sResult = "<OK!> maxepc set to " + this.envMap.get("maxepc");
                    break;
                case "MAXEPR":
                case "EPR":
                    iTemp = Integer.parseInt(sNewValue);
                    if (iTemp<ISetFactory.MINIMUM_MAXEPR || iTemp> 5) {
                        sResult = String.format("<ERROR> Max Equal Number between card Rows must be within the [%d, 5] range.", ISetFactory.MINIMUM_MAXEPR);
                        return -5;
                    }
                    this.envMap.put("maxepr", iTemp);
                    sResult = "<OK!> maxepr set to " + this.envMap.get("maxepr");
                    break;
                case "TIMELIMIT":
                    try {
                        lTemp = Long.parseLong(sNewValue);
                        if ( lTemp < 5000 ) {
                            sResult = String.format("<ERROR> Time Limit for card generation must be at least %,d milliseconds or above.", 5000);
                            return -5;
                        }
                        this.envMap.put("timeLimit", lTemp);
                        sResult = String.format("<DONE!> timeLimit set to %,d milliseconds (%s).", this.envMap.get("timeLimit"), TUtils.prettyMilliseconds(lTemp));
                    } catch (NumberFormatException nfe) {
                        sResult = "<ERROR!> '"+ sNewValue +"' NOT recognized as a valid number for timeLimit. Current value NOT changed.";
                    }
                    break;
                case "ITERATIONLIMIT":
                    try {
                        lTemp = Long.parseLong(sNewValue);
                        if ( lTemp < 100000 ) {
                            sResult = String.format("<ERROR> Iteraction Limit for card generation must be at least %,d or above.", 100000);
                            return -5;
                        }
                        this.envMap.put("iteractionLimit", lTemp);
                        sResult = String.format("<OK!> iteractionLimit set to %,d.", this.envMap.get("maxepr"));
                    } catch (NumberFormatException nfe) {
                        sResult = "<ERROR!> '"+ sNewValue +"' NOT recognized as a valid number for iteractionLimit. Current value NOT changed.";
                    }
                    break;
                case "RANDOMSEED":
                    try {
                        lTemp = Long.parseLong(sNewValue);
                        this.envMap.put("randomSeed", lTemp);
                        sResult = String.format("<OK!> randomSeed set to %,d.", this.envMap.get("maxepr"));
                    } catch (NumberFormatException nfe) {
                        sResult = "<ERROR!> '"+ sNewValue +"' NOT recognized as a valid number for randomSeed. Current value NOT changed.";
                    }
                    break;
                case "METHOD":
                    boolean flagChanged = false;
                    String currentMethod = (String) this.envMap.get("method");
                    if (!currentMethod.equalsIgnoreCase(sNewValue)) {
                        for (String m : TUtils.AVAILABLE_GENERATION_METHODS) {
                            if (sNewValue.equalsIgnoreCase(m)) {
                                flagChanged = true;
                                this.envMap.put("method", m);
                                sResult = "<!> Set Factory method changed to [" + m + "].";
                            }
                        }
                        if (!flagChanged) {
                            sResult = "<!> Set Factory method NOT changed because [" + sNewValue.toUpperCase() + "] is not regocnized as a supported algorithm.";
                            return -5;
                        } else {
                            if (isf != null) {
                                isf = TUtils.getSetFactoryByType((String)envMap.get("method"));
                                this.internals.put("setFactory", isf);
                                ILogger logger = (ILogger) internals.get("logger");
                                logger.info("Series Set Factory just created [" + isf.getClass().getSimpleName()+"]");                                
                            }
                        }
                    } else {
                        sResult = "<!> No changes, set Factory method already set to [" + sNewValue.toUpperCase() + "].";
                    }
                    break;
                case "CARDLABELPREFIX":
                    // TODO(1.2) May be there are some other controls to add...
                    this.envMap.put("cardLabelPrefix", sNewValue.trim());
                    sResult = "<OK!> cardLabelPrefix set to " + this.envMap.get("cardLabelPrefix");
                    break;
                case "CARDLABELSEPARATOR":
                    this.envMap.put("cardLabelSeparator", sNewValue.trim());
                    sResult = "<OK!> cardLabelSeparator set to " + this.envMap.get("cardLabelSeparator");
                    break;
                case "FILEFORMAT":
                    TCardFormat tcf = TCardFormat.valueOf(sNewValue);
                    if (tcf != TCardFormat.AUTO && tcf != TCardFormat.JSON &&
                            tcf != TCardFormat.PRETTY &&
                            tcf != TCardFormat.TINY && tcf != TCardFormat.TINY_SPACED) {
                        this.envMap.put("fileFormat", TCardFormat.valueOf(sNewValue));
                        sResult = "<OK!> fileFormat set to " + this.envMap.get("fileFormat");                
                    } else {
                        sResult = "<ERROR!> FileFormat ["+sNewValue+"] not supported jet.";                
                    }
                    break;
                case "FILENAME":
                    this.envMap.put("fileName", sNewValue.trim());
                    sResult = "<OK!> fileName set to " + this.envMap.get("fileName");
                    break;
                case "O":
                case "FILEOVERWRITE":
                    this.envMap.put("fileOverwrite", Boolean.valueOf(sNewValue));
                    sResult = "<OK!> fileOverwrite set to " + this.envMap.get("fileOverwrite");
                    break;
                case "LOG":
                case "TRACELOGFILENAME":
                    this.envMap.put("traceLogFileName", sNewValue.trim());
                    sResult = "<OK!> traceLogFileName set to " + this.envMap.get("traceLogFileName");
                    break;
                case "U":
                case "UNATTENDED":
                    this.envMap.put("unattended", Boolean.valueOf(sNewValue));
                    sResult = "<OK!> unattended set to " + this.envMap.get("unattended");
                    break;
                case "USEJOLLY":
                    this.envMap.put("useJolly", Boolean.valueOf(sNewValue));
                    sResult = "<OK!> useJolly set to " + this.envMap.get("useJolly");
                    break;
                case "V":
                case "VERBOSE":
                    this.envMap.put("verbose", Boolean.valueOf(sNewValue));
                    sResult = "<OK!> verbose set to " + this.envMap.get("verbose");
                    break;
                default:
                    echo("<ERROR> Parameter ["+sParam+"] NOT Recognized.\n");
            }
        }
        return 0;
    }
}