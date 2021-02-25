# IFCtoRDF-Desktop
Version 2.8.0


A user interface for https://github.com/pipauwel/IFCtoRDF

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

cd EXPRESStoOWL-master

mvn clean install


cd IFCtoRDF-Desktop

mvn clean install

cd ..

## The main class is:
fi.ni.gui.fx.IFC2RDF_Desktop


![GitHub Logo](/IFCtoRDF-Desktop/src/main/resources/screen.png)


## The Bash compatible command line converter:
Example:

java -Xms24G -Xmx24G -jar IFCtoRDFCommandLine.jar -g file.ifc out.ttl

```
Usage: java -jar IFCtoRDFCommandLine.jar [OPTIONS] <inFile>... [<rdfTargetName>...]
      <inFile>...
      [<rdfTargetName>...]
  -d, --dir                  converts all entries at the directory input directory
  -g, --guid_uris            create GUID URIs
  -k, --keep-duplicates      keeps duplicate entries
  -u, --baseURI=<base_URI>   the base uri of the entities
```
## License
This project is released under the open source [GNU Affero General Public License v3](http://www.gnu.org/licenses/agpl-3.0.en.html)

## How to cite
```
@software{jyrki_oraskari_2020_4005935,
  author       = {Jyrki Oraskari},
  title        = {{jyrkioraskari/IFCtoRDF-Desktop: The IFCtoRDF 
                   Desktop Application 2.8}},
  month        = aug,
  year         = 2020,
  publisher    = {Zenodo},
  version      = {2.8},
  doi          = {10.5281/zenodo.4005935},
  url          = {https://doi.org/10.5281/zenodo.4005935}
}
```
## Frequently asked questions

1.  What should I do if I get error like "QuantumRenderer: no suitable pipeline found".

&nbsp;&nbsp;&nbsp;(copied from: assylias @ Stackoverflow)
- run the application with the -Dprism.verbose=true flag
- check the detailed log that is produced
- it may point to a missing garphics library: GTK 2.18 is required to run JavaFX on linux

&nbsp;&nbsp;&nbsp;Linux systems - try this first: 

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;apt-get install openjfx

&nbsp;&nbsp;&nbsp;Feodora:

 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; sudo dnf install java-1.8.0-openjdk-openjfx

&nbsp;&nbsp;&nbsp;If gtk libs missing:

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sudo apt-get install libgtk2.0-bin libXtst6 libxslt1.1

&nbsp;&nbsp;&nbsp;Ubuntu 16.10 (x86-64)- "missing 32-bit dependencies on 64-bit distribution":

&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;sudo apt-get install libgtk2.0-0:i386 libxtst6:i386


