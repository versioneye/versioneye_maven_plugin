package versioneye;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;
import versioneye.utils.DependencyUtils;
import versioneye.utils.JsonUtils;

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
            DependencyUtils dependencyUtils = new DependencyUtils();

            List<Artifact> dependencies          = dependencyUtils.collectAllDependencies(nlg.getDependencies(true));
            List<Artifact> directDependencies    = dependencyUtils.collectDirectDependencies(root.getChildren());
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
        jsonUtils.dependenciesToJsonFile(dependencies, filePath);
        return filePath;
    }

    private void prettyPrintEnd(String pathToJson){
        getLog().info("");
        getLog().info("You find your json file here: " + pathToJson);
        getLog().info("");
    }

}
