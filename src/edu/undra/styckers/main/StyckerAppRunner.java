package edu.undra.styckers.main;

import edu.undra.styckers.SourceApi;
import edu.undra.styckers.StyckerApp;

/**
 *
 * @date 25 de jul. de 2022 17.14.38
 * @author alexandre
 */
public class StyckerAppRunner {

    public static void main(String[] args) {

        String outputFolder = "styckers";//the folder to save styckers into
        
        StyckerApp.generateStyckers(outputFolder, SourceApi.API);

    }

}
