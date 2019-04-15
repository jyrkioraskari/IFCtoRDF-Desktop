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

