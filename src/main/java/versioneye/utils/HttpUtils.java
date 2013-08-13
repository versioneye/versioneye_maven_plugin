package versioneye.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;

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
        return new InputStreamReader(response.getEntity().getContent());
    }

}
