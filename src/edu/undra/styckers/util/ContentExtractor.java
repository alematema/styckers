package edu.undra.styckers.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.function.Function;

/**
 *
 * This class extracts contents from a source text file. The type of text file
 * is required for processing : JSON, HTML, CSV , XML etc.<br>
 * An appropriate algorithm is set up for handling JSON or HTML or XML or CSV
 * format. <br>
 * The algorithm then searches in for provided keys and computes a key=value
 * pairs set.<br>
 * As algorithm progresses, if for all the keys provided a value is found, then
 * it creates an instance of Content and stores<br>
 * the key=value pairs set in that instance. This process ends at EOF.<br>
 * After that, a Content's instances list is returned.
 * <br>
 * JSON extractor's algorithm consumes memory for storing an one(1) line copy of
 * the source's bytes.
 *
 * @date 23 de jul. de 2022 11.08.16
 * @author alexandre
 */
public class ContentExtractor {

    private static JsonState jsonState;//reference for State Design Pattern implementation
    private static final Map<Object, Object> processedKeyValues = new HashMap<>();
    private static int missingKeyValuePairsCount = 0;

    //HTML, CSV, XML support NOT implemented yet.
    private static List<Content> processHTML(String source, Object[] keys) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static List<Content> processCSV(String source, Object[] keys) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    private static List<Content> processXML(String source, Object[] keys) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /**
     * Extracts contents from a source text file. <br>
     * The type of text file is required for processing : JSON, HTML, CSV , XML
     * etc.<br>
     * An appropriate algorithm is set up for handling JSON or HTML or XML or
     * CSV format. <br>
     * The algorithm then searches in for provided keys and computes a key=value
     * pairs set.<br>
     * As algorithm progresses, if for all the keys provided a value is found,
     * then it creates an instance of Content and stores<br>
     * the key=value pairs set in that instance. This process ends at EOF.<br>
     * After that, a Content's instances list is returned.
     * <br>
     * JSON extractor's algorithm consumes memory for storing an one(1) line
     * copy of the source's bytes.
     *
     * @param source
     * @param sourceType
     * @param keys the keys we are interested in to know their values in source
     * file.
     * <br>
     * Example:if we want extract the values of key="image" and key="title" in
     * each json object, we pass them as <br>
     * argument call to extract method :
     * extract(source,sourceType,"title","image")
     * @return a list of Content
     */
    public static List<Content> extract(String source, SourceType sourceType, String... keys) {

        if (source == null || keys == null || sourceType == null) {
            throw new NullPointerException("source AND sourceType AND keys MUST BE NOT null.");
        }

        if (sourceType.equals(SourceType.JSON)) {
            return goJSON(source, keys);
        }

        throw new UnsupportedOperationException(sourceType.toString());

    }

    /**
     * Extracts contents from a source file. <br>
     * The type of text file is required for processing : JSON, HTML, CSV , XML
     * etc.<br>
     * An appropriate algorithm is set up for handling JSON or HTML or XML or
     * CSV format. <br>
     *
     * The algorithm then searches in for provided keys and computes a key=value
     * pairs set.<br>
     * As algorithm progresses, if for all the keys provided a value is found,
     * then it creates an instance of Content and stores<br>
     * the key=value pairs set in that instance. This process ends at EOF.<br>
     * After that, a Content's instances list is returned.
     * <br>
     * JSON extractor's algorithm consumes memory for storing an one(1) line
     * copy of the source's bytes.
     *
     * @param source
     * @param sourceType
     * @param keys the keys we are interested in to know their values in source
     * file.
     * <br>
     * Example:if we want extract the values of key="image" and key="title" in
     * each json object, we pass them as <br>
     * argument call to extract method :
     * extract(source,sourceType,"title","image")
     * @return a list of Content
     */
    public static List<Content> extract(InputStream source, SourceType sourceType, String... keys) {

        if (source == null || keys == null || sourceType == null) {
            throw new NullPointerException("source AND sourceType AND keys MUST BE NOT null.");
        }

        if (sourceType.equals(SourceType.JSON)) {
            return goJSON(source, keys);
        }

        throw new UnsupportedOperationException(sourceType.toString());

    }

