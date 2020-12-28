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

import java.io.FileNotFoundException;
import java.util.Random;

/**
 * This class contains some methods showing how TombolaLib classes and
 * functionalities should be accessed and used by client Java code. Basically,
 * you will find here methods that generate series of cards, prepare (i.e.
 * initialize) a tombola match and play or, if you prefer, simulate a game using
 * such series. Of course methods here are simplify and only show 'default'
 * behaviors and most used cases. But, together with Javadoc associated to this
 * library, they will give you a fair starting point to start and experiment
 * with TombolaLib.
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
public class TTombolaExamples {

    public static String SERIES_FACTORY_TYPE = "RANDOM";
    public static String PREPARATION_LOG_NAME = "preparationLogger";
    public static String PREPARATION_LOG_FILENAME = "SamplePreparation.log";

    private ITLogger seriesPreparationLogger;
    private ITLogger gameLogger;
    private TGame tombola;
    private TSeriesList series;
    private TCardList cards;

    /**
     * Return the {@linkplain TSeriesList} object containing the series used by
     * other example methods within this object. Call
     * {@linkplain TTombolaExamples#prepareSampleSeriesSet(int)} before,
     * otherwise this method will return just a null value.
     *
     * @see TTombolaExamples#prepareSampleSeriesSet(int)
     * @return the series list used by this object to demonstrate the library
     * utilization.
     */
    public TSeriesList getSeriesList() {
        return series;
    }

    /**
     * Return the {@link TGame} object used by other methods within this object
     * to demonstrate the use of the library and thus the simulation of a
     * tombola match. Call this method after a call to
     * {@linkplain TTombolaExamples#initializeGame()}, otherwise you will get
     * just a null value.
     *
     * @return the game object used to demonstrate the library utilization.
     */
    public TGame getGameObject() {
        return tombola;
    }

    /**
     * This method shows how to basically use a factory objects (i.e.&nbsp;a
     * tombolalib class instance implementing the ISetFactory interface) to
     * generate a set of series of cards. This method illustrates also how to
     * prepare a "custom" tombola logger object, that is an instance of
     * {@link TSimpleLogger} class implementing the {@link ITLogger} interface.
     *
     * @param desiredSeries the number of series you want to generate.&nbsp;Each
     * series is composed by 6 cards using all 90 numbers.
     */
    public void prepareSampleSeriesSet(int desiredSeries) {
        try {
            TSimpleLogger.prepareLogger(PREPARATION_LOG_NAME, ITLogger.TLogLevel.VER, PREPARATION_LOG_FILENAME);
        } catch (FileNotFoundException fnfex) {
            System.err.println(fnfex.getMessage());
        }

        ITSetFactory factory = TUtils.getSetFactoryByType(SERIES_FACTORY_TYPE);
        seriesPreparationLogger = TSimpleLogger.getLoggerByName(PREPARATION_LOG_NAME);
        series = new TSeriesList("SempleSet", "AA");

        factory.setLogger(seriesPreparationLogger);
        factory.setSeriesBuilder(new TMakeSix());
        factory.setSeriesList(series);
        factory.setDesiredSeries(desiredSeries);

        factory.requestStart();
        try {
            factory.joinOnEnded();
        } catch (InterruptedException ie) {
            System.err.println(ie.getMessage());
        }
    }

    /**
     * This mathod shows how to instantiate and prepare a {@link TGame} object,
     * that is the main object allowing the management (or simulation) of a
     * tombola match.
     */
    public void initializeGame() {

        cards = new TCardList(series);

        String[] owners = new String[]{"Di Maio", "Salvini", "Renzi", "Berlusconi", "Conte", "Di Battista", "Toninelli"};
        cards.forEach((c) -> {
            c.setOwner(owners[(new Random()).nextInt(7)]);
        });

        gameLogger = TSimpleLogger.getLoggerByName(TSimpleLogger.DEFAULT_GAME_LOGGER);
        gameLogger.setLevel(ITLogger.TLogLevel.INF);

        tombola = new TGame("SampleMatch");
        tombola.setLogger(gameLogger);
        tombola.setCards(cards);
        tombola.setAwards(TAwardList.getSimpleSingleAwardList());
        tombola.setSacchetto(new TSacchetto());
    }

    /**
     * This method shows how to use and interact with a TGame object to
     * simulate and manage a tombola match.&nbsp;The main TGame method to
     * iteratively call is the {@link TGame#extractNumber(int)}
     * method.&nbsp;Checking its return code allows for taking proper action
     * after the extraction of each number, until the game comes to the end.
     *
     * @param randomResolve is used to choose in which way the example must
     * proceed when there are more than one cards candidate to win a prize after
     * the same number extraction.&nbsp;pass true to let the exmple method
     * proceed unattended, thus randomly chosing one winner among all the
     * candidates cards, pass false to let the method stop and ask to the user
     * on the standard console which card must be appointed.
     * @param confirmCandidates is used to set is explicit confirmation of card
     * candidates to win awards must be performed by the user or not.
     */
    public void mainGameLoop(boolean randomResolve, boolean confirmCandidates) {
        tombola.setConfirmCandidateOn(confirmCandidates);

        // Simple while cycle to show how a Tombola game can be managed. It is just one
        // of the simplest cycles, but you can arrange it as you prefer once you got the
        // logic behind TGame object and its methods.
        // Consider that inserting the .extractNumber() call inside the while condition
        // cause an extra call after the last award has benn won, but this is not a problem
        // and shows how TGame handle and log it.
        while (tombola.extractNumber(0) != TGameResultCode.GAME_OVER) {

            // this if branch is used only if confirmCandidates is set to true
            if (tombola.getStatus() == TGameStatus.ACCEPTING) {
                for (TAward aw : tombola.getAwards().getValidatingAwards()) {
                    if (aw.getValidatingList().size() > 0) {
                        System.out.printf("There are %d contenders for the award <%s> to confirm or deny%n", aw.getValidatingList().size(), aw.getLabel());
                        while (aw.getValidatingList().size() > 0) {
                            TCard c = aw.getValidatingList().get(0);
                            System.out.printf("    Card %s owned by %s: [C]onfirm or [D]eny?%n", c.getLabel(), c.getOwner());
                            String sChoice = "";
                            while (!sChoice.toUpperCase().startsWith("D") && !sChoice.toUpperCase().startsWith("C")) {
                                sChoice = System.console().readLine();
                                if (sChoice.toUpperCase().startsWith("C")) {
                                    tombola.confirmCandidate(c);
                                } else if (sChoice.toUpperCase().startsWith("D")) {
                                    tombola.denyCandidate(c);
                                }
                            }
                        }
                    }
                }
                if (tombola.getStatus() == TGameStatus.BUSY) {
                    tombola.assign();
                } else {
                    // There's a problema: I should never be here
                    System.out.println("There's a problema: I should never be here!");
                }
            }

            // While loop is needed here because the "Resolving" process can cascade candidates
            // to next available awards (i.e. a second or third "Terno", etc.
            while (tombola.getStatus() == TGameStatus.RESOLVING) {
                TAward contendedAward = tombola.getFirstAwardToResolve();
                int nCandidates = contendedAward.getCandidatesList().size();
                if (!randomResolve) {
                    System.out.printf("There are %d contenders for the award <%s>:%n", nCandidates, contendedAward.getLabel());
                    int i = 0;
                    for (TCard cx : contendedAward.getCandidatesList()) {
                        System.out.printf("    [%2d] --> %s owned by %s%n", i++, cx.getLabel(), cx.getOwner());
                    }
                    System.out.printf("  Please enter the number of the winner between [0,%d]: ", nCandidates - 1);
                    String sChoice = System.console().readLine();
                    int winnerNumber = Integer.parseInt(sChoice);
                    tombola.resolveCandidates(winnerNumber);
                } else {
                    int winnerNumber = new Random().nextInt(nCandidates);
                    tombola.resolveCandidates(winnerNumber);
                }
            }
        }
    }
}   // End Of File - Rel.(1.1)
