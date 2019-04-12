# IFCtoRDF-Desktop
2.0

## Installation: 
Java 8 is supported. It can be downloaded from https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html




git clone https://github.com/jyrkioraskari/IFCtoRDF-Desktop.git

cd IFCtoRDF-Desktop

mvn clean install


## The main class is:

fi.ni.gui.fx.IFC2RDF_Desktop

This can  be run from the command line using the following command:
java -jar IFCtoRDF_Desktop.jar

If a larger model is converted, use the following:
java -Xms24G -Xmx24G -XX:MaxPermSize=1G  -jar IFCtoRDF_Desktop.jar 

![GitHub Logo](/fi.ni.ifc-rdf-desktop/src/main/resources/screen.png)

