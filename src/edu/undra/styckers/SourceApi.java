package edu.undra.styckers;

import edu.undra.styckers.util.Content;
import edu.undra.styckers.util.SourceType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This enumeration placeholds algorithms that deal with specific content's
 * key=value pairs sets. <br>
 * Each algorithm is coded as a lambda expression.<br>
 * Most of time, the key=value pair set vary from API to API. <br>
 * For instance, the way we process key=value pairs from IMDB differs to
 * much<br>
 * from the way we process Nasa's content's key=value pairs. <br>
 * If we want styckers from another API, we just create another lambda
 * expression containing <br>
 * code to handle the content we want to retrieve from the API <br>
 * and handle the specifics we want, before calling the stycker generator. <br>
 *
 * @author alexandre
 */
public enum SourceApi {

    /**
     * url : https://mocki.io/v1/9a7c1ca9-29b4-4eb3-8306-1adb9d159060
     * keys="title", "image", "imDbRating"
     */
    IMDB((outputFolder) -> {

        try {

            List<Content> contents = StyckerApp.getContents("https://mocki.io/v1/9a7c1ca9-29b4-4eb3-8306-1adb9d159060", SourceType.JSON, "image", "title", "imDbRating");

            for (Content movie : contents) {

                System.out.println(movie);

                InputStream inputStream = new URL(((String) movie.get("\"image\"")).substring(1, ((String) movie.get("\"image\"")).length() - 1).replaceAll("\\._..........................", "")).openStream();
                String cleanerTitle = ((String) movie.get("\"title\"")).replaceAll(" ", "-").replaceAll(":", "");
                String styckerName = outputFolder + File.separator + cleanerTitle + ".png";

                String ratingText = StyckerApp.FIVE_STARS;
                double rate = Double.parseDouble(((String) movie.get("\"imDbRating\"")).replaceAll("\"", ""));
                if (rate <= 1.99) {
                    ratingText = StyckerApp.ONE_STAR;
                } else if (rate <= 3.99) {
                    ratingText = StyckerApp.TWO_STARS;
                } else if (rate <= 5.99) {
                    ratingText = StyckerApp.THREE_STARS;
                } else if (rate <= 9.00) {
                    ratingText = StyckerApp.FOUR_STARS;
                }

                StyckerApp.generate(inputStream, styckerName, ratingText);
            }

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(StyckerApp.class.getName()).log(Level.SEVERE, null, ex);
        }
        /**
         * keys="title", "url"
         */
    }),
    /**
     * url :
     * "https://raw.githubusercontent.com/alura-cursos/imersao-java/api/NASA-APOD.json"
     *
     * <br>keys="title", "url"
     */
    NASA((outputFolder) -> {

        try {

            List<Content> contents = StyckerApp.getContents("https://raw.githubusercontent.com/alura-cursos/imersao-java/api/NASA-APOD.json", SourceType.JSON, "title", "url");

            for (Content nasaImage : contents) {

                System.out.println(nasaImage);

                InputStream inputStream = new URL(((String) nasaImage.get("\"url\"")).substring(1, ((String) nasaImage.get("\"url\"")).length() - 1).replaceAll("\\._..........................", "")).openStream();
                String cleanerTitle = ((String) nasaImage.get("\"title\"")).replaceAll(" ", "-").replaceAll(":", "");
                String styckerName = outputFolder + File.separator + cleanerTitle + ".png";

                StyckerApp.generate(inputStream, styckerName, "NASA@NADA");
            }

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(StyckerApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }),
    /**
     * url :
     * https://raw.githubusercontent.com/alura-cursos/imersao-java/api/MostPopularTVs.json
     * <br>keys="fullTitle", "image", "imDbRating"
     */
    TV_MOST_POP((String outputFolder) -> {

        try {

            List<Content> contents = StyckerApp.getContents("https://raw.githubusercontent.com/alura-cursos/imersao-java/api/MostPopularTVs.json", SourceType.JSON, "fullTitle", "image", "imDbRating");

            for (Content movie : contents) {

                System.out.println(movie);

                InputStream inputStream = new URL(((String) movie.get("\"image\"")).substring(1, ((String) movie.get("\"image\"")).length() - 1).replaceAll("\\._..........................", "")).openStream();
                String cleanerTitle = ((String) movie.get("\"fullTitle\"")).replaceAll(" ", "-").replaceAll(":", "");
                String styckerName = outputFolder + File.separator + cleanerTitle + ".png";

                String ratingText = StyckerApp.FIVE_STARS;
                double rate = Double.parseDouble(stripDoubleQuotes((String) movie.get("\"imDbRating\"")));
                if (rate <= 1.99) {
                    ratingText = StyckerApp.ONE_STAR;
                } else if (rate <= 3.99) {
                    ratingText = StyckerApp.TWO_STARS;
                } else if (rate <= 5.99) {
                    ratingText = StyckerApp.THREE_STARS;
                } else if (rate <= 9.00) {
                    ratingText = StyckerApp.FOUR_STARS;
                }

                StyckerApp.generate(inputStream, styckerName, ratingText);
            }

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(StyckerApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }),
    /**
     * url :
     * <br>keys="author", "image"
     */
    LOCAL((var outputFolder) -> {

        try {

            List<Content> contents = StyckerApp.getContents(new FileInputStream(new File("json-local")), SourceType.JSON, "author", "image");

            for (Content json : contents) {

                System.out.println(json);

//                InputStream inputStream = new FileInputStream(new File(((String) json.get("\"image\"")).substring(1, ((String) json.get("\"image\"")).length() - 1)));
                InputStream inputStream = new URL("file:///" + System.getProperty("user.dir") + File.separator + ((String) json.get("\"image\"")).substring(1, ((String) json.get("\"image\"")).length() - 1)).openStream();
                String authorFullName = ((String) json.get("\"author\""));
                authorFullName = stripDoubleQuotes(authorFullName);
                String styckerName = outputFolder + File.separator + authorFullName.replaceAll(" ", "-") + ".png";

                String styckerFooterText = "";

                Scanner s = new Scanner(authorFullName);

                if (s.hasNext()) {
                    styckerFooterText += (s.next().charAt(0) + "").toUpperCase();
                }
                while (s.hasNext()) {
                    styckerFooterText += ".";
                    styckerFooterText += (s.next().charAt(0) + "").toUpperCase();
                }
                
                StyckerApp.generate(inputStream, styckerName, styckerFooterText);
            }

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(StyckerApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }),
    /**
     * url : http://localhost:8080/linguagens
     * <br>keys="image", "title", "ranking"
     */
    LOCAL_HOST_LANGUAGES((var outputFolder) -> {

        try {

            List<Content> contents = StyckerApp.getContents("http://localhost:8080/linguagens", SourceType.JSON, "title", "image", "ranking");

            for (Content programmingLanguages : contents) {

                System.out.println(programmingLanguages);

                InputStream inputStream = new URL(((String) programmingLanguages.get("\"image\"")).substring(1, ((String) programmingLanguages.get("\"image\"")).length() - 1).replaceAll("\\._..........................", "")).openStream();
                String cleanerTitle = ((String) programmingLanguages.get("\"title\"")).replaceAll(" ", "-").replaceAll(":", "");
                String styckerName = outputFolder + File.separator + stripDoubleQuotes(cleanerTitle) + ".png";

                String ratingText = "GET BACK";
                double rate = Double.parseDouble(stripDoubleQuotes((String) programmingLanguages.get("\"ranking\"")));
                if (rate <= 1.99) {
                    ratingText = "WE LOVE YOU";
                } else if (rate <= 3.99) {
                    ratingText = "GREAT ONE";
                } else if (rate <= 5.99) {
                    ratingText = "NICE LANGUAGE";
                } else if (rate <= 9.00) {
                    ratingText = "BAD BYTES";
                }

                StyckerApp.generate(inputStream, styckerName, ratingText);
            }

        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(StyckerApp.class.getName()).log(Level.SEVERE, null, ex);
        }

    }),
    /**
     * The current API
     */
    API(LOCAL.getAlgorithm());

    /**
     * This enumeration place holds algorithms that deal with specific content's
     * key=value pairs sets. <br>
     * Each algorithm is coded as a lambda expression.<br>
     * Most of time, the key=value pair set vary from API to API. <br>
     * For instance, the way we process key=value pairs from IMDB differs to
     * much<br>
     * from the way we process Nasa's content's key=value pairs. <br>
     * If we want styckers from another API, we just create another lambda
     * expression containing <br>
     * code to handle the content we want to retrieve from the API <br>
     * and handle the specifics we want, before calling the stycker generator.
     * <br>
     *
     */
    public Consumer<String> algorithm;

    private SourceApi(Consumer<String> algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * This enumaration place holds algorithms that deal with specific content's
     * key=value pairs sets. <br>
     * Each algorithm is coded as a lambda expression.<br>
     * Most of time, the key=value pair set vary from API to API. <br>
     * For instance, the way we process key=value pairs from IMDB differs to
     * much<br>
     * from the way we process Nasa's content's key=value pairs. <br>
     * If we want styckers from another API, we just create another lambda
     * expression containing <br>
     * code to handle the content we want to retrieve from the API <br>
     * and handle the specifics we want, before calling the stycker generator.
     * <br>
     *
     * @return an algorithm
     */
    public Consumer<String> getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(SourceApi api) {
        this.algorithm = api.getAlgorithm();
    }

    /**
     * Double quotes a string
     *
     * @param key
     * @return string double quoted
     */ 
    public static String doubleQuote(String key) {
        return StyckerApp.doubleQuote(key);
    }

    /**
     * Strips off double quotes.
     *
     * @param s
     * @return
     */
    public static String stripDoubleQuotes(String s) {
        return StyckerApp.stripDoubleQuotes(s);
    }

}
