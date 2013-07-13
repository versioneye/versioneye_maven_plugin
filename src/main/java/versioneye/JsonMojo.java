package versioneye;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.repository.RemoteRepository;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: robertreiz
 * Date: 7/13/13
 * Time: 2:08 PM
 */
@Mojo( name = "json", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class JsonMojo extends AbstractMojo {

    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    private File outputDirectory;

    @Component
    private RepositorySystem system;

    @Parameter( defaultValue="${project}" )
    private MavenProject project;

    @Parameter( defaultValue="${repositorySystemSession}" )
    private RepositorySystemSession session;

    @Parameter( defaultValue = "${project.remoteProjectRepositories}")
    private List<RemoteRepository> repos;

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
