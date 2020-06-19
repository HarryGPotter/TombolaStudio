/**
 * <b>TombolaLib</b> is an <i>home made</i> Java class library aimed to support
 * the development of “end user ready” software tools and applications
 * supporting and enriching the experience of the traditional Italian game of
 * <b>Tombola</b>.&nbsp;Generate well formed cards, collect them in sets, save
 * to and load from files and databases, print and use them to <i>digitally
 * assist</i> real Tombola games played by small or large communities of
 * families and friends are all objectives addressed by this library.
 * <p>
 * Its purposes can quickly be summarized:
 * <ul>
 * <li>Provide data structures and methods help the creation of valid and
 * well-formed cards, that is the elementary sets of 15 numbers disposed on
 * three rows and nine columns following specific criteria.<p>
 * </li>
 * <li>Help to deal with large sets of cards, allowing the creation of such sets
 * while reducing the numbers that inevitably are equals between different cards
 * and thus limiting the probability of “concurrent wins” during the tombola
 * game. TombolaLib is able to prepare cards arranged in groups of six cards
 * using all the 90 numbers of the game, thus having zero numbers in common.
 * These groups are called series of cards. TombolaLib provide also heuristic
 * algorithms to generate several series of cards while limiting equal numbers
 * between cards and cards single rows.<p>
 * </li>
 * <li>Allow for generated set of cards to be saved in files with open text
 * formats, exported towards databases or other personal productivity tools, so
 * you can print cards, maybe personalize the layout and use them to play.<br>
 * A dedicated class provides also method to parse html+css based templates and
 * produce well formatted html+css file containing cards and ready to be printed
 * using any modern browser.<p>
 * </li>
 * <li>Lively support the real play of the game, specially in large communities
 * of families and friends, providing the environment to realize a “digital
 * twin” of the game, where cards can be checked in real time against extracted
 * numbers, prizes assigned, statistics evaluated, fun and “suspense generating”
 * facts shared, etc.<p>
 * </li>
 * </ul>
 *
 * @author Harry G. Potter (harry.g.potter@gmail.com)
 * @version 1.1
 * @since 1.8
 */
package harrygpotter.tombola.tombolalib;
