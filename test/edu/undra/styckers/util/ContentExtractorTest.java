package edu.undra.styckers.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
public class ContentExtractorTest {
        static String shallowNestingJson
            = "[{\"success\": \"string\",\"image\": \"images/cross-roads.jpeg\",\"length\": \"numeral\"},"
            + "{\"success\": \"string2\",\"image\": \"images/over-cross-roads.jpeg\",\"length\": 102}]";
    static String deepNestingJson = "[{\n"
            + "\"success\": \"string\",\"contents\": {\n"
            + "    \"                               quotes\": [\n"
            + "                                                {\n"
            + "                                                  \"author\": \"Zembabuir Cross Roads Government\",\n"
            + "                                                  \"quote\": \"string\",\n"
            + "                                                  \"tags\": [\n"
            + "                                                    \"string\"\n"
            + "                                                  ],\n"
            + "                                                  \"id\": \"string\",\n"
            + "                                                  \"image\": \"images/cross-roads.jpeg\",\n"
            + "                                                  \"length\":   456e10\n"
            + "                                                }\n"
            + "                                            ]\n"
            + "                                }\n"
            + "},\n"
            + "{\n"
            + "\"success\": \"keep walking\",\"contents\": {\n"
            + "    \"                               quotes\": [\n"
            + "                                                {\n"
            + "                                                  \"author\": \"AC Cesar & Cesars Entreprises\",\n"
            + "                                                  \"quote\": \"string\",\n"
            + "                                                  \"tags\": [\n"
            + "                                                    \"string\"\n"
            + "                                                  ],\n"
            + "                                                  \"id\": \"string\",\n"
            + "                                                  \"image\": \"images/containers.jpeg\",\n"
            + "                                                  \"length\":   456e10\n"
            + "                                                }\n"
            + "                                            ]\n"
            + "                                }\n"
            + "}\n"
            + "]";

    static List<Content> contents = new ArrayList<>();

    public ContentExtractorTest() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void shallowNestingJsonExtraction_ReturnsRightContents_When_PassedAnyValidKeysTest() {

        contents.clear();
        contents = ContentExtractor.extract(shallowNestingJson, SourceType.JSON, "success");
        assertEquals(2, contents.size());
        assertEquals(1, contents.get(0).keys().size());
        assertEquals(1, contents.get(1).keys().size());

        contents = ContentExtractor.extract(shallowNestingJson, SourceType.JSON, "success", "image");
        assertEquals(2, contents.size());
        assertEquals(2, contents.get(0).keys().size());
        assertEquals(2, contents.get(1).keys().size());

        contents = ContentExtractor.extract(shallowNestingJson, SourceType.JSON, "success", "image", "length");
        assertEquals(2, contents.size());
        assertEquals(3, contents.get(0).keys().size());
        assertEquals(3, contents.get(1).keys().size());

        assertEquals(contents.get(0).get(doubleQuote("success")), "\"string\"");
        assertEquals(contents.get(1).get(doubleQuote("success")), "\"string2\"");

        contents = ContentExtractor.extract(shallowNestingJson, SourceType.JSON, "invalidKey", "success", "image", "length");
        assertEquals(0, contents.size());

    }

