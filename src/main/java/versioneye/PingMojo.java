package versioneye;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created with IntelliJ IDEA.
 * User: robertreiz
 * Date: 7/13/13
 * Time: 12:10 PM
 */

@Mojo( name = "ping", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class PingMojo extends AbstractMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            URL url = new URL("https://www.versioneye.com/api/v2/services/ping");
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
