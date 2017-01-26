package com.versioneye;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

/**
 * Ping the VersionEye API. Expects a pong in response.
 */
@SuppressWarnings("unused")
@Mojo( name = "ping", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class PingMojo extends SuperMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException  {
        try{
            setProxy();
            initTls();
            InputStream inputStream = getInputStream(fetchBaseUrl() + apiPath + "/services/ping");
            BufferedReader input =  new BufferedReader( new InputStreamReader( inputStream ) );
            String line;
            getLog().info("");
            while((line = input.readLine())!=null){
                getLog().info(line);
            }
            getLog().info("");
            input.close();
        } catch (Exception ex){
            throw new MojoExecutionException( "Oh no! The API or your internet connection seems to be down. " +
                "Get in touch with the VersionEye guys and give them feedback. " +
                "You find them on Twitter at https//twitter.com/VersionEye. ", ex );
        }
    }

    private InputStream getInputStream( String urlPath ) throws IOException {
        URL url = new URL( urlPath );
        if (urlPath.startsWith("https")){
            System.out.println("https");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            return con.getInputStream();
        } else {
            System.out.println("http");
            URLConnection urlConnection = url.openConnection();
            return urlConnection.getInputStream();
        }
    }
}