    @Test
    public void deepNestingJsonExtraction_ReturnsRightContents_When_PassedAnyValidKeysTest() throws FileNotFoundException {

        //shallow matching remains OK
        //Tests passed
        contents.clear();
        contents = ContentExtractor.extract(deepNestingJson, SourceType.JSON, "success");
        assertEquals(2, contents.size());
        assertEquals(1, contents.get(0).keys().size());
        assertEquals(1, contents.get(1).keys().size());

        contents = ContentExtractor.extract(deepNestingJson, SourceType.JSON, "success", "image");
        assertEquals(2, contents.size());
        assertEquals(2, contents.get(0).keys().size());
        assertEquals(2, contents.get(1).keys().size());

        contents = ContentExtractor.extract(deepNestingJson, SourceType.JSON, "success", "image", "length");
        assertEquals(2, contents.size());
        assertEquals(3, contents.get(0).keys().size());
        assertEquals(3, contents.get(1).keys().size());

        contents = ContentExtractor.extract(deepNestingJson, SourceType.JSON, "invalidKey", "success", "image", "length");
        assertEquals(0, contents.size());

        contents = ContentExtractor.extract(deepNestingJson, SourceType.JSON, "success", "image", "length", "contents");
        assertEquals(2, contents.size());
        assertEquals(4, contents.get(0).keys().size());
        assertEquals(4, contents.get(1).keys().size());
        assertTrue(contents.get(0).hasSameKeys(doubleQuote("success", "image", "length", "contents")));

        Scanner input = new Scanner(new File("tests-input-deep-nesting-json"));
        String deepNestingJsonn = "";
        while (input.hasNextLine()) {
            deepNestingJsonn += input.nextLine();
        }

        contents = ContentExtractor.extract(deepNestingJsonn, SourceType.JSON, "success");
        assertEquals(2, contents.size());
        assertEquals(1, contents.get(0).keys().size());
        assertEquals(1, contents.get(1).keys().size());

        contents = ContentExtractor.extract(deepNestingJsonn, SourceType.JSON, "success", "image");
        assertEquals(2, contents.size());
        assertEquals(2, contents.get(0).keys().size());
        assertEquals(2, contents.get(1).keys().size());

        contents = ContentExtractor.extract(deepNestingJsonn, SourceType.JSON, "success", "image", "length");
        assertEquals(2, contents.size());
        assertEquals(3, contents.get(0).keys().size());
        assertEquals(3, contents.get(1).keys().size());

        contents = ContentExtractor.extract(deepNestingJsonn, SourceType.JSON, "invalidKey", "success", "image", "length");
        assertEquals(0, contents.size());

        contents = ContentExtractor.extract(deepNestingJsonn, SourceType.JSON, "success", "image", "author", "contents");
        assertEquals(2, contents.size());
        assertEquals(4, contents.get(0).keys().size());
        assertEquals(4, contents.get(1).keys().size());
        assertTrue(contents.get(0).hasSameKeys(doubleQuote("success", "image", "author", "contents")));

        assertEquals(contents.get(0).get(doubleQuote("success")), doubleQuote("string"));
        assertEquals(contents.get(0).get(doubleQuote("author")), doubleQuote("Zembabuir Cross Roads Government"));

        assertEquals(contents.get(1).get(doubleQuote("success")), doubleQuote("keep walking"));
        assertEquals(contents.get(1).get(doubleQuote("author")), doubleQuote("AC Cesar & Cesars Entreprises"));

        assertEquals(contents.get(0).get(doubleQuote("image")), doubleQuote("images/cross-roads.jpeg"));
        assertEquals(contents.get(1).get(doubleQuote("image")), doubleQuote("images/containers.jpeg"));

        Scanner oneliner = new Scanner("{\n"
                + "    \"                               quotes\": [\n"
                + "                                                {\n"
                + "                                                  \"author\": \"AC Cesar & Cesars Entreprises\",\n"
                + "                                                  \"quote\": \"string\",\n"
                + "                                                  \"tags\": [\n"
                + "                                                    \"string\"\n"
                + "                                                  ],\n"
                + "                                                  \"id\": \"string\",\n"
                + "                                                  \"image\": \"images/containers.jpeg\",\n"
                + "                                                  \"length\":   456e10\n"
                + "                                                }\n"
                + "                                            ]\n"
                + "                                }");

        String onelineContents = "";
        while (oneliner.hasNextLine()) {
            onelineContents += oneliner.nextLine();
        }
        //"{  quotes\": [{\"author\": \"AC Cesar & Cesars Entreprises\",\"quote\": \"string\", \"tags\": [\"string\" ],\"id\": \"string\",\"image\": \"images/containers.jpeg\", \"length\":   456e10}]}"
        assertEquals(contents.get(1).get(doubleQuote("contents")), onelineContents);

        String json = "{\"items\":[\n"
                + "\n"
                + "{\"id\":\"tt4574334\", \"rank\":\"1\", \"rankUpDown\":\"0\", \"title\":\"Stranger Things\", \"fullTitle\":\"Stranger Things (2016)\", \"year\":\"2016\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/MostPopularTVs_1.jpg\", \"crew\":\"Millie Bobby Brown, Finn Wolfhard\", \"imDbRating\":\"8.7\", \"imDbRatingCount\":\"1092729\"},\n"
                + "{\"id\":\"tt1190634\", \"rank\":\"2\", \"rankUpDown\":\"0\", \"title\":\"The Boys\", \"fullTitle\":\"The Boys (2019)\", \"year\":\"2019\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/MostPopularTVs_2.jpg\", \"crew\":\"Karl Urban, Jack Quaid\", \"imDbRating\":\"8.7\", \"imDbRatingCount\":\"418768\"},\n"
                + "{\"id\":\"tt11743610\", \"rank\":\"3\", \"rankUpDown\":\"-1\", \"title\":\"The Terminal List\", \"fullTitle\":\"The Terminal List (2022)\", \"year\":\"2022\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/MostPopularTVs_3.jpg\", \"crew\":\"Chris Pratt, Constance Wu\", \"imDbRating\":\"8.1\", \"imDbRatingCount\":\"37067\"}],\n"
                + "        \"errorMessage\":\"\"}";

        contents = ContentExtractor.extract(json, SourceType.JSON, "items");
        assertEquals(contents.size(), 1);
        contents = ContentExtractor.extract((String) contents.get(0).get(doubleQuote("items")), SourceType.JSON, "imDbRating", "id", "rank", "title", "fullTitle");
        assertEquals(contents.size(), 3);
    }

