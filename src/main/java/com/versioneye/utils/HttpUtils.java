package com.versioneye.utils;

import com.versioneye.dto.ErrorJsonResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.HttpClientBuilder;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.apache.http.entity.ContentType.APPLICATION_JSON;

/**
 * Methods to deal with the HTTP protocol.
 */
public class HttpUtils {

    private static Integer ONE_SECOND = 1000;
    private static Integer ONE_MINUTE = ONE_SECOND * 60;
    private static Integer TEN_MINUTE = ONE_MINUTE * 10;

    public static String get(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(TEN_MINUTE);
        con.setReadTimeout(TEN_MINUTE);
        con.setRequestProperty( "User-Agent", "VersionEye Maven Plugin" );

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    public static Reader post(String url, byte[] data, String dataName, String visibility, String name, String orga_name, String team) throws Exception {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        ByteArrayBody byteArrayBody = new ByteArrayBody(data, APPLICATION_JSON, "pom.json");

        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addPart(dataName, byteArrayBody);

        if (visibility != null && !visibility.isEmpty())
            builder.addPart("visibility", new StringBody(visibility, APPLICATION_JSON));

        if (name != null && !name.isEmpty())
            builder.addPart("name", new StringBody(name, APPLICATION_JSON));

        if (orga_name != null && !orga_name.isEmpty())
            builder.addPart("orga_name", new StringBody(orga_name, APPLICATION_JSON));

        if (team != null && !team.isEmpty())
            builder.addPart("team_name", new StringBody(team, APPLICATION_JSON));

        httpPost.setEntity(builder.build());
        HttpResponse response = client.execute(httpPost);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200 && statusCode != 201){
            String err = getErrorMessage(response);
            String errMsg = "Status Code: " + statusCode + " -> " + err + " for URL: " + url;
            throw new Exception(errMsg);
        }

        return new InputStreamReader(response.getEntity().getContent());
    }

    public static String delete(String url) throws Exception {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setConnectTimeout(TEN_MINUTE);
        con.setReadTimeout(TEN_MINUTE);
        con.setRequestProperty("User-Agent", "VersionEye Maven Plugin");
        con.setRequestMethod("DELETE");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'DELETE' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }

    private static String getErrorMessage(HttpResponse response) throws Exception {
        String errorMsg = getErrorFromJson(response);
        if (errorMsg != null){
            return errorMsg;
        }
        return getPureBodyString(response);
    }

    private static String getErrorFromJson(HttpResponse response){
        try {
            ObjectMapper mapper = new ObjectMapper();
            ErrorJsonResponse error = mapper.readValue(response.getEntity().getContent(), ErrorJsonResponse.class);
            return error.getError();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static String getPureBodyString(HttpResponse response){
        try {
            InputStream content = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader( content ) );
            String inputLine;
            StringBuilder body = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                body.append(inputLine);
            }
            in.close();
            return body.toString();
        } catch (Exception exception) {
            exception.printStackTrace();
            return "";
        }

    }

}
