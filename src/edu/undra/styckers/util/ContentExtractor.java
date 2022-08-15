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

            if (start != -1 && end != -1) {

                processJSON(sb.substring(start, end + 1), keys)
                        .stream()
                        .filter(c -> (c.hasSameKeys(doubleQuote(keys))))
                        .forEach(c -> {
                            contents.add(c);
                        });

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
     * @return a list of Content
     */
    private static List<Content> processJSON(String json, String[] keys) {

        if (json == null) {
            throw new NullPointerException("json must be NOT null");
        }
        if (keys == null) {
            throw new NullPointerException("keys must be NOT null");
        }

        if (!json.trim().substring(0, 1).equals("{")) {
            throw new IllegalArgumentException("source is not a JSON: does not start with {");
        }

        KeysState keysState = new KeysState(Arrays.asList(keys));
        jsonState = keysState;

        StringBuilder oneLine = new StringBuilder();

        //onelines the source
        Scanner s = new Scanner(json);
        while (s.hasNextLine()) {
            String line = s.nextLine();
            oneLine.append(line);
        }

        List<Content> contents = new ArrayList<>();

        //Processes each json charactere
        //The State Design Pattern handles each charactere the appropriate processing 
        for (int i = 0; i < json.length(); i++) {
            jsonState.process(json.charAt(i), contents);
        }

        // Some key may be left unprocessed after a reading cycle because of json's deeper or shalow nesting structure.
        //Then, recursive calls are made to solve this issue
        if (keysState.getValueState().hasAnyMissingKeyValuePair()) {

            processMissingKeyValuePairs(keysState, keys, json, contents);

        } else {

            if (!contents.isEmpty()) {
                Content content = contents.get(0);
                content.keys().forEach(key -> {
                    processedKeyValues.put(key, content.get(key));
                });

            }
        }

        return contents;

    }

    /**
     * Makes recursive calls to processJSON, until there is ZERO unprocessed key
     * left behind.
     * <br>
     * Some key may be left unprocessed after a reading cycle because of json's
     * deeper or shalow nesting structure. <br>
     * These recursive calls solves the problem.
     *
     * @param keysState
     * @param keys
     * @param json
     * @param contents
     */
    private static void processMissingKeyValuePairs(KeysState keysState, String[] keys, String json, List<Content> contents) {
        Iterator<Object> it = keysState.getValueState().getProcessedKeyValues().keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            if (keysState.getValueState().getProcessedKeyValues().get(key) != null) {
                processedKeyValues.put(key, keysState.getValueState().getProcessedKeyValues().get(key));
            }
        }

        List<String> remainingKeys = new ArrayList<>();
        String newSouce = "";
        for (String key : keys) {
            if (keysState.getValueState().getProcessedKeyValues().get("\"" + key + "\":") == null) {
                remainingKeys.add(key);
            } else {
//                    newSouce += keysState.getValueState().getProcessedKeyValues().get("\"" + key + "\":");
            }
        }
        String[] remainingKeyss = new String[remainingKeys.size()];
        for (int i = 0; i < remainingKeys.size(); i++) {
            remainingKeyss[i] = remainingKeys.get(i);
        }
        if (keepRecursion(remainingKeys)) {
            missingKeyValuePairsCount = remainingKeys.size();
            processJSON(json, remainingKeyss);
        }

        Content content = new Content();
        it = processedKeyValues.keySet().iterator();
        while (it.hasNext()) {
            Object key = it.next();
            content.set(((String) key).replace(":", ""), processedKeyValues.get(key));
        }
        contents.add(content);
        processedKeyValues.clear();
        missingKeyValuePairsCount = 0;
    }

    /**
     * Tells if recursion should goes on.
     * <br>
     * It keeps going until there is ZERO unprocessed key left behind.
     *
     * @param remainingKeys
     * @return
     */
    private static boolean keepRecursion(List<String> remainingKeys) {
        return missingKeyValuePairsCount != remainingKeys.size();
    }

    /**
     * Placeholder class for ones that implement State Design Pattern
     */
    abstract class JsonState {

        abstract public void process(char c, List<Content> contents);
    }

    /**
     * Class that implements State Design Pattern
     */
    static class ValueState extends JsonState {

        private int identicals = 1;
        private String key;
        private final KeysState keyState;
        private String value = "";
        private String starting;
        private final List<String> processedKeys = new ArrayList<>();
        private final Map<Object, Object> processedKeyValues = new HashMap<>();

        public ValueState(KeysState keyState) {
            this.keyState = keyState;
            keyState.getKeys().forEach(keyy -> {
                processedKeyValues.put(keyy, null);
            });
        }

        @Override
        public void process(char c, List<Content> contents) {

            if ((c + value).equals(" ")) {//consumes spaces
                //return;
            } else if (value.equals("")) {//memoizes first value's char
                value += "" + c;
                starting = "" + c;
            } else {

                value += "" + c;

                switch (starting) {
                    case "\""://value is a double quoted string

                        if ((c + "").equals("\"")) {
                            endsAValueReading(contents);
                        }
                        break;
                    case "{"://value is a json object
                        //eager behaviour:dont return after first match
                        if ((c + "").equals("{")) {
                            identicals++;
                        }
                        if ((c + "").equals("}")) {
                            identicals--;
                        }
                        if ((c + "").equals("}") && identicals <= 0) { //ends a value reading
                            endsAValueReading(contents);
                        }
                        break;
                    case "["://value is an array
                        //eager behaviour:dont return after first match
                        if ((c + "").equals("[")) {
                            identicals++;
                        }
                        if ((c + "").equals("]")) {
                            identicals--;
                        }
                        if ((c + "").equals("]") && identicals <= 0) { //ends a value reading
                            endsAValueReading(contents);
                        }
                        break;
                    default://value is anything, but not cases above
                        //ends a value reading
                        if ((c + "").equals("\"") || (c + "").equals(" ") || (c + "").equals("}") || (c + "").equals("]") || (c + "").equals(",")) {
                            if ((value).startsWith("}")) {
                                value = "";
                                endsAValueReading(contents);
                            } else if ((value).startsWith("]")) {
                                value = "";
                                endsAValueReading(contents);
                            } else if ((value).equals(" ")) {
                                value = value.substring(0, value.length() - 1);
                                endsAValueReading(contents);
                            } else if ((value).startsWith(",")) {
                                value = "";
                                endsAValueReading(contents);
                            } else if ((value).startsWith("\"")) {
                                value = "";
                                endsAValueReading(contents);
                            } else {

                                value = shrinkEagerly(value.trim().replaceAll("\\s", ""));

                                endsAValueReading(contents);

                            }
                        }
                        break;
                }

            }
        }

        //ends a value reading
        /**
         * Ends a value reading.<br>
         * A value is the thing which is associated with the current key being
         * processed.<br>
         * If it is a full ending, e.g, all keys were fully processed in this
         * cycle, then an instance of Content is generated and added to contents
         * list<br>
         * Then, all the stuffs are reset and the states will be ready for
         * another fully searching cycle.<br>
         * Otherwise,if there are remaining unprocessed keys, then the current
         * key=value pair is put in a map<br>
         * and this ValueState instance is partially reset, as it is the
         * KeyState instace, letting it ready for processing remaining keys.
         *
         * @param contents the list of content to be filled.
         */
        public void endsAValueReading(List<Content> contents) {

            if (processedKeys.size() == keyState.getKeys().size()) {//must create an Content instance

                setKeyValue();

                Content content = new Content();
                Iterator<Object> it = processedKeyValues.keySet().iterator();
                while (it.hasNext()) {
                    Object keyy = it.next();
                    content.set(((String) keyy).replace(":", ""), processedKeyValues.get(keyy));
                }

                contents.add(content);

                reset();

            } else {
                setKeyValue();
                value = "";
                key = "";
                starting = "";
                identicals = 1;

            }

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
        private void setKeyValue() {
            if (processedKeyValues.get(key) != null) {
                Object vallue = processedKeyValues.get(key);
                if (vallue instanceof List) {

                    List<String> values = (ArrayList<String>) vallue;
                    values.add(this.value);
                    processedKeyValues.put(key, values);

                } else {
                    List<String> values = new ArrayList<>();
                    values.add((String) vallue);
                    values.add(this.value);
                    processedKeyValues.put(key, values);
                }
            } else {
                processedKeyValues.put(key, value);
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
        public void process(char c, List<Content> contents) {
            if (anyKeyStartsWith(current + "" + c)) {

                current += "" + c;

            } else {

                if (hasKeyEquals(current)) {
                    jsonState = valueState;
                    valueState.setKey(current);
                    valueState.process(c, contents);
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

//        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("input")), SourceType.JSON, "success", "author");
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
        List<Content> contents = ContentExtractor.extract(new FileInputStream(new File("tests-input-bad-key-value-pair-format-json")), SourceType.JSON, "id");
        System.out.println(contents);

//        System.out.println(ContentExtractor.shrinkEagerly("1002b0a109sd7"));
//        System.out.println(ContentExtractor.shrinkEagerly("b 105s7f"));
    }

}
