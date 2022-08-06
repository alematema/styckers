package edu.undra.styckers.util;

import edu.undra.styckers.util.JsonParser;
import edu.undra.styckers.util.Content;
import edu.undra.styckers.util.ContentExtractor;
import edu.undra.styckers.util.SourceType;
import java.util.List;
import java.util.Map;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * @date 23 de jul. de 2022 11.08.16
 * @author alexandre
 */
public class RegexInsuficiencyTest {

    public RegexInsuficiencyTest() {
    }

    @Test
    public void regexFailsCatchingKeyValuePairs_When_KeysAreDeeperNestedInJsonFile_Test() {

//        String aBitMoreDeeperNestedJson = "[{\"id\":\"tt5491994\",\"tags\":[{\"rank\":\"string\"}], \"rank\":\"1\", \"title\":\"Planet Earth II\", \"fullTitle\":\"Planet Earth II (2016)\", \"year\":\"2016\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/TopTVs_1.jpg\", \"crew\":\"David Attenborough, Chadden Hunter\", \"imDbRating\":\"9.4\", \"imDbRatingCount\":\"137192\"}]";
//        String aBitMoreDeeperNestedJson = "[{\"id\":\"tt5491994\",\"tags\":[{\"rank\":\"string\"}]}]";
//        String aBitMoreDeeperNestedJson = "[{\"tags\":{\"rank\":\"string\"}}]";
        String aBitMoreDeeperNestedJson = "[{\"tags\":[{\"rank\":\"2.9\"},{\"rank\":\"1.5\"}]}]";
        JsonParser aluraJsonParser = new JsonParser();
        List<Map<String, String>> keyValuePairsList = aluraJsonParser.parse(aBitMoreDeeperNestedJson);

        assertTrue(!keyValuePairsList.isEmpty());

        /**
         * regex fails catching value for key=tags... 
         * even worse, regex ignores the tags key...
         * the lines bellow asserts true that no map contains the tags key 
         */
        for (Map<String, String> keyValuePairs : keyValuePairsList) {

            assertTrue(!keyValuePairs.containsKey("\"tags\""));
            assertTrue(!keyValuePairs.containsKey("tags"));

        }

    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void jsonParserThrowsExceptionParsingABitMoreNestedJson() {

        String aBitMoreDeeperNestedJson = "{\n"
                + "  \"id\": 1,\n"
                + "  \"name\": \"John Appleseed\",\n"
                + "  \"work\": {\n"
                + "    \"title\": \"Engineer\",\n"
                + "    \"company\": \"Acme\"\n"
                + "  },\n"
                + "  \"phones\": [\n"
                + "    {\n"
                + "      \"phone\": \"0000000000\",\n"
                + "      \"type\": \"mobile\"\n"
                + "    }\n"
                + "  ]\n"
                + "}";
        aBitMoreDeeperNestedJson = "{\"id\": 1,\"name\": \"John Appleseed\", \"work\":"
                + " {\"title\": \"Engineer\",\"company\": \"Acme\"},"
                + "  \"phones\": ["
                + "    {\"phone\": \"0000000000\","
                + "      \"type\": \"mobile\"\n"
                + "    }\n"
                + "  ]\n"
                + "}";

        //WORKS
        List<Content> contents = ContentExtractor.extract(aBitMoreDeeperNestedJson, SourceType.JSON, "phones", "phone");
        System.out.println(contents);

        //DOES NOT WORK
        JsonParser aluraJsonParser = new JsonParser();
        aluraJsonParser.parse(aBitMoreDeeperNestedJson);

    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }
}
