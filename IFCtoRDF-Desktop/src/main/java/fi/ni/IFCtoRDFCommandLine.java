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
import org.ifcrdf.EventBusService;
import org.ifcrdf.messages.SystemErrorEvent;
import org.ifcrdf.messages.SystemStatusEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import be.ugent.IfcSpfReader;
import guidcompressor.GuidCompressor;
import javafx.application.Platform;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/*
 * Copyright (c) 2015, 2019 Jyrki Oraskari (Jyrki.Oraskari@aalto.fi / jyrki.oraskari@aalto.fi)
 * Copyright (c) 2015 Pieter Pauwels (pipauwel.pauwels@ugent.be / pipauwel@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@Command(abbreviateSynopsis = true,name = "IFCtoRDFCommandLine")
public class IFCtoRDFCommandLine {
    private final EventBus eventBus = EventBusService.getEventBus();
    private static final Logger LOG = LoggerFactory.getLogger(IFCtoRDFCommandLine.class);
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
        eventBus.register(this);
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
        else
            System.out.println("Not creating GUID URIs. Use --guid_uris if needed.");

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
                    System.out.println("Temp file: "+tempFile.getAbsolutePath());
                    RDFDataMgr.read(m, tempFile.getAbsolutePath());
                    System.out.println("Create a list of GUID nodes");
                    m.listStatements().forEachRemaining(x -> {
                        String guid = getGUID(x.getSubject());
                        if (guid != null) {
                            rootmap.put(x.getSubject().getURI(), GuidCompressor.uncompressGuidString(guid));
                        }
                    });
                    System.out.println("Rootmap  size: "+rootmap.size());
                    System.out.println("IfcOwl model size: "+m.size());
                    System.out.println("Target name: "+rdfTargetName);
                    
                    generateModel(rootmap, m, rdfTargetName);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    tempFile.deleteOnExit();
                }

            } else
                r.convert(ifcFileName, rdfTargetName, baseURI);
        } catch (Exception e) {
            e.printStackTrace();
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
        System.out.println("out model size: "+out_model.size());
        try (OutputStream out = new FileOutputStream(rdfTargetName)) {
            RDFDataMgr.write(out, out_model, Lang.N3);
            out.flush();
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
    
    @Subscribe
    public void handleEvent(final SystemErrorEvent event) {
        System.out.println("error: " + event.getStatus_message());
    }

    @Subscribe
    public void handleEvent(final SystemStatusEvent event) {
        System.out.println("message: " + event.getStatus_message());
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
