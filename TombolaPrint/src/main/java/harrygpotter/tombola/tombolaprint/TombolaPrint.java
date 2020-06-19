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
package harrygpotter.tombola.tombolaprint;

import harrygpotter.tombola.tombolalib.TCardFormat;
import harrygpotter.tombola.tombolalib.TFileFormatter;
import harrygpotter.tombola.tombolalib.THtmlPrinter;
import harrygpotter.tombola.tombolalib.TMakeSix;
import harrygpotter.tombola.tombolalib.TSeries;
import harrygpotter.tombola.tombolalib.TSeriesList;
import harrygpotter.tombola.tombolalib.TUtils;
import java.io.File;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.Map;

/**
 * Another Command Line Interface tool helping to prepare awesome prints of
 * you preferred tombola cards!
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 * @see harrygpotter.tombolalib
 */
public class TombolaPrint {

    private static final String TP_VERSION = "1.1";
    private static final String TP_DEFAULT_OUTPUT_FILENAME = "CardPrint001.html";

    /**
     * TombolaPrint [first | first last ] -F=InputFile -T=templateName {other options}
     * -O=outputFile -H -U -V -J
     *
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    public static void main(String[] args) throws IOException {
        // Runtime.getRuntime().addShutdownHook(new TShutdownHook());

        // Program invoked with no command line parameter. 
        // Header and Help info are printed, then TombolaTool ends.
        if (args.length < 1) {
            printInitialBanner();
            printInfo();
            System.exit(0);
        }

        String inputFile = null;
        String outputFile = TP_DEFAULT_OUTPUT_FILENAME;
        String templateFile = null;
        boolean verbose = false;
        boolean jollyOn = false;
        int first = -1;
        int last = -1;

        for (String arg : args) {
            boolean isNumber = false;
            try {
                int tempValue = Integer.parseInt(arg);
                if (tempValue < 0 || tempValue > TUtils.MAX_SERIES * 6) {
                    System.err.println(String.format("<FATAL!>  Card range to print must be in [0,%d].", TUtils.MAX_SERIES * 6 - 1));
                    System.exit(-1);
                }
                isNumber = true;
                if (first < 0) {
                    first = tempValue;
                } else if (last < 0) {
                    last = tempValue;
                } else {
                    System.err.println("<FATAL!>  You must specify maximum two number, first and last card to print.");
                    System.exit(-2);
                }
            } catch (NumberFormatException nfe) {
                // Nothing to do, it is not a problem.
            }
            if (!isNumber) {
                if (arg.length() < 2 || arg.charAt(0) != '-') {
                    System.err.println("<FATAL!> Parameter [" + arg + "] not recognized. Run TombolaPrint with no parameters to see the help.");
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
                    case "I":
                        inputFile = argArg;
                        break;
                    case "T":   // Read the (T)emplate file name (default extension thtml)
                        templateFile = argArg;
                        break;
                    case "O":   // Read the (O)utput file name (default extension html)
                        outputFile = argArg;
                        break;
                    case "J":   // Activate verbose mode
                        jollyOn = true;
                        break;
                    case "V":   // Activate verbose mode
                        verbose = true;
                        break;
                    case "H":   // just print the help message and exit.
                        printInfo();
                        System.exit(0);
                    break;
                }
            }

        }   // End of for args
        if (first<0) first = 0;
        TSeriesList tsl = null;

        System.out.print("Reading card series file: " + inputFile + "... ");
        TFileFormatter tff = new TFileFormatter();
        try {
            tsl = tff.readSeriesFile(inputFile, TCardFormat.AUTO);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        int printedCount = 0;
        if (tsl != null && tsl.size()>0) {
            System.out.println("[OK!]");
            System.out.println(String.format("%d cards just loaded.", tsl.size()*6));
            
            last = ((last>(tsl.size()*6-1) || last<0) ? tsl.size()*6-1 : last);

            THtmlPrinter thp = new THtmlPrinter(templateFile);
            thp.enableJolly(jollyOn);
            System.out.println(String.format("Start printing from card %d to card %d.", first, last));
            System.out.println("Using template file: " + templateFile);
            System.out.println("Jolly numbers will be " + (jollyOn ? "" : "NOT ") + "highlighted.");
            //TODO(1.2) Check for alread existing file...
            printedCount = thp.printHtml(tsl, first, last, outputFile, StandardOpenOption.CREATE_NEW);           
            
        } else {
            System.err.println("<ERROR> Impossible read cards for printing. Please retry. Thank you!\n");
        }
        System.out.println("Hic sunt leones! Just printed " + printedCount + " cards on file "+outputFile+".\nBest regards!\n");

    }

    private static void printInitialBanner() {
        System.out.println("\nHello again, Tombola World! Let's put your wishes on paper.");
        System.out.println("TombolaPrint version: " + TP_VERSION);
        System.out.println("Library: " + TUtils.LIB_NAME + ", version " + TUtils.LIB_VERSION);
        System.out.println("--------------------------------------------------");
    }

    private static void printInfo() {
        System.out.println("\nUsage:\n");
        System.out.println("  TombolaPrint [first | first last] -I=<input_file> -T=<template_file> {options flags ...}\n");

        System.out.println("  first                The index of the first card to print (default = 0)");
        System.out.println("  last                 The index of the last card to print (default = all cards within the input file)");
        System.out.println("  -I=<input_file>      Specify the input file from which read cards (mandatory parameter).");
        System.out.println("  -T=<template_file>   Specify the html+css template file name to use to generate printed cards.");
        System.out.println("                       (mandatory parameters)\n");

        System.out.println("  options are case insensitive and can be used in the order you prefer.\n");
        System.out.println("  -O=<file_name>       Set the filename where generated html output is stored (default: " + TP_DEFAULT_OUTPUT_FILENAME + ")");
        // TODO(1.2) System.out.println("  -L=<template_dir>    List the HTML/Css templates in the specified directory (default: ).");
        System.out.println("  -J                   If this option is present, jolly Numbers are NOT highlighted on the output html file.");
        System.out.println("  -H                   If this option is present, TombolaPrint prints this help and exit. No other options are considered.");
        System.out.println("  -U                   Use the Unattended mode, that is no interaction with the user are required");
        System.out.println("  -V                   Use verbose mode, print to the screen more detailed inforation during series printing");
        // TODO(1.2) System.out.println("  -W                   Overwrite the specified output file if it already exists");
        System.out.println("\nFor further information, please, take a look at http://pages.github.io/tombolaStudio/TombolaPrint");
    }
}
