package com.versioneye.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.SystemDefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.plexus.util.StringUtils;

import com.versioneye.dto.ErrorJsonResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Methods to deal with the HTTP protocol.
 */
@SuppressWarnings("WeakerAccess")
public class HttpUtils {

    public static final Integer ONE_SECOND = 1000;
    public static final Integer ONE_MINUTE = ONE_SECOND * 60;
    public static final Integer TEN_MINUTE = ONE_MINUTE * 10;

    public static String get(String url) throws IOException {
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
        HttpClient client = new SystemDefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        ByteArrayBody byteArrayBody = new ByteArrayBody(data, "application/json", "pom.json");

        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart(dataName, byteArrayBody);

        if (StringUtils.isNotBlank(visibility))
            multipartEntity.addPart("visibility", new StringBody(visibility));

        if (StringUtils.isNotBlank(name))
            multipartEntity.addPart("name", new StringBody(name));

        if (StringUtils.isNotBlank(orga_name))
            multipartEntity.addPart("orga_name", new StringBody(orga_name));

        if (StringUtils.isNotBlank(team))
            multipartEntity.addPart("team_name", new StringBody(team));

        httpPost.setEntity(multipartEntity);
        HttpResponse response = client.execute(httpPost);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200 && statusCode != 201){
            String err = getErrorMessage(response);
            String errMsg = "Status Code: " + statusCode + " -> " + err + " for URL: " + url;
            throw new Exception(errMsg);
        }

        return new InputStreamReader(response.getEntity().getContent());
    }

    public static String delete(String url) throws IOException {
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

    private static String getErrorMessage(HttpResponse response) {
        String errorMsg = getErrorFromJson(response);
        if (errorMsg != null){
            return errorMsg;
        }
        return getPureBodyString(response);
    }

    private static String getErrorFromJson(HttpResponse response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ErrorJsonResponse error = mapper.readValue(response.getEntity().getContent(), ErrorJsonResponse.class);
            return error.getError();
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }
    }

    private static String getPureBodyString(HttpResponse response) {
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
