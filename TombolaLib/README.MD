# TombolaLib

**TombolaLib** is an _home made_ Java class library supporting the development of “end user ready”applications aimed to enriching the experience of the traditional Italian game of **Tombola** [Here more details](https://en.wikipedia.org/wiki/Tombola_(raffle)). Generating _well formed_ tombola cards, collecting them in card sets, saving to and loading from files and databases, printing and using them to _digitally assist_ real Tombola games played by small and large communities of families and friends are all objectives addressed by this library.

TombolaLib:

 * provides data structures and methods helping to generate valid and well-formed cards, that is the elementary sets of 15 numbers disposed on three rows and nine columns used to play the raffle game.

 * helps to deal with large sets of cards. TombolaLib is able to prepare cards arranged in groups of six cards using all the 90 numbers of the game, thus having zero numbers in common. These groups are called **series of cards**. TombolaLib provide also heuristic algorithms to generate several series of cards while limiting equal numbers between cards and also between each card row.
 
 * saves generated set of cards in files using 'open' text formats (csv, xml, json) or exports them towards sql databases so you can use them in several ways. A dedicated class provides also method to parse html+css based templates and produce well formatted html+css card file ready to be printed using any modern browser.
 
 * Lively supports the real play of the game, specially in large communities of families and friends, providing the environment to realize a “digital twin” of the game, where cards can be checked in real time against extracted numbers, prizes assigned, statistics evaluated, fun and “suspense generating” facts shared, etc.

 ## Getting Started
These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites
TombolaLib is a simple, plain java class library that have no specific prerequisites to be used by your own Java applications. It neither use any other libraries besides standard JDK. I developed and tested it on Oracle JDK 1.8 and Open JDK 12. And I use Apache Maven 3.6.2 as build and dependency management tool. So you just need:
* An up & running [Java Development Kit](https://www.oracle.com/technetwork/java/javase/overview/index.html), version 1.8 or higher
* (_optional_) [Apache Maven](https://maven.apache.org/) version 3.6.1 or higher installed on your machine

Here follows what happens on my machine when I run the java -version command:

```
c:\>java -version
java version "1.8.0_221"
Java(TM) SE Runtime Environment (build 1.8.0_221-b11)
Java HotSpot(TM) 64-Bit Server VM (build 25.221-b11, mixed mode)
```
And here follows the output of the mvn -version command on my pc:

```
c:\>mvn -version
Apache Maven 3.6.1 (d66c9c0b3152b2e69ee9bac180bb8fcc8e6af555; 2019-04-04T21:00:29+02:00)
Maven home: C:\dev\run\apache-maven-3.6.1\bin\..
OS name: "windows 7", version: "6.1", arch: "amd64", family: "windows"
```

### Installing
TombolaLib is just a Java library. There is not too much to do to install it. You can directly download the jar package [here](https://github.com/HarryGPotter/TombolaStudio/release1.1), include it in your classpath (on within the dependencies of your project) and start using it.
Otherwise, you can clone this repository and prepare your own package using Maven.

```
c:\> cd TombolaLib
C:\> mvn package

[INFO] Scanning for projects...
[INFO]
[INFO] ------------------< harrygpotter.tombola:TombolaLib >-------------------
[INFO] Building TombolaLib 1.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-resources-plugin:2.6:resources (default-resources) @ TombolaLib ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory c:\...\TombolaStudio\TombolaLib\src\main\resources
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:compile (default-compile) @ TombolaLib ---
[INFO] Compiling 1 source file to c:\...\TombolaStudio\TombolaLib\target\classes
[INFO]
[INFO] --- maven-resources-plugin:2.6:testResources (default-testResources) @ TombolaLib ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory c:\...\TombolaStudio\TombolaLib\src\test\resources
[INFO]
[INFO] --- maven-compiler-plugin:2.3.2:testCompile (default-testCompile) @ TombolaLib ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- maven-surefire-plugin:2.12.4:test (default-test) @ TombolaLib ---
[INFO]
[INFO] --- maven-jar-plugin:2.4:jar (default-jar) @ TombolaLib ---
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.474 s
[INFO] Finished at: 2019-10-28T17:02:26+01:00
[INFO] ------------------------------------------------------------------------

```

After a successfull Maven build process, you should find a ready library jar file inside the usual /target folder.

## Further documentation and related projects
The whole **TombolaStudio** projects is described in an old fashioned _all-in-one_ document, you can find it in the main repository [folder](https://github.com/HarryGPotter/TombolaStudio). Besides, I spent some time to write the javadoc documentation at an acceptable level, you can find it here in the [/apidoc](https://github.com/HarryGPotter/TombolaStudio/TombolaLib/apidoc) directory.
**TombolaLib** is the cornerstone library for some other more final user-ready little programs, such as TombolaCards, TombolaPrint. Please consider also them as good use-cases to see this library in action.
Finally, it worth mentioning that TombolaLib includes a sort of demo class, `TTombolaExamples`, where you can see main objects and methods of the library, how generate series of cards, prepare and _digitally twin_ a Tombola match. Have a look at `TTombolaExamples#mainGameLoop()`, it constitutes an interesting insight on the details that should be managed during a match.

Here the simplest example of use for this class.

```
import harrygpotter.tombola.tombolalib.*;

public class HelloTombolaWorld {
    
    public static void main(String[] args) {
        System.out.println("Let's show the simplest use of TombolaLib!");        
        
        TTombolaExamples demo = new TTombolaExamples();
        demo.prepareSampleSeriesSet(3);
        demo.initializeGame();
        demo.mainGameLoop(false);
    }
 
}
```

## Contributing
I haven't set a contribution policy yet, but.. the code is here on GitHub and I'll be very happy to receive suggestions, comments, corrections, pull requests... thank you very much since now!

## Authors
* **Harry G. Potter** - _harry[dot]g[dot]potter[at]gmail[dot]com_

## License

This project is licensed under the GNU GENERAL PUBLIC LICENSE - see the [LICENSE](../LICENSE) file for details

## Acknowledgments

* Hat tip to my brother, **"The Doc"**, for his original algorithm, many ideas and his inspirations ( `if(stessaDecina())` made the history! ).