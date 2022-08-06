package edu.undra.styckers;

import edu.undra.styckers.util.Content;
import edu.undra.styckers.util.ContentExtractor;
import edu.undra.styckers.util.Http;
import edu.undra.styckers.util.SourceType;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * The stycker app. <br>
 *
 * @date 25 de jul. de 2022 15.03.55
 * @author alexandre
 */
public abstract class StyckerApp {

    //DEFAULT CONTENT RATING TEXTS
    public static final String FIVE_STARS = "TOPZERA";//THE SUPREME 
    public static final String FOUR_STARS = "Bom D+";//THE BEST
    public static final String THREE_STARS = "O WC pode esperar...";//THE NOT BAD
    public static final String TWO_STARS = "Alguem assiste isso? ";//THE BAD
    public static final String ONE_STAR = "Já acabou o filme? ";//THE LOWEST

    /**
     *
     * Extracts contents from the url sourceUrl<br>
     * The type of text file is required for processing : JSON, HTML, CSV , XML
     * etc.<br>
     * Example:if we want extract the values of key="image" and key="title" in
     * each json object, we pass them as <br>
     * argument call to getContents method :<br>
     * <b>getContents("http://localhost:8080/movies",SourceType.JSON,"title","image")</b>
     *
     * @param sourceUrl the source url we are going to retrieve the json from
     * @param sourceType JSON, HTML, CSV , XML, etc
     * @param keys the keys we are interested in to know their values in source
     * @return a list of Content
     * @throws IOException I'm a risky method. Please, handle exception if I
     * fail.
     * @throws InterruptedException I'm a risky method. Please, handle exception
     * if I fail.
     */
    public static List<Content> getContents(String sourceUrl, SourceType sourceType, String... keys) throws IOException, InterruptedException {
        return ContentExtractor.extract(Http.GET(sourceUrl).body(), sourceType, keys);
    }

    /**
     *
     * Extracts contents from the InputStream source<br>
     * The type of text file is required for processing : JSON, HTML, CSV , XML
     * etc.<br>
     * Example:if we want extract the values of key="image" and key="title" in
     * each json object, we pass them as <br>
     * argument call to getContents method :<br>
     *  <b>getContents(new FileInputStream(new File("json-local")),SourceType.JSON,"title","image")</b>
     *
     * @param source the source url we are going to retrieve the json from
     * @param sourceType JSON, HTML, CSV , XML, etc
     * @param keys the keys we are interested in to know their values in source
     * @return a list of Content
     * @throws IOException I'm a risky method. Please, handle exception if I
     * fail.
     * @throws InterruptedException I'm a risky method. Please, handle exception
     * if I fail.
     */
    public static List<Content> getContents(InputStream source, SourceType sourceType, String... keys) throws IOException, InterruptedException {
        return ContentExtractor.extract(source, sourceType, keys);
    }

    /**
     * Creates stycker from an input stream's imageable bytes (source),<br>
     * draws styckerText into stycker's foot.<br>
     * and saves the new image (a stycker) into storage refered by styckerName.
     *
     * @param source the input stream's images bytes
     * @param styckerName storage location to save new image to
     * @param styckerText the text to write into the stycker
     * @throws IOException I'm a risky method. Please, handle exception if I
     * fail.
     */
    public static void generate(InputStream source, String styckerName, String styckerText) throws IOException {
        StyckerGenerator.generate(source, styckerName, styckerText);
    }

    /**
     * Creates stycker from an input stream's imageable bytes, the source,<br>
     * draws styckerText into stycker's foot.<br>
     * and writes the stycker's bytes to an output stream.
     *
     * @param source the input stream's images bytes
     * @param oPS the out put stream to write the bytes to
     * @param styckerText the text to write into the stycker
     * @throws IOException I'm a risky method. Please, handle exception if I
     * fail.
     */
    public static void generate(InputStream source, OutputStream oPS, String styckerText) throws IOException {
        StyckerGenerator.generate(source, oPS, styckerText);
    }

    /**
     * Executes an algorithm that holds invoking code to Http, ContentExtractor
     * and StyckerGenerator.<br>
     *
     * @param outputFolder the output folder name, WITHOUT slashes, to save the
     * styckers into
     * @param sourceUrl the source url we are going to retrieve the json from
     * @param algorithm a lambda expression containing executable code.
     */
    public static void generateStyckers(String outputFolder, String sourceUrl, BiConsumer<String, String> algorithm) {
        algorithm.accept(outputFolder, sourceUrl);
    }

    /**
     *
     * Executes an algorithm that holds invoking code Http, ContentExtractor and
     * StyckerGenerator.<br>
     *
     * @param outputFolder the output folder name, WITHOUT slashes, to save the
     * styckers into
     * @param algorithm a lambda expression containing executable code.
     */
    public static void generateStyckers(String outputFolder, Consumer<String> algorithm) {
        algorithm.accept(outputFolder);
    }

    /**
     * Executes an algorithm that holds invoking code to Http, ContentExtractor
     * and StyckerGenerator.<br>
     *
     * @param outputFolder the output folder name, WITHOUT slashes, to save the
     * styckers into
     * @param source
     */
    public static void generateStyckers(String outputFolder, SourceApi source) {
        source.getAlgorithm().accept(outputFolder);
    }

    public static void generateStyckers(String outputFolder, String souceUrl, String styckerName, String footerText, String image) {
        try {
            List<Content> contents = getContents(souceUrl, SourceType.JSON, image);
            for (Content content : contents) {

                System.out.println(content);

                InputStream inputStream = new URL(((String) content.get("\"" + image + "\"")).substring(1, ((String) content.get("\"" + image + "\"")).length() - 1)).openStream();
                if (styckerName.isEmpty()) {
                    styckerName = outputFolder + File.separator + ("image" + System.currentTimeMillis()) + (new Random().nextInt(100)) + ".png";
                }

                StyckerApp.generate(inputStream, styckerName, footerText);
            }
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(StyckerApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Double quotes a string
     *
     * @param key
     * @return string double quoted
     */
    public static String doubleQuote(String key) {
        return ContentExtractor.doubleQuote(key);
    }

    /**
     * Strips off double quotes.
     *
     * @param s
     * @return
     */
    public static String stripDoubleQuotes(String s) {
        return ContentExtractor.stripDoubleQuotes(s);
    }

}
