package versioneye;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Ping the VersionEye API. Expects a pong in response.
 */
@Mojo( name = "ping", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class PingMojo extends SuperMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            URL url = new URL(baseUrl + apiPath + "/services/ping");
            URLConnection conn = url.openConnection();
            conn.setConnectTimeout(3000);
            BufferedReader input =  new BufferedReader( new InputStreamReader(conn.getInputStream()) );
            String line = "";
            getLog().info("");
            while((line = input.readLine())!=null){
                getLog().info(line);
            }
            getLog().info("");
            input.close();
        } catch (Exception ex){
            getLog().error( ex );
            getLog().error("Oh no! The API or your internet connection seems to be down. Get in touch with the VersionEye guys and give them feedback.");
            throw new MojoExecutionException( ex.toString() );
        }
    }

}
