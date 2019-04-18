# IFCtoRDF-Desktop
Version 2.2

## Usage: 
IFC-RDF_Desktop.jar can be started by double clicking the file.

The same from the commad line:
java -jar IFC-RDF_Desktop.jar

If a larger model is converted, use the following:
java -Xms24G -Xmx24G -XX:MaxPermSize=1G  -jar IFC-RDF_Desktop.jar

The binary was compiled with Java 8. It was been tested to work with Java 11, also.

If needed, Java can be downlowaded from:
https://www.java.com/en/download/

## Compilation: 
git clone https://github.com/jyrkioraskari/IFCtoRDF-Desktop.git

cd be.ugent.EXPRESStoOWL-0.3_snapshot
mvn clean install
cd ..

cd be.ugent.IFCtoRDF-0.3_snapshot
mvn clean install
cd ..

cd IFCtoRDF-Desktop
mvn clean install
cd ..

## The main class is:
fi.ni.gui.fx.IFC2RDF_Desktop


![GitHub Logo](/IFCtoRDF-Desktop/src/main/resources/screen.png)


## License
This project is released under the open source [GNU Affero General Public License v3](http://www.gnu.org/licenses/agpl-3.0.en.html)

## Frequently asked questions

1.  What should I do if I get error like "QuantumRenderer: no suitable pipeline found".

(copied from: assylias @ Stackoverflow)
- run the application with the -Dprism.verbose=true flag
- check the detailed log that is produced
- it may point to a missing garphics library: GTK 2.18 is required to run JavaFX on linux

&nbsp;&nbsp;&nbsp;Linux systems - try this first: 
 apt-get install openjfx

&nbsp;&nbsp;&nbsp;Feodora:

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; sudo dnf install java-1.8.0-openjdk-openjfx

&nbsp;&nbsp;&nbsp;If gtk libs missing:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sudo apt-get install libgtk2.0-bin libXtst6 libxslt1.1

&nbsp;&nbsp;&nbsp;Ubuntu 16.10 (x86-64)- "missing 32-bit dependencies on 64-bit distribution":

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sudo apt-get install libgtk2.0-0:i386 libxtst6:i386


