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
import versioneye.utils.PropertiesUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

/**
 * Updates an existing project at VersionEye with the dependencies from the current project.
 */
@Mojo( name = "update", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class UpdateMojo extends SuperMojo {

    @Parameter( property = "resource", defaultValue = "/projects?api_key=")
    private String resource;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            PropertiesUtils propertiesUtils = new PropertiesUtils();
            String propFile = projectDirectory + "/" + propertiesFile;

            File file = new File(propFile);
            if (!file.exists())
                throw new MojoExecutionException(propFile + " is missing! Read the instructions at https://github.com/versioneye/versioneye_maven_plugin");

            Properties properties = propertiesUtils.readProperties(propFile);
            String apiKey = properties.getProperty("api_key");
            String projectId = properties.getProperty("project_id");

            DependencyUtils dependencyUtils = new DependencyUtils();
            CollectRequest collectRequest = dependencyUtils.getCollectRequest(project, repos);
            DependencyNode root = system.collectDependencies(session, collectRequest).getRoot();
            DependencyRequest dependencyRequest = new DependencyRequest(root, null);
            system.resolveDependencies(session, dependencyRequest);
            root.accept(new PreorderNodeListGenerator());

            List<Artifact> directDependencies = dependencyUtils.collectDirectDependencies(root.getChildren());
            JsonUtils jsonUtils = new JsonUtils();
            ByteArrayOutputStream outStream = jsonUtils.dependenciesToJson(directDependencies);

            getLog().info(".");
            getLog().info("Starting to update dependencies on server. This can take a couple seconds ... ");
            getLog().info(".");

            String url = baseUrl + apiPath + resource + apiKey;
            HttpUtils httpUtils = new HttpUtils();
            Reader reader = httpUtils.post(url, outStream.toByteArray(), "project_file");

            ObjectMapper mapper = new ObjectMapper();
            ProjectJsonResponse response = mapper.readValue(reader, ProjectJsonResponse.class );

            prettyPrint( response );
            writeProperties( properties, response );
        } catch( Exception exception ){
            throw new MojoExecutionException("Oh no! Something went wrong. Get in touch with the VersionEye guys and give them feedback.", exception);
        }
    }

    private void prettyPrint(ProjectJsonResponse response) throws Exception {
        getLog().info(".");
        getLog().info(".");
        getLog().info("You can find your updated project here: " + baseUrl + "user/projects/" + response.getId() );
        getLog().info("");
        getLog().info("Dependencies: " + response.getDep_number());
        getLog().info("Outdated: "     + response.getOut_number());
        getLog().info("");
    }

}