    @Test
    public void recursiveProcessingNestedKeysTest() throws FileNotFoundException {

        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input-recursive-content-extractor")), SourceType.JSON, "contents", "author");

        assertEquals(contents.size(), 1);

        String expected = "{\n"
                + "        \"quotes\": [\n"
                + "        {\"author\": \"recursivelly-found-author\",\n"
                + "                \"quote\": \"string\",\n"
                + "                \"tags\": [\n"
                + "                        \"string\"\n"
                + "                ],\n"
                + "                \"id\": \"string\",\n"
                + "                \"image\": \"string\",\n"
                + "                \"length\": 0\n"
                + "            }\n"
                + "        ]\n"
                + "        }";
        expected = oneline(expected);
        assertEquals(contents.get(0).get(doubleQuote("contents")), expected);
        expected = doubleQuote("recursivelly-found-author");
        assertEquals(contents.get(0).get(doubleQuote("author")), expected);

        contents = ContentExtractor.extract(new FileInputStream(new File("input-recursive-content-extractor_1")), SourceType.JSON, "items");
        assertEquals(contents.size(), 1);
        expected = "[\n"
                + "\n"
                + "{\"id\":\"tt4574334\", \"rank\":\"1\", \"rankUpDown\":\"0\", \"title\":\"Stranger Things\", \"fullTitle\":\"Stranger Things (2016)\", \"year\":\"2016\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/MostPopularTVs_1.jpg\", \"crew\":\"Millie Bobby Brown, Finn Wolfhard\", \"imDbRating\":\"8.7\", \"imDbRatingCount\":\"1092729\"},\n"
                + "{\"id\":\"tt1190634\", \"rank\":\"2\", \"rankUpDown\":\"0\", \"title\":\"The Boys\", \"fullTitle\":\"The Boys (2019)\", \"year\":\"2019\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/MostPopularTVs_2.jpg\", \"crew\":\"Karl Urban, Jack Quaid\", \"imDbRating\":\"8.7\", \"imDbRatingCount\":\"418768\"},\n"
                + "{\"id\":\"tt11743610\", \"rank\":\"3\", \"rankUpDown\":\"-1\", \"title\":\"The Terminal List\", \"fullTitle\":\"The Terminal List (2022)\", \"year\":\"2022\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/MostPopularTVs_3.jpg\", \"crew\":\"Chris Pratt, Constance Wu\", \"imDbRating\":\"8.1\", \"imDbRatingCount\":\"37067\"}]";
        expected = oneline(expected);
        assertEquals(contents.get(0).get(doubleQuote("items")), expected);

        contents = ContentExtractor.extract((String) contents.get(0).get(doubleQuote("items")), SourceType.JSON, "title", "id");
        assertEquals(contents.size(), 3);
        
        String[] ids = {"tt4574334","tt1190634","tt11743610"};
        String[] titles = {"Stranger Things","The Boys","The Terminal List"};
        for(int i = 0; i < 3; i++){
            
            String id = doubleQuote(ids[i]);
            String title = doubleQuote(titles[i]);
            Content content=null;
            for(Content c : contents){
                if(c.get(doubleQuote("id")).equals(id)){
                    content = c;
                    break;
                }
            }
            assertEquals(content.get(doubleQuote("id")), id);            
            assertEquals(content.get(doubleQuote("title")), title);            
        }

    }
    
