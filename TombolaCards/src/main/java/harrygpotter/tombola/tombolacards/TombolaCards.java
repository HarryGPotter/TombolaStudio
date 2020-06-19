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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import harrygpotter.tombola.tombolacards.interactive.ICommand;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.TreeMap;
import harrygpotter.tombola.tombolalib.ILogger;
import harrygpotter.tombola.tombolalib.ISetFactory;
import harrygpotter.tombola.tombolalib.TCardFormat;
import harrygpotter.tombola.tombolalib.TFileFormatter;
import harrygpotter.tombola.tombolalib.TSimpleLogger;
import harrygpotter.tombola.tombolalib.TMakeSix;
import harrygpotter.tombola.tombolalib.TSeriesList;
import harrygpotter.tombola.tombolalib.TUtils;
import harrygpotter.tombola.tombolacards.interactive.*;
import java.util.Arrays;

/**
 * TombolaCards - A Command Line Tool that you'll love from the first time you'll
 * use it! :-DDD
 * Whit this tool you'll be able to <b>generate lists of series of cards</b> trying to improve their
 * "game-ability", that is trying to avoid too similar cards and therefore reducing
 * the probability of concurrent-winds during a Tombola match.&nbsp;You will also be able
 * to save these lists of series of cards on a text files, re-elaborate them, and so on.&nbsp;
 * TombolaCards: a Swiss Army knife to manage all your tombola cards!
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TombolaCards {

    private static final String TT_VERSION = "1.1";
    private static final String TT_DEFAULT_OUTPUT_FILENAME = "CardSeries001.csv";
    //TODO(2.0) Still not used: private static final String TT_DEFAULT_CONFIG_FILE = "./conf/tombolaCards.cfg";

    private Map<String, Object> environment;
    private Map<String, Object> internals;
    private Map<String, ICommand> commands;


    /**
     * TombolaCards - A Command Line Tool that you'll love from the first time you'll use it!
     * This is just the main method, the entry point for this tool.
     *
     * @param args the command line arguments. Considering that this is a command line tool...
     * command line arguments are very important! See {@linkplain TombolaCards#printInfo(Map)}
     * for much more details.
     */
    public static void main(String[] args) {

        Map<String, Object> envMap = new TreeMap<>();
        Map<String, Object> internalMap = new TreeMap<>();

        // 0. Prepare the "environment variable map" with the 'code-level-default' for TombolaCards
        envMap.put("fileName", "CardSeries001.csv");
        envMap.put("fileFormat", TCardFormat.CSV_PLUS);
        envMap.put("desiredSeries", 10);
        envMap.put("method", TUtils.AVAILABLE_GENERATION_METHODS[0]);
        envMap.put("maxepc", ISetFactory.MINIMUM_MAXEPC + 4 );            // TODO(2.0) how to improve?
        envMap.put("maxepr", ISetFactory.MINIMUM_MAXEPR + 3 );            // TODO(2.0) how to improve?
        envMap.put("avoidEmptyColumn", true);
        envMap.put("useJolly", true);
        envMap.put("verbose", false);
        envMap.put("unattended", false);
        envMap.put("fileOverwrite", false);
        envMap.put("cardLabelPrefix", "TT");
        envMap.put("cardLabelChecksum", true);
        envMap.put("cardLabelSeparator", "-");
        envMap.put("cardLabelMode", TSeriesList.TLabelingModes.BYCARDS);
        //envMap.put("traceLogFileName", TSimpleLogger.NULL_LOGGER);
        envMap.put("traceLogFileName", "TombolaCards.log");
        envMap.put("defaultSeriesTitle", "CardSeries001");
        envMap.put("randomSeed", null);
        envMap.put("timeLimit", ISetFactory.MAX_ITERATIONS_MILLISECS);
        envMap.put("iteractionLimit", ISetFactory.MAX_ITERATIONS);

        internalMap.put("prompt", ">>");
        internalMap.put("rPrompt", "<<");
        internalMap.put("interactive", false);
        internalMap.put("notifyConclusion",false);
        internalMap.put("unsavedWork", false);
        // TODO(1.1): Please, review this hook and its usage
        Runtime.getRuntime().addShutdownHook(new TShutdownHook(envMap, internalMap));

        // Program invoked with no command line parameter.
        // Header and Help info are printed, then TombolaCards ends.
        if (args.length < 1) {
            printInitialBanner();
            printInfo(envMap);
            System.exit(0);
        }

        // Command line is not empty
        // 1. Command line parameters parsing
        boolean numberFound = false;
        int tempValue = -1;
        for (String arg : args) {
            boolean flag1 = false;
            if (!numberFound) {
                try {
                    tempValue = Integer.parseInt(arg);
                    if (tempValue < 1 || tempValue > TUtils.MAX_SERIES) {
                        System.err.println(String.format("<FATAL!>  You can generate from a minumum of %d to a maximum of %d series (%d cards).", 1, TUtils.MAX_SERIES, TUtils.MAX_SERIES * 6));
                        System.exit(-1);
                    }
                    numberFound = true;
                    flag1 = true;
                    envMap.put("desiredSeries", tempValue);
                } catch (NumberFormatException nfe) {
                    // Nothing to do, it is not a problem.
                }
            }
            if (!flag1) {   // Current arg value in the loop is not a number
                if (arg.length() < 2 || arg.charAt(0) != '-') {
                    System.err.println("<FATAL!> Parameter [" + arg + "] not recognized. Run TombolaCards with no parameters to see the help.");
                    System.exit(-1);
                }
                arg = arg.substring(1);
                String argArg = null;
                if (arg.contains("=")) {
                    argArg = arg.substring(arg.indexOf("=") + 1);
                    arg = arg.substring(0, arg.indexOf("="));
                }
                arg = arg.toUpperCase();
                switch (arg) {
                    case "E":
                        if (argArg.equalsIgnoreCase("RANDOM")) {
                            envMap.put("method", TUtils.AVAILABLE_GENERATION_METHODS[0]);
                        } else if (argArg.equalsIgnoreCase("PROGRESSIVE")) {
                            envMap.put("method", TUtils.AVAILABLE_GENERATION_METHODS[1]);
                        } else {
                            System.err.println("<FATAL!> Euristic method [" + argArg + "] not recognized. Run TombolaCards with no parameters to see the help.");
                            System.exit(-1);
                        }
                        break;
                    case "F":
                        envMap.put("fileFormat", TCardFormat.valueOf(argArg));
                        break;
                    case "H":
                        TombolaCards.printInfo(envMap);
                        System.exit(0);
                        break;
                    case "I":
                        internalMap.put("interactive", true);
                        break;
                    case "J":
                        if (argArg != null && argArg.equalsIgnoreCase("OFF")) {
                            envMap.put("useJolly", false);
                        }
                        break;
                    case "L":
                        if (argArg != null) {
                            envMap.put("cardLabelPrefix", argArg);
                        }
                        break;
                    case "LN":
                        if (argArg != null) {
                            envMap.put("cardLabelMode", TSeriesList.TLabelingModes.valueOf("BY"+argArg));
                        }
                        break;
                    case "LS":
                        if (argArg!=null) {
                            envMap.put("cardLabelSeparator", argArg);
                        } else {
                            envMap.put("cardLabelSeparator", "");
                        }
                        break;
                    case "LK":
                        if (argArg != null && argArg.equalsIgnoreCase("OFF")) {
                            envMap.put("cardLabelChecksum", false);
                        }
                        break;
                    case "MPC":
                        try {
                            int tempMpc = Integer.parseInt(argArg);
                            if (tempMpc < ISetFactory.MINIMUM_MAXEPC) {
                                System.err.println("<FATAL!> Max Equal number allowed per Card cannot be lesser than " + ISetFactory.MINIMUM_MAXEPC);
                                System.exit(-1);
                            }
                            envMap.put("maxepc", tempMpc);
                        } catch (NumberFormatException nfe) {
                            System.err.println("<FATAL!> Max Equal number allowed per Card not recognized [" + argArg + "]");
                            System.exit(-1);
                        }
                        break;
                    case "MPR":
                        try {
                            int tempMpr = Integer.parseInt(argArg);
                            if (tempMpr < ISetFactory.MINIMUM_MAXEPR) {
                                System.err.println("<FATAL!> Max Equal number allowed per Row cannot be lesser than " + ISetFactory.MINIMUM_MAXEPR);
                                System.exit(-1);
                            }
                            envMap.put("maxepr", tempMpr);
                        } catch (NumberFormatException nfe) {
                            System.err.println("<FATAL!> Max Equal number allowed per Row not recognized [" + argArg + "]");
                            System.exit(-1);
                        }
                        break;
                    case "O":
                        envMap.put("fileName", argArg);
                        break;
                    case "R":
                        envMap.put("inputFile", argArg);
                        break;
                    case "S":
                        if (argArg!=null && argArg.length()>0) {
                            try {
                                long seed = Long.parseLong(argArg);
                                envMap.put("randomSeed", seed);
                            } catch (NumberFormatException nfe) {
                                System.err.println("<FATAL!> Seed value for random number generator not recognized [" + argArg + "]");
                                System.exit(-1);
                            }                            
                        } else {
                            envMap.put("randomSeed", null);
                        }
                        break;
                    case "T":
                        if (argArg!=null && argArg.length()>0) {
                            envMap.put("traceLogFileName", argArg);
                        } else {
                            envMap.put("traceLogFileName", TSimpleLogger.NULL_LOGGER);
                        }
                        break;
                    case "Q":
                        if (argArg != null && argArg.equalsIgnoreCase("OFF")) {
                            envMap.put("avoidEmptyColumn", false);
                        }
                        break;
                    case "U":
                        envMap.put("unattended", true);
                        break;
                    case "V":
                        envMap.put("verbose", true);
                        break;
                    case "W":
                        envMap.put("fileOverwrite", true);
                        break;
                    default:
                        System.err.println("<FATAL!> Parameter [" + arg + "] not recognized. Run TombolaCards with no parameters to see the help.");
                        System.exit(-1);
                }
            }
        }   // End of for loop to parse parameters

        //Some initialization "special cases". If the heuristic is pure random, maepc and maxepr values are not considered.
        if (((String) envMap.get("method")).equalsIgnoreCase(TUtils.AVAILABLE_GENERATION_METHODS[0])) {
            envMap.put("maxepc", 15);
            envMap.put("maxepr", 5);
        }

        // Some initialization echo to the screen. Maybe this can be moved in the executeBacth()/executeInteractive() methods.
        if (!(boolean) envMap.get("unattended")) {
            TombolaCards.printInitialBanner();
            if ((boolean) envMap.get("verbose")) {
                System.out.println("\n Current environment parameters:");
                for (String key : envMap.keySet()) {
                    int iPadding = 21 - key.length();
                    char[] cPadding = new char[iPadding];
                    Arrays.fill(cPadding, ' ');
                    System.out.println("   " + key + ": " + new String(cPadding) + envMap.get(key));
                }
                System.out.println();
            }
        }

        // If there is a file to read, it is read here, so the task is accomplished before entering
        //  either batch or interactive mode
        TSeriesList tsl = null;

        boolean mustGenerate = true;
        String inputFile = (String) envMap.get("inputFile");
        if (inputFile != null) {
            TFileFormatter tff = new TFileFormatter();
            try {
                tsl = tff.readSeriesFile(inputFile, TCardFormat.AUTO);
                int toGo = (int) envMap.get("desiredSeries");
                if (tsl != null) {
                    toGo -= tsl.size();
                    mustGenerate = (toGo > 0);
                }
                if (!(boolean) envMap.get("unattended")) {
                    if (tsl != null && tsl.size() > 0) {
                        System.out.println(String.format("Just read %d series (%d cards) from [%s] file.", tsl.size(), tsl.size() * 6, inputFile));
                    }
                    if (mustGenerate) {
                        System.out.printf("There are still %d series (%d cards) to generate.%n", toGo, toGo * 6);
                    } else {
                        System.out.println("Nothing more to generate.");
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }   // End of if inputFile != null

        //TODO(1.2) Improve the management of the name of the whole series list.
        if (tsl==null) {
            tsl = new TSeriesList("Series list freshly generated by TombolaCards");
        }
        internalMap.put("seriesList", tsl); // may be null or not null, but it is always inserted.
        internalMap.put("mustGenerate", mustGenerate); // it is always inserted.

        TombolaCards tt = new TombolaCards();
        tt.initialize(envMap, internalMap);

        if ((Boolean) internalMap.get("interactive")) {
            tt.enterInteractiveMode();
        } else {
            tt.executeBatch();
        }
        if (!(boolean) envMap.get("unattended")) {
            TombolaCards.printFinalBanner();
        }
    }   // End of main()

    // ----------------------------------------------------------------------
    private void initialize(Map<String, Object> env, Map<String, Object> intMap) {
        this.environment = env;
        this.internals = intMap;
        String logName = (String)environment.get("traceLogFileName");

        ILogger logger = TSimpleLogger.getLoggerByName(logName);
        if (logger==null) {
            try {
                TSimpleLogger.prepareLogger(logName, ILogger.TLogLevel.VER, logName);
                logger = TSimpleLogger.getLoggerByName(logName);
            } catch (FileNotFoundException fnfe) {
                System.err.println("<ERROR> Impossible to prepare the trace log file. No trace log will be generated.");
                logger = TSimpleLogger.getLoggerByName(TSimpleLogger.NULL_LOGGER);
            }
        }
        logger.verbose("Today is a good day. Trace Logger for TombolaCards run has been properly created.");
        intMap.put("logger", logger);

        if (!(boolean)internals.get("interactive")) {
            logger.info("Entering the batch execution mode.");
        } else {
            logger.info("Entering the interactive execution mode.");

            TTCommandDelete cmdDel= new TTCommandDelete();
            TTCommandEnv cmdEnv = new TTCommandEnv();
            TTCommandExit cmdExit = new TTCommandExit();
            TTCommandHelp cmdHelp = new TTCommandHelp();
            TTCommandLabel cmdLabel = new TTCommandLabel();
            TTCommandRead cmdRead = new TTCommandRead();
            TTCommandReset cmdReset = new TTCommandReset();
            TTCommandRun cmdRun = new TTCommandRun();
            TTCommandSave cmdSave = new TTCommandSave();
            TTCommandSet cmdSet = new TTCommandSet();
            TTCommandShow cmdShow = new TTCommandShow();
            TTCommandSort cmdSort = new TTCommandSort();
            TTCommandStats cmdStats = new TTCommandStats();
            TTCommandStatus cmdStatus = new TTCommandStatus();
            TTCommandStop cmdStop = new TTCommandStop();

            cmdDel.setEnvironment(environment, internals);
            cmdEnv.setEnvironment(environment, internals);
            cmdExit.setEnvironment(environment, internals);
            cmdHelp.setEnvironment(environment, internals);
            cmdLabel.setEnvironment(environment, internals);
            cmdRead.setEnvironment(environment, internals);
            cmdReset.setEnvironment(environment, internals);
            cmdRun.setEnvironment(environment, internals);
            cmdSave.setEnvironment(environment, internals);
            cmdSet.setEnvironment(environment, internals);
            cmdShow.setEnvironment(environment, internals);
            cmdSort.setEnvironment(environment, internals);
            cmdStats.setEnvironment(environment, internals);
            cmdStatus.setEnvironment(environment, internals);
            cmdStop.setEnvironment(environment, internals);

            this.commands = new TreeMap<>();
            this.commands.put("CLS", new TTCommandCls());       // Good
            this.commands.put("DEL", cmdDel);
            this.commands.put("DELETE", cmdDel);
            this.commands.put("ENV", cmdEnv);
            this.commands.put("EXIT", cmdExit);                 // Good
            this.commands.put("QUIT", cmdExit);                 // Good
            this.commands.put("HELP", cmdHelp);
            this.commands.put("?", cmdHelp);
            this.commands.put("LABEL", cmdLabel);
            this.commands.put("LOAD", cmdRead);
            this.commands.put("READ", cmdRead);
            this.commands.put("RESET", cmdReset);
            this.commands.put("RUN", cmdRun);
            this.commands.put("SAVE", cmdSave);
            this.commands.put("SET", cmdSet);
            this.commands.put("SHOW", cmdShow);            
            //this.commands.put("SORT", cmdSort);
            //this.commands.put("STATS", cmdStats);            
            this.commands.put("STATUS", cmdStatus);
            this.commands.put("STOP", cmdStop);
            this.commands.put("PAUSE", cmdStop);

            // TODO(1.2) Are these thing really needed?
            // STATS?
            // this.internals.put("Commands", commands);
        }
    }

    // ----------------------------------------------------------------------
    private void executeBatch() {
        boolean unattended = (boolean) environment.get("unattended");
        boolean verbose = (boolean) environment.get("verbose");
        boolean mustGenerate = (boolean) internals.get("mustGenerate");
        boolean overWrite = (boolean) environment.get("fileOverwrite");
        if (!overWrite) {
            File f = new File((String)environment.get("fileName"));
            if (f.exists() && ! f.isDirectory()) {
                if (!unattended) {
                    System.err.printf("<ERROR!> Chosen output file [%s] already exist. Change name or use option -W to overwrite it.%n", (String)environment.get("fileName"));
                }
                System.exit(-3);
            }
        }

        TSeriesList tsl = (TSeriesList) internals.get("seriesList");
        if (tsl == null) {
            //TODO(1.2) Check if it is not better use the filename as name for tseries list.
            tsl = new TSeriesList("TombolaCards newly generated set", (String) environment.get("cardLabelPrefix"));
        }

        if (mustGenerate) {
            int dMaxEpc = (int) environment.get("maxepc");
            int dMaxEpr = (int) environment.get("maxepr");
            // Check coherence of desired maxepc, maxepr and values derived from series imported from
            //  input file.
            if (tsl.size()>0) {
                System.out.println("Read series will be re-labeled using current TombolaCards configuration.");
                tsl.setLabelPrefix((String) environment.get("cardLabelPrefix"));
                int cMaxEpc = tsl.getCurrentMEPC();
                int cMaxEpr = tsl.getCurrentMEPR();
                if (cMaxEpc>dMaxEpc) {
                    if (!unattended) {
                        System.out.printf("Attention! Cards read from input file have a MaxEpc value of %d, greater than your desired of %d. %d is used for further series generation.%n", cMaxEpc, dMaxEpc, cMaxEpc);
                    }
                    dMaxEpc = cMaxEpc;
                }
                if (cMaxEpr>dMaxEpr) {
                    if (!unattended) {
                        System.out.printf("Attention! Cards read from input file have a MaxEpr value of %d, greater than your desired of %d. %d is used for further series generation.%n", cMaxEpr, dMaxEpr, cMaxEpr);
                    }
                    dMaxEpr = cMaxEpr;
                }
            }
            ISetFactory factory = TUtils.getSetFactoryByType((String) environment.get("method"));
            if (factory == null) {
                System.err.println("<FATAL!> Chosen Set Series generation method does not exist.");
                System.exit(-2);
            }
            factory.setSeriesList(tsl);
            factory.setDesiredSeries((int) environment.get("desiredSeries"));
            factory.setMaxEqualPerCard(dMaxEpc);
            factory.setMaxEqualPerRow(dMaxEpr);
            TMakeSix builder;
            if (this.environment.get("randomSeed") == null) {
                builder = new TMakeSix((boolean)environment.get("avoidEmptyColumn"));
            } else {
                builder = new TMakeSix((long)this.environment.get("randomSeed"),(boolean)environment.get("avoidEmptyColumn"));
            }

            factory.setSeriesBuilder(builder);
            
            factory.setLogger((ILogger) internals.get("logger"));
            int pingCounter = tsl.size();
            if (!unattended && !verbose) {
                System.out.print("\nRequested " + factory.getDesideredSeries() + " series, " + factory.getDesideredSeries()*6 + " cards.");
                System.out.print("\nGenerating: ");
            }
            factory.requestStart();
            internals.put("unsavedWork", true);
            while (!unattended && pingCounter < factory.getDesideredSeries()) {
                if (pingCounter < factory.getSeriesList().size()) {
                    String pingChar = ".";
                    //if ((pingCounter + 1) % 5 == 0) {
                    //    pingChar = "o";
                    //}
                    //if ((pingCounter + 1) % 10 == 0) {
                    //    pingChar = "O";
                    //}
                    if(verbose) {
                        System.out.println("Generated series " + (pingCounter+1) + " of " + factory.getDesideredSeries());
                    } else {
                        System.out.print(pingChar);
                    }
                    pingCounter++;
                }
            }
            try {
                factory.joinOnEnded();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            if (!unattended && !verbose) {
                System.out.println(" Done! " + factory.getSeriesList().size() + " have been generated.");
            }
            tsl = factory.getSeriesList();
            tsl.compareByCard();
            tsl.compareByRow();
            tsl.sortBestToWorstByCard();
            tsl.setLabelPrefix((String)environment.get("cardLabelPrefix"));
            tsl.prepareLabels(0, tsl.size(), (String)environment.get("cardLabelSeparator"), (TSeriesList.TLabelingModes)environment.get("cardLabelMode"), (boolean) environment.get("cardLabelChecksum"));
        }

        TFileFormatter tff = new TFileFormatter((TCardFormat) environment.get("fileFormat"), (boolean)environment.get("useJolly"));
        try {
            if(overWrite) {
                File f = new File((String)environment.get("fileName"));
                if (f.exists() && ! f.isDirectory()) {
                    f.delete();
                }
            }
            tff.writeSeriesFile((String) environment.get("fileName"), tsl, (TCardFormat) environment.get("fileFormat"), StandardOpenOption.CREATE_NEW);
            System.out.println("File [" +(String) environment.get("fileName")+"] has been saved.");
            internals.put("unsavedWork", false);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }

    }

    // ----------------------------------------------------------------------
    private void enterInteractiveMode() {
        boolean flagExit = false;
        boolean flagUnsavedWarning = false;
        System.out.println("\nInteractove mode - Type HELP to get hints on available interactive commands.\n");

        Scanner scanner = new Scanner(System.in);
        while (!flagExit) {
            ISetFactory isf = (ISetFactory) internals.get("setFactory");
            if (isf != null && isf.getStatus() != ISetFactory.TStatus.RUNNING) {
                boolean bNotifyEnd = (boolean) internals.get("notifyConclusion");
                if (bNotifyEnd) {
                    System.out.println(internals.get("rPrompt") + " Generation algorithm has come to an end.\n");
                    internals.put("notifyConclusion", false);
                }
            }

            System.out.print(internals.get("prompt")+" ");
            String readLine = scanner.nextLine();
            if (readLine.length()==0) {
                String pingMsg = "There are %d series (%d cards) in memory.";
                if (isf != null) {
                    pingMsg += " Set Factory is " + isf.getStatus();
                }
                pingMsg = internals.get("rPrompt") + " " + pingMsg + "\n";
                System.out.printf(pingMsg, ((TSeriesList) internals.get("seriesList")).size(), ((TSeriesList) internals.get("seriesList")).size()*6);
            }
            if (readLine != null && readLine.trim().length() > 0) {
                StringTokenizer st = new StringTokenizer(readLine.trim(), " ");
                String command = st.nextToken().toUpperCase();
                ICommand toDo = commands.get(command);

                int result = 0;
                if (toDo != null) {
                    try {
                        result = toDo.execute(st);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    if (toDo.getResultMessage() != null && toDo.getResultMessage().length()>0) {
                        toDo.echo(toDo.getResultMessage()+"\n");
                    }
                } else {
                    String rPrompt = (String) internals.get("rPrompt");
                    System.out.println(String.format("%s <Error> Command not recognized!", rPrompt));
                }
                flagExit = (result == 10);

            } else {
                // Nothing on the input line.
            }
            // System.out.println();
        }
    }

    private static void printInitialBanner() {
        System.out.println("\nHello Tombola World!");
        System.out.println("TombolaCards version: " + TT_VERSION);
        System.out.println("Library: " + TUtils.LIB_NAME + ", version " + TUtils.LIB_VERSION);
        System.out.println("--------------------------------------------------");
    }

    private static void printFinalBanner() {
        System.out.println("--------------------------------------------------");
        System.out.println("Bisogna avere il caos dentro di se per partorire una stella danzante.\n");
    }

    private static void printInfo(Map<String, Object> envMap) {
        System.out.println("\nUsage:\n");
        System.out.println("  TombolaCards [nSeries] {option flags ...}\n");

        System.out.println("  nSeries              The number of series (6 cards each) you want to generate.\n");

        System.out.println("  options are case insensitive and can be used in the order you prefer. Asterisks indicate default values\n");
        System.out.println("  -E=<method>          Set the euristic method used to generate the set of series. Available methods:");
        System.out.println("     *RANDOM              Use a pure random generetor method. MPC/MPR values are not used");
        System.out.println("      PROGRESSIVE         Progressively add a new series to the set only if MPC/MPR critera are met");
        System.out.println("  -F=<file_format>     Set the format used to store cards in the output file. Available formats:");
        System.out.println("     *CSV");
        System.out.println("      CSV_PLUS");
        System.out.println("      CSV_PACKED");
        System.out.println("      CSV_PACKED_PLUS");
        System.out.println("  -H                   If this option is present, TombolaCards prints this help and exit. No other options are considered.");
        System.out.println("  -I                   Enter the interactive mode!. nSeries is set by default to: " + envMap.get("desiredSeries") + " series, " + ((Integer) envMap.get("desiredSeries")) * 6 + " cards");
        System.out.println("  -J=[*ON | OFF]       Enable or disable the use of Jolly numbers (default: ON)");
        System.out.println("  -L=<label_prefix>    Set the prefix of the labels that will be used to name each card");
        System.out.println("  -LN=[*CARDS|SERIES]  Set numbering method of the cards in the file, by CARD, or by SERIES");
        System.out.println("  -LS=<separator>      Set the character used as separetor between the parts of each card label (default: '-')");
        System.out.println("  -LK=[*ON | OFF]      Set the presence or not of a final checksum code at the end of each card label");
        System.out.println("  -MPC=<nn>            Set the maximum number equal allowed between each couple of cards");
        System.out.println("  -MPR=<nn>            Set the maximum number equal allowed between each couple of rows of different cards");
        System.out.println("  -O=<file_name>       Set the filename where generated cards are stored (default: " + TT_DEFAULT_OUTPUT_FILENAME + ")");
        System.out.println("  -Q=[*ON | OFF]       Enable or disable the control to avoid entirely empty card columns  (default: ON)");
        System.out.println("  -R=<file_name>       Read the series from the specified file_name before start the series generation");
        System.out.println("                        Use this options to continue a previous work and/or add other series to a preexisting series file");
        System.out.println("  -S=random_seed       Let the  user to specify the seed for the pseudorandom number generator used to generate the cards");
        System.out.println("                        Using the same seed (and algorithm, etc.) let you rigenerate the same cards.");
        System.out.println("  -T=<file_name>       Specifies a log file where trace detailed information about the series generation (default: "+envMap.get("traceLogFileName")+")");
        System.out.println("  -U                   Use the Unattended mode, that is no interaction with the user are required");
        System.out.println("  -V                   Use verbose mode, print to the screen more detailed information during series creation");
        System.out.println("  -W                   Overwrite the specified output file if it already exists");
        // System.out.println("  -X                If present, extendended statistics information saved in the output file.");
        System.out.println("\nFor further information, please, take a look at http://pages.github.io/tombolaStudio/TombolaCards");
    }
}
