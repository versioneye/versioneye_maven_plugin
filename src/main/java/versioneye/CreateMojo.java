package versioneye;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.jackson.map.ObjectMapper;
import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;
import versioneye.dto.ProjectJsonResponse;
import versioneye.utils.DependencyUtils;
import versioneye.utils.HttpUtils;
import versioneye.utils.JsonUtils;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.util.List;

/**
 * Creates a project at VersionEye based on the dependencies from the current project.
 */
@Mojo( name = "create", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class CreateMojo extends SuperMojo {

    @Parameter( property = "resource", defaultValue = "/projects?api_key=")
    private String resource;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            DependencyUtils dependencyUtils = new DependencyUtils();
            CollectRequest collectRequest = dependencyUtils.getCollectRequest(project, repos);
            DependencyNode root = system.collectDependencies(session, collectRequest).getRoot();
            DependencyRequest dependencyRequest = new DependencyRequest(root, null);
            system.resolveDependencies(session, dependencyRequest);
            root.accept(new PreorderNodeListGenerator());

            List<Artifact> directDependencies = dependencyUtils.collectDirectDependencies(root.getChildren());
            JsonUtils jsonUtils = new JsonUtils();
            ByteArrayOutputStream outStream = jsonUtils.dependenciesToJson(directDependencies);

            prettyPrintStart();
            ProjectJsonResponse response = uploadDependencies(outStream);
            writeProperties( response );
            prettyPrint( response );
        } catch( Exception exception ){
            throw new MojoExecutionException("Oh no! Something went wrong :-( " +
                    "Get in touch with the VersionEye guys and give them feedback." +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception);
        }
    }

    private ProjectJsonResponse uploadDependencies(ByteArrayOutputStream outStream) throws Exception {
        String apiKey = fetchApiKey();
        String url = baseUrl + apiPath + resource + apiKey;
        HttpUtils httpUtils = new HttpUtils();
        Reader reader = httpUtils.post(url, outStream.toByteArray(), "upload");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, ProjectJsonResponse.class );
    }

    private void prettyPrintStart(){
        getLog().info(".");
        getLog().info("Starting to upload dependencies. This can take a couple seconds ... ");
        getLog().info(".");
    }

    private void prettyPrint(ProjectJsonResponse response) throws Exception {
        getLog().info(".");
        getLog().info(".");
        getLog().info("Dependencies: " + response.getDep_number());
        getLog().info("Outdated: "     + response.getOut_number());
        getLog().info(".");
        getLog().info("You can find your project here: " + baseUrl + "/user/projects/" + response.getId() );
        getLog().info(".");
    }

}
