# TombolaCards

**TombolaCard** is a simple command line tool written in Java that generates well formed cards to play the traditional Italian game of **Tombola** ([here more details](https://en.wikipedia.org/wiki/Tombola_(raffle))). Tombola cards are composed by 15 numbers between 1 and 90 placed on a matrix of three rows by nine columns (there are also 14 empty cells). Something like that:

|    |    |    |    |    |    |    |    |    |
|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|:--:|
|    | 13 |    | 33 |    | 50 | 61 |    | 82 |
|  7 |    | 24 |    | 42 |    | 66 |    | 80 |
|    | 11 | 21 | 36 | 44 |    |    | 75 |    |
|    |    |    |    |    |    |    |    |    |

TombolaCards uses [TombolaLib](../TombolaLib/Readme.MD) algorithms to generate _**series of cards**_, that is groups of six cards each where cards have no numbers in common and are made up using all 90 available numbers. Generated series of cards can be saved on files, choosing between several text based formats (csv, xml, sql inserts, etc.). Such files can be used by [TombolaPerf](../TombolaPerf/Readme.MD) to produce pretty HTML+CSS files ready to be printed on paper using your preferred web browser.

TombolaCards can be used both as an _unattended_ command line tool and in _interactive_ mode, letting the user to give specific instructions and waiting for related responses.

 ## Getting Started
These instructions will let ysu start use TombolaCards to prepare your preferred cards.

### Prerequisites
TombolaCard is a simple, plain Java program that have no specific prerequisites to be used by your own computer. Besides standard JDK, it just requires to use [TombolaLib](../TombolaLib/) classes.

- I developed and tested it on Oracle JDK 1.8 and Open JDK 12. And I use Apache Maven 3.6.2 as build and dependency management tool. So you just need:
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

### Installing and use
TombolaCard is just a standard Java program. There is not too much to do to install and use it. You can directly download the jar package [here](https://github.com/HarryGPotter/TombolaStudio/release1.1) and start using it. Open a command line (shell) window on your pc, move to the directory where the TombolaCard jar file is located and just type

```
java -jar TombolaCards-1.1.jar
```
Since you have specified no command line arguments, TombolaCards will print a short but comprehensive help message:
```
Hello Tombola World!
TombolaCards version: 1.1
Library: TombolaLib, version 1.1
--------------------------------------------------

Usage:

  TombolaCards [nSeries] {option flags ...}

  nSeries              The number of series (6 cards each) you want to generate.

  options are case insensitive and can be used in the order you prefer. Asterisks indicate default values

  -E=<method>          Set the euristic method used to generate the set of series. Available methods:
     *RANDOM              Use a pure random generetor method. MPC/MPR values are not used
      PROGRESSIVE         Progressively add a new series to the set only if MPC/MPR critera are met
  -F=<file_format>     Set the format used to store cards in the output file. Available formats:
     *CSV
      CSV_PLUS
      CSV_PACKED
      CSV_PACKED_PLUS
  -H                   If this option is present, TombolaCards prints this help and exit. No other options are considered.
  -I                   Enter the interactive mode!. nSeries is set by default to: 10 series, 60 cards
  -J=[*ON | OFF]       Enable or disable the use of Jolly numbers (default: ON)
  -L=<label_prefix>    Set the prefix of the labels that will be used to name each card
  -LN=[*CARDS|SERIES]  Set numbering method of the cards in the file, by CARD, or by SERIES
  -LS=<separator>      Set the character used as separetor between the parts of each card label (default: '-')
  -LK=[*ON | OFF]      Set the presence or not of a final checksum code at the end of each card label
  -MPC=<nn>            Set the maximum number equal allowed between each couple of cards
  -MPR=<nn>            Set the maximum number equal allowed between each couple of rows of different cards
  -O=<file_name>       Set the filename where generated cards are stored (default: CardSeries001.csv)
  -Q=[*ON | OFF]       Enable or disable the control to avoid entirely empty card columns  (default: ON)
  -R=<file_name>       Read the series from the specified file_name before start the series generation
                        Use this options to continue a previous work and/or add other series to a preexisting series file
  -S=random_seed       Let the  user to specify the seed for the pseudorandom number generator used to generate the cards
                        Using the same seed (and algorithm, etc.) let you rigenerate the same cards.
  -T=<file_name>       Specifies a log file where trace detailed information about the series generation (default: TombolaCards.log)
  -U                   Use the Unattended mode, that is no interaction with the user are required
  -V                   Use verbose mode, print to the screen more detailed information during series creation
  -W                   Overwrite the specified output file if it already exists
```
So, considering instructions above, you could start to try this sample command:
```
java -jar TombolaCards-1.1.jar 20 -O=MyFirstCards.csv
```
TombolaCards will randomly generate 20 series of 6 cards each (thus, 120 tombola cards) and will save them in `MyFirstCards.csv` file within the current directory. Here follows what you should see on your monitor:
```
Hello Tombola World!
TombolaCards version: 1.1
Library: TombolaLib, version 1.1
--------------------------------------------------
Requested 20 series, 120 cards.
Generating: .................... Done! 20 have been generated.
File [MyFirstCards.csv] has been saved.
--------------------------------------------------
Bisogna avere il caos dentro di se per partorire una stella danzante.
```
You may also prefer to enter the _interactive mode_ by typing:
```
java -jar TombolaCards-1.1.jar -i
```
In this case, you should read on your screen:
```
Hello Tombola World!
TombolaCards version: 1.1
Library: TombolaLib, version 1.1
--------------------------------------------------

Interactove mode - Type HELP to get hints on available interactive commands.

>> _
```
Now TombolaCards is ready to receive your interactive commands. Let's start typing `env` to check current values for TombolaCards parameters of `help` to remind yourself which are accepted commands. Enjoy and... remember, use the `env` command to check which are the current working parameter used by TombolaCards to prepare your cards, type `quit` or `exit` to terminate TombolaCards and return to your operation system command window.

### Installing and use - Using Maven
If you do not prefer to use the pre-packaged version of TombolaCards you can find in the `/release1.1` folder, you could try to prepare your build of TombolaCards using Maven. After you have cloned this repository on your pc, you just need to execute, within this `TombolaCards` home directory, the usual `mvn build` command.
After a successfull Maven build process, you should find a ready runnable jar file inside the usual /target subfolder.

## Further documentation and related projects
The whole **TombolaStudio** projects is described in an old fashioned _all-in-one_ document, you can find it in the main repository [folder](https://github.com/HarryGPotter/TombolaStudio). 
Chatper four *"Generate cards with TombolaCards"* is fully dedicated to introduce you all TombolaCards functionalities and suggestions for an optimal use.

## Contributing
I haven't set a contribution policy yet, but.. the code is here on GitHub and I'll be very happy to receive suggestions, comments, corrections, pull requests... thank you very much since now!

## Authors
* **Harry G. Potter** - _harry[dot]g[dot]potter[at]gmail[dot]com_

## License

This project is licensed under the GNU GENERAL PUBLIC LICENSE - see the [LICENSE](../LICENSE) file for details

## Acknowledgments

* Hat tip to my brother, **"The Doc"**, for his original algorithm, many ideas and his inspirations ( `if(stessaDecina())` made the history! ).