    public static List<Content> extract(String source, Function<String, List<Content>> extractorAlgorithm) {
        return extractorAlgorithm.apply(source);
    }

    /**
     * Turns input into a one line string.<br>
     * Then, breaks it into substring that starts with { and ends with }<br>
     * that is, breaks the one lined input into json strings and process each js
     * object.<br>
     * Each substring representing a json is processed by a method calling:<br>
     * processJSON(json,keys);
     *
     * @param source
     * @param keys
     * @return a list of Content
     * @throws IllegalArgumentException
     */
    public static List<Content> goJSON(InputStream source, String[] keys) throws IllegalArgumentException {
        Scanner s = new Scanner(source);
        StringBuilder sb = new StringBuilder();
        while (s.hasNextLine()) {
            sb.append(s.nextLine());
        }
        return goJSON(sb.toString(), keys);
    }

    /**
     * Turns input into a one line string.<br>
     * Then, breaks it into substring that starts with { and ends with }<br>
     * that is, breaks the one lined input into json strings and process each js
     * object.<br>
     * Each substring representing a json is processed by a melhod calling:<br>
     * processJSON(json,keys);
     *
     * @param source
     * @param keys
     * @return a list of Content
     * @throws IllegalArgumentException
     */
    public static List<Content> goJSON(String source, String[] keys) throws IllegalArgumentException {

        if (!source.trim().substring(0, 1).equals("[") && !source.trim().substring(0, 1).equals("{")) {
            throw new IllegalArgumentException("source is not a JSON");
        }

        Scanner s = new Scanner(source);
        StringBuilder sb = new StringBuilder();
        while (s.hasNextLine()) {
            sb.append(s.nextLine());
        }

        List<Content> contents = new ArrayList<>();

        int identicals = 0;
        int start = -1;
        int end = -1;

        for (int i = 0; i < sb.length(); i++) {

            if (("" + sb.charAt(i)).equals("{")) {

                if (identicals == 0) {
                    start = i;
                }

                identicals++;

            } else if (("" + sb.charAt(i)).equals("}")) {

                --identicals;

                if (identicals == 0) {
                    end = i;
                }

            }

            //this is necessary to force interpreting correctly an array of jsons
            // [ {},{},...,{} ] or { [ {},{},...,{} ] }
            if (("" + sb.charAt(i)).equals("[")) {

                if (sb.substring(start + 1, i).trim().isEmpty()) {
                    identicals = 0;
                    start = -1;
                }

            }

            if (start != -1 && end != -1) {

                Content content = new Content();

                processJSON(sb.substring(start, end + 1), keys, content);

                if (content.hasSameKeys(doubleQuote(keys))) {
                    contents.add(content);
                }

                start = -1;
                end = -1;
            }

        }

        return contents;
    }

    /**
     * This algorithm searches in for provided keys and computes a key=value
     * pairs set.<br>
     * As algorithm progresses, if for all the keys provided a value is found,
     * then it creates an instance of Content and stores<br>
     * the key=value pairs set in that instance. This process ends at EOF.<br>
     * After that, a Content instances list is returned.
     * <br>
     * JSON extractor's algorithm consumes memory for storing an one(1) line
     * copy of the source's bytes.
     *
     * @param json a json file starting with { and ending with }
     * @param keys the keys we are interested in to know their values in source
     * file.
     * <br>
     * Example:if we want extract the values of key="image" and key="title" in
     * each json object, we pass them as <br>
     * argument call to processJSON method : processJSON(json,"title","image")
     * @return a Content instance
     */
    private static void processJSON(String json, String[] keys, Content content) {

        if (json == null) {
            throw new NullPointerException("json must be NOT null");
        }
        if (keys == null) {
            throw new NullPointerException("keys must be NOT null");
        }
        if (content == null) {
            throw new NullPointerException("content must be NOT null");
        }

        KeysState keysState = new KeysState(Arrays.asList(keys));
        jsonState = keysState;

        StringBuilder oneLineJson = new StringBuilder();

        //onelines the source and strips out spaces
        Scanner s = new Scanner(json);
        while (s.hasNextLine()) {
            String line = s.nextLine();
            oneLineJson.append(line);
        }

        //Processes each json charactere
        //The State Design Pattern handles each charactere the appropriate processing 
        for (int i = 0; i < oneLineJson.length(); i++) {
            jsonState.process(json.charAt(i), i, oneLineJson.toString(), content);
        }

    }

