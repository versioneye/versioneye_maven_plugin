package versioneye;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;
import versioneye.utils.DependencyUtils;
import versioneye.utils.JsonUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Writes all direct dependencies into a JSON file.
 */
@Mojo( name = "json", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class JsonMojo extends SuperMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            DependencyUtils dependencyUtils = new DependencyUtils();
            CollectRequest collectRequest = dependencyUtils.getCollectRequest(project, repos);
            DependencyNode root = system.collectDependencies(session, collectRequest).getRoot();
            DependencyRequest dependencyRequest = new DependencyRequest(root, null);

            system.resolveDependencies(session, dependencyRequest);

            PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
            root.accept(nlg);

            List<Artifact> dependencies          = dependencyUtils.collectAllDependencies(nlg.getDependencies(true));
            List<Artifact> directDependencies    = dependencyUtils.collectDirectDependencies(root.getChildren());
            List<Artifact> recursiveDependencies = new ArrayList<Artifact>(dependencies);
            recursiveDependencies.removeAll(directDependencies);

            JsonUtils jsonUtils = new JsonUtils();
            String filePath = outputDirectory + "/pom.json";
            jsonUtils.dependenciesToJsonFile(directDependencies, filePath);

            getLog().info("");
            getLog().info("You find your json file here: " + filePath);
            getLog().info("");
        } catch( Exception exception ){
            exception.printStackTrace();
            getLog().error("Oh no! Something went wrong. Get in touch with the VersionEye guys and give them feedback.");
            getLog().error( exception );
        }
    }

}
