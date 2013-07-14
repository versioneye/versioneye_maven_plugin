package versioneye;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created with IntelliJ IDEA.
 * User: robertreiz
 * Date: 7/13/13
 * Time: 5:25 PM
 */
public class HttpUtils {

    public static Reader post(String url,byte[] data, String dataName) throws MojoExecutionException {
        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            ByteArrayBody byteArrayBody = new ByteArrayBody(data, "application/json", "pom.json");
            MultipartEntity multipartEntity = new MultipartEntity();
            multipartEntity.addPart(dataName, byteArrayBody);
            httpPost.setEntity( multipartEntity );
            HttpResponse response = client.execute(httpPost);
            System.out.print(response.toString());
            return new InputStreamReader(response.getEntity().getContent());
        } catch (Exception e) {
            throw new MojoExecutionException("Fatal error, unsupported encoding while constructing HTTP post MIME body", e);
        }
    }

}
