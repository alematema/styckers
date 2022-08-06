package edu.undra.styckers.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class models and encapsulates a content as a set of key=value pairs.
 * <br>
 * The the number of a content's attributes may vary from API to API.<br>
 * <br>
 * Example1 : if the type is a movie, we may be intersted in only 3 movie's
 * attributes : title, cover image and ranking. <br>
 * Example2 : if the type is a movie, we may be intersted in only 2 movie's
 * attributes : title, cover image <br>
 * Example3 : if the type is an image, we may be intersted in only 2 images's
 * attributes : title, dimension. <br>
 *
 *
 *
 * @date 19 de jul. de 2022 07.56.38
 * @author alexandre
 */
public class Content {

    //Stores the key-value pairs for this content
    private final Map<Object, Object> attributes;

    public Content() {
        this.attributes = new HashMap<>();
    }

    public Content(List<Object> keys) {
        attributes = new HashMap<>();
        keys.forEach(key -> {
            attributes.put(key, null);
        });
    }

    public Object get(Object key) {
        return attributes.get(key);
    }

    public void set(Object key, Object value) {
        attributes.put(key, value);
    }

    public List<Object> keys() {
        List<Object> keyss = new ArrayList<>();
        Iterator<Object> it = attributes.keySet().iterator();
        while (it.hasNext()) {
            keyss.add(it.next());
        }
        return keyss;
    }

    /**
     * Tells if this object has all the given keys.
     *
     * @param keys the keys to check for.
     * @return true , if this object has all the keys. <Br>
     * false, otherwise.
     */
    private Set<Object> probe = new HashSet<>();

    public boolean hasSameKeys(Object... keys) {
        probe.clear();
        probe.addAll(Arrays.asList(keys));
        return probe.equals(attributes.keySet());
    }

    private String tryDoubleQuotes(String value) {
        if (value.isEmpty()) {
            return doubleQuote("");
        }
        return value;
    }

    private String doubleQuote(String key) {
        return "\"" + key + "\"";
    }

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();
        sb.append("{");
        Iterator<Object> it = attributes.keySet().iterator();
        while (it.hasNext()) {

            Object next = it.next();

            if (attributes.get(next) instanceof List) {

                List<String> values = (List<String>) attributes.get(next);

                values.forEach(value -> {
                    sb.append(next).append("=>").append(tryDoubleQuotes(value)).append(",");
                });

            } else {
                sb.append(next).append("=>").append(tryDoubleQuotes((String) attributes.get(next))).append(",");
            }

        }
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append("}");

        return sb.toString();
    }

    public static void main(String[] args) {
//        Content c = new Content("\"1\"", "2");
        Content c = new Content();
        List<String> l = new ArrayList<>();
        l.add("1.1");
        l.add("1.2");
        c.set("1", l);
        c.set("2", "2");

        System.out.println(c);

        System.out.println(c.hasSameKeys("1", "2"));

//        System.out.println(System.getProperty("user.dir"));
    }
//    public static void main(String[] args) {
//        Content c = new Content("\"ola\"", "java");
//        c.set("ola", "OI EM PTBR");
//        c.set("java", "\"É SHOW\"");
//
//        System.out.println(Arrays.asList(c, c));
//        System.out.println("ABC".trim());
//        System.out.println("  abc   ".trim());
//        System.out.println("  {   ".trim().equals("s{"));
//    }

}
