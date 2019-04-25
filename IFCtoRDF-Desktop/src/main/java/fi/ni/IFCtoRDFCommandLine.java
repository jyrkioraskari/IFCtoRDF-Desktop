package fi.ni;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

import be.ugent.IfcSpfReader;
import guidcompressor.GuidCompressor;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/*
* The GNU Affero General Public License
* 
* Copyright (c) 2019 Jyrki Oraskari (Jyrki.Oraskari@aalto.fi); 
* 2016 Pieter Pauwels, Ghent University; Lewis John McGibbney, Apache
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as
* published by the Free Software Foundation, either version 3 of the
* License, or (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program. If not, see <http://www.gnu.org/licenses/>.
*/

@Command(abbreviateSynopsis = true,name = "IFCtoRDFCommandLine")
public class IFCtoRDFCommandLine {
    @Parameters(index = "0",arity = "1..*")    String inFile;
    @Parameters(index = "1",arity = "0..*")    String rdfTargetName;

    @Option(names = { "-u", "--baseURI" }, description = "the base uri of the entities")
    String base_URI;

    @Option(names = { "-g", "--guid_uris" }, description = "create GUID URIs")
    boolean createGUID_URIs;

    @Option(names =  { "-k", "--keep-duplicates"}, description = "keeps duplicate entries")
    boolean keep_duplicates=false;

    @Option(names = { "-d", "--dir"}, description = "converts all entries at the directory input directory")
    boolean readsdir;

    String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

    public IFCtoRDFCommandLine() {
    }

    public void convert() {
        if(inFile==null)
            return;

        if (base_URI == null) {
            base_URI = "http://linkedbuildingdata.net/resources/" + timeLog + "/";
        }
        System.out.println("base_URI: "+base_URI);
        if(this.createGUID_URIs)
            System.out.println("Creating GUID URIs.");

        if(this.keep_duplicates)
            System.out.println("Keeping duplicate entries.");
        
        if(this.readsdir)
            System.out.println("Converting the whole directory.");
        
        final List<String> inputFiles;
        final List<String> outputFiles;

        if (readsdir) {
            inputFiles = showFiles(inFile);
            outputFiles = null;
        } else {
            inputFiles = Arrays.asList(new String[] { inFile });
            if(rdfTargetName==null)
                outputFiles = null;
            else
                 outputFiles = Arrays.asList(new String[] { rdfTargetName});
        }

        for (int i = 0; i < inputFiles.size(); ++i) {
            final String inputFile = inputFiles.get(i);
            final String outputFile;
            if (inputFile.endsWith(".ifc")) {
                if (outputFiles == null) {
                    outputFile = inputFile.substring(0, inputFile.length() - 4) + ".ttl";
                } else {
                    outputFile = outputFiles.get(i);
                }
                System.out.println("inputFile: "+inputFile);
                System.out.println("outputFile: "+outputFile);

                convert(inputFile, outputFile, base_URI, this.createGUID_URIs);
            }
        }
        System.out.println("Done");
    }