    /**
     * Placeholder class for ones that implement State Design Pattern
     */
    abstract class JsonState {

        abstract public void process(char c, int currentIndex, String json, Content content);
    }

    /**
     * Class that implements State Design Pattern
     */
    static class ValueState extends JsonState {

        private int identicals = 1;
        private String key;
        private final KeysState keyState;
        private String value = "";//the key's value
        private String starting;//the first valid character a key's value has
        private String current = "";//for recursive processing a possible nested key in this value
        private int currentIndex = -1;//for recursive processing a possible nested key in this value
        private final List<String> processedKeys = new ArrayList<>();
        private final Map<Object, Object> processedKeyValues = new HashMap<>();

        public ValueState(KeysState keyState) {
            this.keyState = keyState;
            keyState.getKeys().forEach(keyy -> {
                processedKeyValues.put(keyy, null);
            });
        }

        @Override
        public void process(char c, int currentIndex, String json, Content content) {

            if ((c + value).equals(" ")) {//consumes spaces
                //return;
            } else if (value.equals("")) {//memoizes first value's char
                value += "" + c;
                starting = "" + c;
            } else {

                value += "" + c;

                if (KeysState.anyKeyStartsWith(current + "" + c, keyState.getKeys())) {
                    current += "" + c;
                    if (this.currentIndex == -1) {
                        this.currentIndex = currentIndex;
                    }
                } else {

                    if (KeysState.hasKeyEquals(current, keyState.getKeys())) {
                        String[] keys = {stripDoubleQuotes(current.replace(":", ""))};
                        processJSON(json.substring(this.currentIndex), keys, content);
                        jsonState = this;
                    }

                    current = "";
                    this.currentIndex = -1;

                }

                switch (starting) {
                    //value is a double quoted string
                    case "\"":
                        processDoubleQuotedCase(c, content);
                        break;
                    //value is a json object
                    //eager behaviour:dont return after first match
                    case "{":
                        processeJsonObjectCase(c, content);
                        break;
                    //value is an array
                    //eager behaviour:dont return after first match
                    case "[":
                        processArrayCase(c, content);
                        break;
                    //value is anything, but not cases above
                    default:
                        processDefaultCase(c, content);
                        break;
                }

            }
        }

        //value is an array
        //eager behaviour:dont return after first match
        private void processArrayCase(char c, Content content) {
            //value is an array
            //eager behaviour:dont return after first match
            if ((c + "").equals("[")) {
                identicals++;
            }
            if ((c + "").equals("]")) {
                identicals--;
            }
            if ((c + "").equals("]") && identicals <= 0) { //ends a value reading
                endsAValueReading(content);
            }
        }

        //value is a json object
        //eager behaviour:dont return after first match
        private void processeJsonObjectCase(char c, Content content) {
            //value is a json object
            //eager behaviour:dont return after first match
            if ((c + "").equals("{")) {
                identicals++;
            }
            if ((c + "").equals("}")) {
                identicals--;
            }
            if ((c + "").equals("}") && identicals <= 0) { //ends a value reading
                endsAValueReading(content);
            }
        }

