package edu.undra.styckers.util;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * Esta classe faz :
 * <br>HTTP GET
 * <br>HTTP POST
 * <br>HTTP PUT
 * <br>HTTP DELETE
 * <br>HTTP PATCH<br>
 * 
 *
 * @date 18 de jul. de 2022 13.56.45
 * @author alexandre
 */
public class Http {
    
    /**
     * Does a GET request and<br>
     * reads http response's body as a string.<br>
     * Default implementation.
     * @param url the resource to get from the server.
     * @return HttpResponse the response from the server
     * @throws java.io.IOException I am a risky method. Please, handle fail if some bad thing happens. 
     * @throws java.lang.InterruptedException I am a risky method. Please, handle fail if some bad thing happens. 
     */
    public static HttpResponse<String> GET(String url) throws IOException, InterruptedException{
        return GET(url, BodyHandlers.ofString());
        
    }
    /**
     * Does a GET request and<br>
     * returns the http response<br>
     * @param url the resource to get from the server.
     * @param bodyHandler how the body's response should be interpreted/handled
     * @return HttpResponse the response from the server
     * @throws java.io.IOException I am a risky method. Please, handle fail if some bad thing happens. 
     * @throws java.lang.InterruptedException I am a risky method. Please, handle fail if some bad thing happens. 
     */
    public static  HttpResponse GET(String url, BodyHandler bodyHandler) throws IOException, InterruptedException{
        URI uri = URI.create(url);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder(uri).GET().build();
        return client.send(request,bodyHandler);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
//        HttpResponse<String> response = Http.GET("https://mocki.io/v1/9a7c1ca9-29b4-4eb3-8306-1adb9d159060");
//        System.out.println(response.body());
        HttpResponse<String> response = Http.GET("http://localhost:8080/linguagens");
        System.out.println(response.body());
    }
    
}