    @Test
    public void naiveparsingTest() throws FileNotFoundException{
        
        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input-naive-parsing")), SourceType.JSON, "id", "title");

        assertEquals(contents.size(),3);
        
        String[] ids = {"tt4574334","tt1190634","tt11743610"};
        String[] titles = {"Stranger Things","The Boys","The Terminal List"};
        for(int i = 0; i < 3; i++){
            
            String id = doubleQuote(ids[i]);
            String title = doubleQuote(titles[i]);
            Content content=null;
            for(Content c : contents){
                if(c.get(doubleQuote("id")).equals(id)){
                    content = c;
                    break;
                }
            }
            assertEquals(content.get(doubleQuote("id")), id);            
            assertEquals(content.get(doubleQuote("title")), title);            
        }
        
        contents = ContentExtractor.extract(new FileInputStream(new File("input-naive-parsing-2")), SourceType.JSON, "id", "title");
        assertEquals(contents.size(),3);
        
        Content c = contents.get(0);
        assertNotNull(c.get(doubleQuote("id")));
        List<String> value = new ArrayList<>();
        value.add(doubleQuote(ids[0]));
        value.add(doubleQuote(ids[1]));
        value.add(doubleQuote(ids[2]));
        assertEquals(c.get(doubleQuote("id")),value);
        assertEquals(c.get(doubleQuote("title")),doubleQuote(titles[0]));
        
        for(int i = 1; i < 3; i++){
            
            String id = doubleQuote(ids[i]);
            String title = doubleQuote(titles[i]);
            Content content=null;
            for(Content contentt : contents){
                if(contentt.get(doubleQuote("id")).equals(id)){
                    content = contentt;
                    break;
                }
            }
            assertEquals(content.get(doubleQuote("id")), id);            
            assertEquals(content.get(doubleQuote("title")), title);            
        }
        
        
        contents = ContentExtractor.extract(new FileInputStream(new File("input-naive-parsing-2")), SourceType.JSON, "ids");
        assertEquals(contents.size(),1);
        
        String expected = "[{\"id\":\"tt4574334\"},{\"id\":\"tt1190634\"},{\"id\":\"tt11743610\"}]";
        assertEquals(contents.get(0).get(doubleQuote("ids")),expected);
        
        
        contents = ContentExtractor.extract(new FileInputStream(new File("input-naive-parsing-3")), SourceType.JSON, "id", "title");
        assertEquals(contents.size(),1);
        
        value = new ArrayList<>();
        value.add(doubleQuote(ids[0]));
        value.add(doubleQuote(ids[1]));
        value.add(doubleQuote(ids[2]));
        
        Content content = contents.get(0);
        List<String> values = ((List<String>)content.get(doubleQuote("id")));
        assertEquals(values.get(0), value.get(0));
        assertEquals(values.get(1), value.get(1));
        assertEquals(values.get(2), value.get(2));
        
        value = new ArrayList<>();
        value.add(doubleQuote(titles[0]));
        value.add(doubleQuote(titles[1]));
        value.add(doubleQuote(titles[2]));
        
        content = contents.get(0);
        values = ((List<String>)content.get(doubleQuote("title")));
        assertEquals(values.get(0), value.get(0));
        assertEquals(values.get(1), value.get(1));
        assertEquals(values.get(2), value.get(2));
       
        
    }