        //value is a double quoted string
        private void processDoubleQuotedCase(char c, Content content) {

            if ((c + "").equals("\"")) {
                endsAValueReading(content);
            }
        }

        /**
         * Value is anything but an array or a json object or a double quoted
         * string
         *
         * @param c
         * @param content
         */
        private void processDefaultCase(char c, Content content) {
            //value is anything, but not cases above
            //ends a value reading
            if ((c + "").equals("\"") || (c + "").equals(" ") || (c + "").equals("}") || (c + "").equals("]") || (c + "").equals(",")) {
                if ((value).startsWith("}")) {
                    value = "";
                    endsAValueReading(content);
                } else if ((value).startsWith("]")) {
                    value = "";
                    endsAValueReading(content);
                } else if ((value).equals(" ")) {
                    value = value.substring(0, value.length() - 1);
                    endsAValueReading(content);
                } else if ((value).startsWith(",")) {
                    value = "";
                    endsAValueReading(content);
                } else if ((value).startsWith("\"")) {
                    value = "";
                    endsAValueReading(content);
                } else {

                    value = shrinkEagerly(value.trim().replaceAll("\\s", ""));

                    endsAValueReading(content);

                }
            }
        }

        //ends a value reading
        /**
         * Ends a value reading.<br>
         * A value is the thing which is associated with the current key being
         * processed.<br>
         *
         *
         * Deals with logic for correctly setting and storing a key=value
         * pair.<br><br>
         * If a key, such as "title" is found twice or more times in a json,
         * then a <b>List</b> of<br>
         * values is created and associated with the key, and the key=value pair
         * is stored in a map<br>
         * Otherwise, if the "title" key is found only once, then a
         *
         *
         * @param content the content to be filled.
         */
        public void endsAValueReading(Content content) {

            setKeyValue(content);

            value = "";
            key = "";
            starting = "";
            identicals = 1;
            current = "";

            jsonState = keyState;//returns processing keys state

            keyState.reset();

        }

        /**
         * Deals with logic for correctly setting and storing a key=value
         * pair.<br><br>
         * If a key, such as "title" is found twice or more times in a json,
         * then a <b>List</b> of<br>
         * values is created and associated with the key, and the key=value pair
         * is stored in a map<br>
         * Otherwise, if the "title" key is found only once, then a
         * <b>String</b> value is associated with the key,and the key=value is
         * put in the map.<br>
         *
         */
        private void setKeyValue(Content content) {

            if (content.get(key.replace(":", "")) != null) {//json has at least 2 samenamed keys

                Object vallue = content.get(key.replace(":", ""));

                if (vallue instanceof List) {//adds this value to the list and resets content's key

                    List<String> values = (ArrayList<String>) vallue;
                    values.add(this.value);
                    content.set(key.replace(":", ""), values);

                } else { //creates a list and adds this value and previous value to the list and resets content's key

                    List<String> values = new ArrayList<>();
                    values.add((String) vallue);
                    values.add(this.value);
                    content.set(key.replace(":", ""), values);

                }

            } else {

                content.set(key.replace(":", ""), value);

            }
        }

        /**
         * Sets the current key and if it is not in processedKeys list, adds to
         * it.
         *
         * @param key the key
         */
        public void setKey(String key) {
            this.key = key;
            if (!processedKeys.contains(key)) {
                processedKeys.add(key);
            }
        }

        /**
         * Puts all the stuff ready to another fullY searching cycle.
         */
        public void reset() {

            processedKeys.clear();
            keyState.getKeys().forEach(keyy -> {
                processedKeyValues.put(keyy, null);
            });
            value = "";
            key = "";
            starting = "";
            identicals = 1;
        }

        public Map<Object, Object> getProcessedKeyValues() {
            return processedKeyValues;
        }

