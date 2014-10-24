package com.versioneye;

import com.versioneye.utils.DependencyUtils;
import com.versioneye.utils.JsonUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Writes all direct dependencies into a JSON file.
 */
@Mojo( name = "json", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class JsonMojo extends ProjectMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
            DependencyNode root = getDependencyNode(nlg);

            List<Artifact> dependencies          = DependencyUtils.collectAllDependencies(nlg.getDependencies(true));
            List<Artifact> directDependencies    = DependencyUtils.collectDirectDependencies(root.getChildren());
            List<Artifact> recursiveDependencies = new ArrayList<Artifact>(dependencies);
            recursiveDependencies.removeAll(directDependencies);

            String pathToJson = writeToJson(directDependencies);
            prettyPrintEnd(pathToJson);
        } catch( Exception exception ){
            throw new MojoExecutionException( "Oh no! Something went wrong. " +
                    "Get in touch with the VersionEye guys and give them feedback. " +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception );
        }
    }

    private String writeToJson(List<Artifact> dependencies) throws Exception {
        JsonUtils jsonUtils = new JsonUtils();
        String filePath = outputDirectory + "/pom.json";
        jsonUtils.dependenciesToJsonFile(project.getName(), dependencies, filePath);
        return filePath;
    }

    private void prettyPrintEnd(String pathToJson){
        getLog().info("");
        getLog().info("You find your json file here: " + pathToJson);
        getLog().info("");
    }

}
