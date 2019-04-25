/*
 * The GNU Affero General Public License
 * 
 * Copyright (c) 2015 Jyrki Oraskari (Jyrki.Oraskari@aalto.fi / rkiorri@gmail.com)
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

/*
 * To compile this, Java 11 is needed. jfxrt.jar is included, so, the the plugin should not be mandatory
 * but installing the http://www.eclipse.org/efxclipse/index.html and http://gluonhq.com/open-source/scene-builder/
 * make coding easier. 
 * 
 */

package fi.ni.gui.fx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

@SuppressWarnings("restriction")
public class IFC2RDF_Desktop extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("IFC2RDF.fxml"));
        Scene scene = new Scene(root);
        stage.setTitle("IFCtoRDF Desktop 2.1 for Java 11");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {

        org.apache.jena.query.ARQ.init();
        launch(args);
    }

}