    @Test
    public void badKeyValuePairsFormatJsonTest() throws FileNotFoundException, IOException {

        contents = ContentExtractor.extract(new FileInputStream("tests-input-bad-key-value-pair-format-json"), SourceType.JSON, "badnumeral1st", "badnumeral2nd", "badnumeral3rd");
        assertEquals(contents.get(0).get(doubleQuote("badnumeral1st")), "012");
        assertEquals(contents.get(0).get(doubleQuote("badnumeral2nd")), "123");

        contents = ContentExtractor.extract(new FileInputStream("tests-input-bad-key-value-pair-format-json"), SourceType.JSON, "valueless");
        assertEquals(contents.get(0).get(doubleQuote("valueless")), "");

        contents = ContentExtractor.extract(new FileInputStream("tests-input-bad-key-value-pair-format-json"), SourceType.JSON, "badstringvalue");
        assertEquals(contents.size(), 0);

        contents = ContentExtractor.extract(new FileInputStream("tests-input-bad-key-value-pair-format-json"), SourceType.JSON, "badsquarebracket");
        assertEquals(contents.get(0).get(doubleQuote("badsquarebracket")), "");

        contents = ContentExtractor.extract(new FileInputStream("tests-input-bad-key-value-pair-format-json"), SourceType.JSON, "badbracket");
        assertEquals(contents.get(0).get(doubleQuote("badbracket")), "");

        contents = ContentExtractor.extract(new FileInputStream("tests-input-bad-key-value-pair-format-json"), SourceType.JSON, "id");
        assertEquals(contents.get(0).get(doubleQuote("id")), "");

    }

    @Test
    public void shrinkEagerlyTest() {

        String shrinked = ContentExtractor.shrinkEagerly("999abc123fg987");
        assertEquals(shrinked, "999");

        shrinked = ContentExtractor.shrinkEagerly("999abc");
        assertEquals(shrinked, "999");

        shrinked = ContentExtractor.shrinkEagerly("876");
        assertEquals(shrinked, "876");

        shrinked = ContentExtractor.shrinkEagerly("1a2b3c 4d2000");
        assertEquals(shrinked, "1");

        shrinked = ContentExtractor.shrinkEagerly("12a2b3c 4d2000");
        assertEquals(shrinked, "12");

        shrinked = ContentExtractor.shrinkEagerly("a123456789");
        assertEquals(shrinked, "");

        shrinked = ContentExtractor.shrinkEagerly("abcd");
        assertEquals(shrinked, "");

        shrinked = ContentExtractor.shrinkEagerly("");
        assertEquals(shrinked, "");

        shrinked = ContentExtractor.shrinkEagerly("{123");
        assertEquals(shrinked, "");

        shrinked = ContentExtractor.shrinkEagerly("123 abc 456");
        assertEquals(shrinked, "123");

    }

    private static Object[] doubleQuote(String... keys) {
        Object[] doubleQuoted = new Object[keys.length];

        for (int i = 0; i < keys.length; i++) {
            doubleQuoted[i] = "\"" + keys[i] + "\"";
        }

        return doubleQuoted;
    }

    private static String doubleQuote(String key) {
        return "\"" + key + "\"";
    }

    private String oneline(String multiLine) {
        StringBuilder oneLine = new StringBuilder();

        Scanner s = new Scanner(multiLine);
        while (s.hasNextLine()) {
            oneLine.append(s.nextLine());
        }

        return oneLine.toString();
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