        /**
         * Tells if there is any missing unprocessed key=value pair. <br>
         * When EVERYTHING is processed, ALL keys are set to NULL. This
         * information may trigger recursive call.<br>
         *
         * @return true if any key is associated with a NOT NULL value. <br>
         * False, otherwise
         */
        public synchronized boolean hasAnyMissingKeyValuePair() {

            int count = 0;
            Iterator<Object> it = processedKeyValues.keySet().iterator();
            while (it.hasNext()) {
                Object keyy = it.next();
                if (processedKeyValues.get(keyy) != null) {
                    count++;
                }
            }

            return 0 != count;
        }

    }

    /**
     * Class that implements State Design Pattern
     */
    static class KeysState extends JsonState {

        private final ValueState valueState;
        private final List<String> keys;
        private String current = "";

        public KeysState(List<String> keys) {
            this.keys = new ArrayList<>();
            //NOT ALLOWED duplication
            keys.stream().map(key -> "\"" + key + "\"" + ":").filter(kew -> (!this.keys.contains(kew))).forEachOrdered(kew -> {
                this.keys.add(kew);
            });
            valueState = new ValueState(this);
        }

        @Override
        public void process(char c, int currentIndex, String json, Content content) {
            if (anyKeyStartsWith(current + "" + c)) {

                current += "" + c;

            } else {

                if (hasKeyEquals(current)) {
                    jsonState = valueState;
                    valueState.setKey(current);
                    valueState.process(c, currentIndex, json, content);
                }

                current = "";

            }
        }

        /**
         * Gets a key that is equals to current, if any.
         *
         * @param current
         * @return
         */
        private String getKeyEquals(String current) {
            if (hasKeyEquals(current)) {
                return keys.get(keys.indexOf(current));
            }
            return null;
        }

        /**
         * Checks if any key is equals to current.
         *
         * @param current
         * @return
         */
        private boolean hasKeyEquals(String current) {
            return keys.indexOf(current) != -1;
        }

        private boolean anyKeyStartsWith(String s) {
            return keys.stream().anyMatch(key -> (key.startsWith(s)));
        }

        public void reset() {
            current = "";
        }

        public List<String> getKeys() {
            return keys;
        }

        public ValueState getValueState() {
            return valueState;
        }

        static public boolean anyKeyStartsWith(String s, List<String> keys) {
            return keys.stream().anyMatch(key -> (key.startsWith(s)));
        }

        /**
         * Checks if any key is equals to current.
         *
         * @param current
         * @return
         */
        static public boolean hasKeyEquals(String current, List<String> keys) {
            return keys.indexOf(current) != -1;
        }

    }

    /**
     * Double quotes a string
     *
     * @param key
     * @return string double quoted
     */
    public static String doubleQuote(String key) {
        return "\"" + key + "\"";
    }

    /**
     * Double quotes the stringS
     *
     * @param key
     * @return stringS double quoted
     */
    private static Object[] doubleQuote(String[] keys) {
        Object[] doubleQuoted = new Object[keys.length];

        for (int i = 0; i < keys.length; i++) {
            doubleQuoted[i] = doubleQuote(keys[i]);
        }

        return doubleQuoted;
    }

    /**
     * Strips off double quotes.
     *
     * @param s
     * @return
     */
    public static String stripDoubleQuotes(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        if (s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }

        return s;
    }

