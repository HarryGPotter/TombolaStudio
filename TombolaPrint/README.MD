# TombolaPrint

**TombolaPrint** is a simple command line tool written in Java that reads tombola cards file produced by `TombolaCards` and produces pretty HTML+CSS files ready to be printed on paper using your preferred browser. To achieve this goal, TombolaPrint uses templates files, that are standard HTML+CSS files using special tags that are substituted using data read from cards files produces by `TombolaCards`.

If you want, you can take a look of such templates in the [print_templates](./print_templates) folder. There are some _basic_ templates realized just as first examples. Each template is optimized to print one or more card on an A4 sized sheet of paper. Templates file names give you an hint about how many cadrs are printed on a single sheet (1, 2, 3) and the paper orientation set (landscape or portrait).
It should be a good idea to open them with your preferred text editor and try to understand the _principles_ and the _logic_ upon which they are based, which are the 'static parts' of the template that does not change when cards are printed and which are instead the tags that will be susbstitute during the print process.
Changing and adapting these templates to your needs will allow you to put images on your cards, backgrounds, colors, preferred character fonts, etc.

TombolaPrint uses [TombolaLib](../TombolaLib/Readme.MD) algorithms to manipulate cards files.

TombolaPrint can be used as an usual command line tool specifing parameters and otptions when calling the executable jar file (see below).

 ## Getting Started
These instructions will let ysu start use TombolaPrint to print your preferred cards.

### Prerequisites
TombolaPrint is a simple, plain Java program that have no specific prerequisites to be used by your own computer. Besides standard JDK, it just requires to use [TombolaLib](../TombolaLib/) classes.

I developed and tested it on Oracle JDK 1.8 and Open JDK 12. And I use Apache Maven 3.6.2 as build and dependency management tool. So you just need:
* An up & running [Java Development Kit](https://www.oracle.com/technetwork/java/javase/overview/index.html), version 1.8 or higher
* (_optional_) [Apache Maven](https://maven.apache.org/) version 3.6.1 or higher installed on your machine
* Finally, we assume that you have already prepared a file containing the cards you want to print using `TombolaCards`. Let's say this file is named `MyCards.csv` and is whitin the same directory where you're launching TombolaPrint.

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
TombolaPrint is just a standard Java program. There is not too much to do to install and use it. You can directly download the jar package [here](https://github.com/HarryGPotter/TombolaStudio/release1.1) and start using it. Open a command line (shell) window on your pc, move to the directory where the TombolaPrint jar file is located and just type

```
java -jar TombolaPrint-1.1.jar
```
Since you have specified no command line arguments, TombolaPrint will print a short but comprehensive help message:
```
Hello again, Tombola World! Let's put your wishes on paper.
TombolaPrint version: 1.1
Library: TombolaLib, version 1.1
--------------------------------------------------

Usage:

  TombolaPrint [first | first last] -I=<input_file> -T=<template_file> {options flags ...}

  first                The index of the first card to print (default = 0)
  last                 The index of the last card to print (default = all cards within the input file)
  -I=<input_file>      Specify the input file from which read cards (mandatory parameter).
  -T=<template_file>   Specify the html+css template file name to use to generate printed cards.
                       (mandatory parameters)

  options are case insensitive and can be used in the order you prefer.

  -O=<file_name>       Set the filename where generated html output is stored (default: CardPrint001.html)
  -J                   If this option is present, jolly Numbers are NOT highlighted on the output html file.
  -H                   If this option is present, TombolaPrint prints this help and exit. No other options are considered.
  -U                   Use the Unattended mode, that is no interaction with the user are required
  -V                   Use verbose mode, print to the screen more detailed inforation during series printing

For further information, please, take a look at http://pages.github.io/tombolaStudio/TombolaPrint
```
So, considering instructions above, you could start to try this sample command:
```
java -jar TombolaPrint-1.1.jar -I=MyCards.csv -T=print_templates/SIMPLE_1Card_x_Page_A4L.html -O=MyPrintedCards.html
```
It seems a complicated command line but it just specifies three parameters:
* the input file containing the cards to print (option -I, file `MyCards.csv`);
* the html template file to use (option -T, the chosen template print one card per page on A4 sheets);
* the output file name where the produced html will be saved, (option -O, file MyPrintedCards.html)
Here follows what you should see on your monitor:
```
Reading card series file: MyCards.csv... [OK!]
60 cards just loaded.
Start printing from card 0 to card 59.
Using template file: print_templates/SIMPLE_1Card_x_Page_A4L.html
Jolly numbers will be NOT highlighted.
Hic sunt leones! Just printed 60 cards on file MyPrintedCards.html.
Best regards!
```
Now, you can open the `MyPrintedCards.html` whit your browser, choose print and see if you like results!


### Installing and use - Using Maven
If you do not prefer to use the pre-packaged version of TombolPrint you can find in the `/release1.1` folder, you could try to prepare your build of TombolaPrint using Maven. After you have cloned this repository on your pc, you just need to execute, within this `TombolaPrint` home directory, the usual `mvn build` command.
After a successfull Maven build process, you should find a ready runnable jar file inside the usual /target subfolder.

## Further documentation and related projects
The whole **TombolaStudio** projects is described in an old fashioned _all-in-one_ document, you can find it in the main repository [folder](https://github.com/HarryGPotter/TombolaStudio). 
Chatper five *"Print your cards with TombolaPrint"* is fully dedicated to introduce you all TombolaPrint functionalities and suggestions for an optimal usage.

## Contributing
I haven't set a contribution policy yet, but.. the code is here on GitHub and I'll be very happy to receive suggestions, comments, corrections, pull requests... thank you very much since now!

## Authors
* **Harry G. Potter** - _harry[dot]g[dot]potter[at]gmail[dot]com_

## License

This project is licensed under the GNU GENERAL PUBLIC LICENSE - see the [LICENSE](../LICENSE) file for details

## Acknowledgments

* Hat tip to my brother, **"The Doc"**, for his original algorithm, many ideas and his inspirations ( `if(stessaDecina())` made the history! ).