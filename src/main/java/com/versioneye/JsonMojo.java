package com.versioneye;

import com.versioneye.utils.JsonUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.Map;

/**
 * Writes all direct dependencies into a JSON file.
 */
@SuppressWarnings("unused")
@Mojo( name = "json", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class JsonMojo extends ProjectMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            Map<String, Object> jsonMap = getDirectDependenciesJsonMap(nameStrategy);
            JsonUtils jsonUtils = new JsonUtils();
            String filePath = outputDirectory + "/pom.json";
            jsonUtils.dependenciesToJsonFile(project.getName(), jsonMap, filePath);
            prettyPrintEnd(filePath);
        } catch( Exception exception ){
            throw new MojoExecutionException( "Oh no! Something went wrong. " +
                    "Get in touch with the VersionEye guys and give them feedback. " +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception );
        }
    }

    private void prettyPrintEnd(String pathToJson){
        getLog().info("");
        getLog().info("You find your json file here: " + pathToJson);
        getLog().info("");
    }
}
