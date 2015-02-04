package com.versioneye.utils;

import com.versioneye.dto.ErrorJsonResponse;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Methods to deal with the HTTP protocol.
 */
public class HttpUtils {

    public static Reader post(String url, byte[] data, String dataName) throws Exception {
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        ByteArrayBody byteArrayBody = new ByteArrayBody(data, "application/json", "pom.json");
        MultipartEntity multipartEntity = new MultipartEntity();
        multipartEntity.addPart(dataName, byteArrayBody);
        httpPost.setEntity( multipartEntity );
        HttpResponse response = client.execute(httpPost);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 200 && statusCode != 201){
            String err = getErrorMessage(response);
            String errMsg = "Status Code: " + statusCode + " -> " + err;
            throw new Exception(errMsg);
        }

        return new InputStreamReader(response.getEntity().getContent());
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
            StringBuffer body = new StringBuffer();
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
