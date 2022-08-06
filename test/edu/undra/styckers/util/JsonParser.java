package edu.undra.styckers.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extrai filmes do json.<br>
 * Os filmes são coletados e armazenado numa lista de conjuntos key=value.<br>
 *
 * @author alura
 */
public class JsonParser {

    private static final Pattern REGEX_ITEMS = Pattern.compile(".*\\[(.+)\\].*");
    private static final Pattern REGEX_ATRIBUTOS_JSON = Pattern.compile("\"(.+?)\":\"(.*?)\"");

    public List<Map<String, String>> parse(String json) {

        Matcher matcher = REGEX_ITEMS.matcher(json);
        if (!matcher.find()) {

            throw new IllegalArgumentException("Não encontrou items.");
        }

        String[] items = matcher.group(1).split("\\},\\{");

        List<Map<String, String>> dados = new ArrayList<>();

        for (String item : items) {

            Map<String, String> atributosItem = new HashMap<>();

            Matcher matcherAtributosJson = REGEX_ATRIBUTOS_JSON.matcher(item);
            while (matcherAtributosJson.find()) {
                String atributo = matcherAtributosJson.group(1);
                String valor = matcherAtributosJson.group(2);
                atributosItem.put(atributo, valor);
            }

            dados.add(atributosItem);
        }

        return dados;

    }

    public static void main(String[] args) {

        String json = "[{\"id\":\"tt5491994\",\"tags\":[{\"rank\":\"string\"}], \"rank\":\"1\", \"title\":\"Planet Earth II\", \"fullTitle\":\"Planet Earth II (2016)\", \"year\":\"2016\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/TopTVs_1.jpg\", \"crew\":\"David Attenborough, Chadden Hunter\", \"imDbRating\":\"9.4\", \"imDbRatingCount\":\"137192\"}]";
//        String json = "[{\n"
//                + "\"success\": \"string\",\n"
//                + "        \"contents\": {\n"
//                + "        \"quotes\": [\n"
//                + "        {\n"
//                + "        \"author\": \"string\",\n"
//                + "                \"quote\": \"string\",\n"
//                + "                \"tags\": [\n"
//                + "                        \"string\"\n"
//                + "                ],\n"
//                + "                \"id\": \"string\",\n"
//                + "                \"image\": \"string\",\n"
//                + "                \"length\": 0\n"
//                + "\n"
//                + "        }\n"
//                + "        ]\n"
//                + "        }\n"
//                + "}]";
        
        
        String jsonn="";
        Scanner s = new Scanner(json);
        while(s.hasNextLine()){
            jsonn+=s.nextLine();
        }
        
//        System.out.println(jsonn);
        
        System.out.println(new JsonParser().parse(jsonn));
//        String json = "[{\"id\":\"tt5491994\", \"rank\":\"1\", \"title\":\"Planet Earth II\", \"fullTitle\":\"Planet Earth II (2016)\", \"year\":\"2016\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/TopTVs_1.jpg\", \"crew\":\"David Attenborough, Chadden Hunter\", \"imDbRating\":\"9.4\", \"imDbRatingCount\":\"137192\"},{\"id\":\"tt0903747\", \"rank\":\"2\", \"title\":\"Breaking Bad\", \"fullTitle\":\"Breaking Bad (2008)\", \"year\":\"2008\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/TopTVs_2.jpg\", \"crew\":\"Bryan Cranston, Aaron Paul\", \"imDbRating\":\"9.4\", \"imDbRatingCount\":\"1769920\"},{\"id\":\"tt0795176\", \"rank\":\"3\", \"title\":\"Planet Earth\", \"fullTitle\":\"Planet Earth (2006)\", \"year\":\"2006\", \"image\":\"https://imersao-java-apis.s3.amazonaws.com/TopTVs_3.jpg\", \"crew\":\"Sigourney Weaver, David Attenborough\", \"imDbRating\":\"9.4\", \"imDbRatingCount\":\"203274\"}]";
    }

}