    /**
     * List all files in a particular directory.
     * 
     * @param dir
     *            the input directory for which you wish to list file.
     * @return a {@link java.util.List} of Strings denoting files.
     */
    public List<String> showFiles(String dir) {
        List<String> goodFiles = new ArrayList<>();

        File folder = new File(dir);
        if(!folder.isDirectory())
        {
            System.err.println("The input is not a directory.");
            return goodFiles;
        }
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile())
                goodFiles.add(listOfFiles[i].getAbsolutePath());
            else if (listOfFiles[i].isDirectory())
                goodFiles.addAll(showFiles(listOfFiles[i].getAbsolutePath()));
        }
        return goodFiles;
    }
    
    public IFCtoRDFCommandLine(String ifcFileName, String rdfTargetName) {
        String baseURI = "http://linkedbuildingdata.net/ifc/resources" + timeLog + "/";
        convert(ifcFileName, rdfTargetName, baseURI, false);
    }

    public IFCtoRDFCommandLine(String ifcFileName, String rdfTargetName, boolean hasGUID_URIs) {
        String baseURI = "http://linkedbuildingdata.net/ifc/resources" + timeLog + "/";
        convert(ifcFileName, rdfTargetName, baseURI, hasGUID_URIs);
    }

    public IFCtoRDFCommandLine(String ifcFileName, String rdfTargetName, String baseURI, boolean hasGUID_URIs) {
        convert(ifcFileName, rdfTargetName, baseURI, hasGUID_URIs);
    }

    private void convert(String ifcFileName, String rdfTargetName, String baseURI, boolean hasGUID_URIs) {
        IfcSpfReader r = new IfcSpfReader();
        r.setRemoveDuplicates(!this.keep_duplicates);
        try {
            URL u = new URL(baseURI);
            u.toURI();
        } catch (Exception e) {
            baseURI = "http://linkedbuildingdata.net/ifc/resources" + timeLog + "/";
        }
        try {
            if (hasGUID_URIs) {
                final Map<String, String> rootmap = new HashMap<>();
                File tempFile = File.createTempFile("ifc", ".ttl");
                try {
                    Model m = ModelFactory.createDefaultModel();
                    r.convert(ifcFileName, tempFile.getAbsolutePath(), baseURI);
                    RDFDataMgr.read(m, tempFile.getAbsolutePath());

                    m.listStatements().forEachRemaining(x -> {
                        String guid = getGUID(x.getSubject());
                        if (guid != null) {
                            rootmap.put(x.getSubject().getURI(), GuidCompressor.uncompressGuidString(guid));
                        }
                    });

                    generateModel(rootmap, m, rdfTargetName);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    tempFile.deleteOnExit();
                }

            } else
                r.convert(ifcFileName, rdfTargetName, baseURI);
        } catch (IOException e) {
        }

    }

    private void generateModel(Map<String, String> rootmap, Model in_model, String rdfTargetName) {
        Map<String, Resource> resources_map = new HashMap<>();

        for (String root_uri : rootmap.keySet()) {
            String sg = rootmap.get(root_uri);
            if (sg != null) {
                String sn = root_uri.substring(0, (root_uri.lastIndexOf("/") + 1)) + "GUID/" + sg;
                Resource root = ResourceFactory.createResource(sn);
                resources_map.put(root_uri, root);
            }
        }
        Map<String, String> prefixes = in_model.getNsPrefixMap();

        Model out_model = ModelFactory.createDefaultModel();
        out_model.setNsPrefixes(prefixes);
        in_model.listStatements().forEachRemaining(x -> {
            Resource s = x.getSubject();
            Property p = x.getPredicate();
            RDFNode o = x.getObject();
            if (resources_map.containsKey(s.getURI()))
                s = resources_map.get(s.getURI());
            if (o.isResource())
                if (resources_map.containsKey(o.asResource().getURI()))
                    o = resources_map.get(o.asResource().getURI());
            out_model.add(out_model.createStatement(s, p, o));
        });

        try (OutputStream out = new FileOutputStream(rdfTargetName)) {
            RDFDataMgr.write(out, out_model, Lang.N3);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getGUID(Resource r) {
        StmtIterator i = r.listProperties();
        while (i.hasNext()) {
            Statement s = i.next();
            if (s.getPredicate().toString().endsWith("globalId_IfcRoot")) {
                String guid = s.getObject().asResource().getProperty(property(EXPRESS, "hasString")).getObject().asLiteral().getLexicalForm();
                return guid;
            }
        }
        ;
        return null;
    }

    public static final String EXPRESS = "https://w3id.org/express#";

    Property property(String base_uri, String tag) {
        return ResourceFactory.createProperty(base_uri, tag);
    }

    public static void main(String[] args) {
        org.apache.jena.query.ARQ.init();
        IFCtoRDFCommandLine converter = new IFCtoRDFCommandLine();
        CommandLine commandLine=new CommandLine(converter);
        try {
            commandLine.parse(args);
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
            commandLine.usage(System.out);
        }
        converter.convert();
    }

}