    /**
     *
     * @param value
     * @return
     */
    public static String shrinkEagerlySingle(String value) {

        while (value.length() > 0 && !(value.endsWith("0")
                || value.endsWith("1")
                || value.endsWith("2")
                || value.endsWith("3")
                || value.endsWith("4")
                || value.endsWith("5")
                || value.endsWith("6")
                || value.endsWith("7")
                || value.endsWith("8")
                || value.endsWith("9"))) {

            value = value.substring(0, value.length() - 1);

        }

        //           shrink(105s7f)
        //            105s == shrink(105s) ? "105s7" || shrink(105s) 
        //            10   == shrink(10)   ?  105    || shrink(10)
        //            1    == shrink(1)    ?  10     || shrink(1)
        //            ""   == shrink("")   ?  1      || shrink("")
        //           shrink(a105s7f)
        //            a105s == shrink(a105s) ? "a105s7" || shrink(a105s) 
        //            a10   == shrink(a10)   ?  a105    || shrink(a10)
        //            a1    == shrink(a1)    ?  a10     || shrink(a1)
        //            a     == shrink(a)     ?  a1      || shrink(a)
        //            ""    == shrink("")   ?   a       || shrink("")
        if (value.equals("")) {
            return "";
        }

        return value.substring(0, value.length() - 1).equals(shrinkEagerly(value.substring(0, value.length() - 1))) ? value : shrinkEagerly(value.substring(0, value.length() - 1));
    }

    /**
     *
     * @param value
     * @return
     */
    public static String shrinkFromEnding(String value) {

        while (value.length() > 0 && !(value.endsWith("0")
                || value.endsWith("1")
                || value.endsWith("2")
                || value.endsWith("3")
                || value.endsWith("4")
                || value.endsWith("5")
                || value.endsWith("6")
                || value.endsWith("7")
                || value.endsWith("8")
                || value.endsWith("9"))) {

            value = value.substring(0, value.length() - 1);

        }

        //           shrink(105s7f)
        //            105s == shrink(105s) ? "105s7" || shrink(105s) 
        //            10   == shrink(10)   ?  105    || shrink(10)
        //            1    == shrink(1)    ?  10     || shrink(1)
        //            ""   == shrink("")   ?  1      || shrink("")
        //           shrink(a105s7f)
        //            a105s == shrink(a105s) ? "a105s7" || shrink(a105s) 
        //            a10   == shrink(a10)   ?  a105    || shrink(a10)
        //            a1    == shrink(a1)    ?  a10     || shrink(a1)
        //            a     == shrink(a)     ?  a1      || shrink(a)
        //            ""    == shrink("")   ?   a       || shrink("")
        if (value.equals("")) {
            return "";
        }

        return value.substring(0, value.length() - 1).equals(shrinkEagerly(value.substring(0, value.length() - 1))) ? value : shrinkEagerly(value.substring(0, value.length() - 1));
    }

    /**
     *
     * @param value
     * @return
     */
    public static String shrinkFromBeginnig(String value) {

        //strips from head
        while (value.length() > 0 && !(value.startsWith("0")
                || value.startsWith("1")
                || value.startsWith("2")
                || value.startsWith("3")
                || value.startsWith("4")
                || value.startsWith("5")
                || value.startsWith("6")
                || value.startsWith("7")
                || value.startsWith("8")
                || value.startsWith("9"))) {

            value = value.substring(1);

        }

        //           shrink(105s7f)
        //            105s == shrink(105s) ? "105s7" || shrink(105s) 
        //            10   == shrink(10)   ?  105    || shrink(10)
        //            1    == shrink(1)    ?  10     || shrink(1)
        //            ""   == shrink("")   ?  1      || shrink("")
        //           shrink(a105s7f)
        //            a105s == shrink(a105s) ? "a105s7" || shrink(a105s) 
        //            a10   == shrink(a10)   ?  a105    || shrink(a10)
        //            a1    == shrink(a1)    ?  a10     || shrink(a1)
        //            a     == shrink(a)     ?  a1      || shrink(a)
        //            ""    == shrink("")   ?   a       || shrink("")
        if (value.equals("")) {
            return "";
        }

        return value.substring(0, value.length() - 1).equals(shrinkEagerly(value.substring(0, value.length() - 1))) ? value : shrinkEagerly(value.substring(0, value.length() - 1));
    }

