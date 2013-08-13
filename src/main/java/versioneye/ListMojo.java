package versioneye;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;
import versioneye.utils.DependencyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Lists all direct and recursive dependencies.
 */
@Mojo( name = "list", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class ListMojo extends ProjectMojo {

    public void execute() throws MojoExecutionException {
        versionEyeOutput();
        try{
            PreorderNodeListGenerator nlg = new PreorderNodeListGenerator();
            DependencyNode root = getDependencyNode(nlg);
            DependencyUtils dependencyUtils = new DependencyUtils();
            List<Artifact> dependencies          = dependencyUtils.collectAllDependencies(nlg.getDependencies(true));
            List<Artifact> directDependencies    = dependencyUtils.collectDirectDependencies(root.getChildren());
            List<Artifact> recursiveDependencies = new ArrayList<Artifact>(dependencies);
            recursiveDependencies.removeAll(directDependencies);
            produceNiceOutput(dependencies, directDependencies, recursiveDependencies);
        } catch( Exception exception ){
            throw new MojoExecutionException( "Oh no! Something went wrong. " +
                    "Get in touch with the VersionEye guys and give them feedback. " +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception );
        }
    }

    private void produceNiceOutput(List<Artifact> dependencies, List<Artifact> directDependencies, List<Artifact> recursiveDependencies){
        productNiceOutputForDirectDependencies(directDependencies);
        productNiceOutputForRecursiveDependencies(recursiveDependencies);
        produceNiceOutputSummary(directDependencies.size(), recursiveDependencies.size(), dependencies.size());
    }

    private void versionEyeOutput(){
        getLog().info("");
        getLog().info("************* \\_/ VersionEye \\_/ *************");
        getLog().info("");
    }

    private void productNiceOutputForDirectDependencies(List<Artifact> directDependencies){
        getLog().info("");
        getLog().info("Direct Dependencies: ");
        getLog().info("--------------------");
        for (Artifact artifact : directDependencies){
            getLog().info(artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion());
        }
        getLog().info("");
    }

    private void productNiceOutputForRecursiveDependencies(List<Artifact> recursiveDependencies){
        getLog().info("");
        getLog().info("Recursive Dependencies: ");
        getLog().info("--------------------");
        for (Artifact artifact : recursiveDependencies){
            getLog().info(artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion());
        }
        getLog().info("");
    }

    private void produceNiceOutputSummary(int directCount, int recursiveCount, int allCount){
        getLog().info("");
        getLog().info(directCount + " Direct dependencies and " +
                recursiveCount + " recursive dependencies. This project has " +
                allCount + " dependencies.");
        getLog().info("");
        getLog().info("");
    }

}
