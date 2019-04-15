# IFCtoRDF-Desktop
Version 2.2

## Installation: 
Java 8 is supported. It can be downloaded from https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html




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

This can  be run from the command line using the following command:
java -jar IFC-RDF_Desktop_2.2.jar

If a larger model is converted, use the following:
java -Xms24G -Xmx24G -XX:MaxPermSize=1G  -jar IFC-RDF_Desktop_2.2.jar

![GitHub Logo](/fi.ni.ifc-rdf-desktop/src/main/resources/screen.png)