    /**
     *
     * @param value
     * @return
     */
    public static String shrinkEagerly(String value) {

        while (value.length() > 0 && !(value.endsWith("0")
                || value.endsWith("1")
                || value.endsWith("2")
                || value.endsWith("3")
                || value.endsWith("4")
                || value.endsWith("5")
                || value.endsWith("6")
                || value.endsWith("7")
                || value.endsWith("8")
                || value.endsWith("9"))) {

            value = value.substring(0, value.length() - 1);

        }

        //           shrink(105s7f)
        //            105s == shrink(105s) ? "105s7" || shrink(105s) 
        //            10   == shrink(10)   ?  105    || shrink(10)
        //            1    == shrink(1)    ?  10     || shrink(1)
        //            ""   == shrink("")   ?  1      || shrink("")
        //           shrink(a105s7f)
        //            a105s == shrink(a105s) ? "a105s7" || shrink(a105s) 
        //            a10   == shrink(a10)   ?  a105    || shrink(a10)
        //            a1    == shrink(a1)    ?  a10     || shrink(a1)
        //            a     == shrink(a)     ?  a1      || shrink(a)
        //            ""    == shrink("")   ?   a       || shrink("")
        if (value.equals("")) {
            return "";
        }

        return value.substring(0, value.length() - 1).equals(shrinkEagerly(value.substring(0, value.length() - 1))) ? value : shrinkEagerly(value.substring(0, value.length() - 1));
    }

    /**
     *
     * @param value
     * @return
     */
    public static String shrinkEagerlyFromBothSides(String value) {

        //strips from tail
        while (value.length() > 0 && !(value.endsWith("0")
                || value.endsWith("1")
                || value.endsWith("2")
                || value.endsWith("3")
                || value.endsWith("4")
                || value.endsWith("5")
                || value.endsWith("6")
                || value.endsWith("7")
                || value.endsWith("8")
                || value.endsWith("9"))) {

            value = value.substring(0, value.length() - 1);

        }
        //strips from head
        while (value.length() > 0 && !(value.startsWith("0")
                || value.startsWith("1")
                || value.startsWith("2")
                || value.startsWith("3")
                || value.startsWith("4")
                || value.startsWith("5")
                || value.startsWith("6")
                || value.startsWith("7")
                || value.startsWith("8")
                || value.startsWith("9"))) {

            value = value.substring(1);

        }

        //           shrink(105s7f)
        //            105s == shrink(105s) ? "105s7" || shrink(105s) 
        //            10   == shrink(10)   ?  105    || shrink(10)
        //            1    == shrink(1)    ?  10     || shrink(1)
        //            ""   == shrink("")   ?  1      || shrink("")
        //           shrink(a105s7f)
        //            a105s == shrink(a105s) ? "a105s7" || shrink(a105s) 
        //            a10   == shrink(a10)   ?  a105    || shrink(a10)
        //            a1    == shrink(a1)    ?  a10     || shrink(a1)
        //            a     == shrink(a)     ?  a1      || shrink(a)
        //            ""    == shrink("")   ?   a       || shrink("")
        if (value.equals("")) {
            return "";
        }

        return value.substring(1, value.length() - 1).equals(shrinkEagerly(value.substring(1, value.length() - 1))) ? value : shrinkEagerly(value.substring(1, value.length() - 1));
    }

    public static void main(String[] args) throws FileNotFoundException {

        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "success", "author");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "contents");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "author");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "quotes");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "quotes", "author", "tags");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "contents", "tags");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "length", "tags");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "SortAs", "ID");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "title", "ID");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "title", "ID", "GlossTerm");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "contents", "author");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "contentsdfs", "tags");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("tests-input-bad-format-json")), SourceType.JSON, "id", "quote", "badlengthstring");
//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("tests-input-bad-key-value-pair-format-json")), SourceType.JSON, "id");
        System.out.println(contents);

//        System.out.println(ContentExtractor.shrinkEagerly("1002b0a109sd7"));
//        System.out.println(ContentExtractor.shrinkEagerly("b 105s7f"));
    }

}
