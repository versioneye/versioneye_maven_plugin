package versioneye;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.jackson.map.ObjectMapper;
import versioneye.dto.ProjectJsonResponse;
import versioneye.utils.HttpUtils;

import java.io.ByteArrayOutputStream;
import java.io.Reader;

/**
 * Updates an existing project at VersionEye with the dependencies from the current project.
 */
@Mojo( name = "update", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class UpdateMojo extends ProjectMojo {

    @Parameter( property = "resource", defaultValue = "/projects")
    private String resource;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            prettyPrintStart();
            ByteArrayOutputStream jsonDirectDependenciesStream = getDirectDependenciesJsonStream();
            ProjectJsonResponse response = uploadDependencies(jsonDirectDependenciesStream);
            writeProperties( response );
            prettyPrint( response );
        } catch( Exception exception ){
            throw new MojoExecutionException("Oh no! Something went wrong. " +
                    "Get in touch with the VersionEye guys and give them feedback." +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception);
        }
    }

    private ProjectJsonResponse uploadDependencies(ByteArrayOutputStream outStream) throws Exception {
        String apiKey = fetchApiKey();
        String projectId = properties.getProperty("project_id");
        String url = baseUrl + apiPath + resource + "/" + projectId + "?api_key=" + apiKey;
        HttpUtils httpUtils = new HttpUtils();
        Reader reader = httpUtils.post(url, outStream.toByteArray(), "project_file");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, ProjectJsonResponse.class );
    }

    private void prettyPrintStart(){
        getLog().info(".");
        getLog().info("Starting to update dependencies to server. This can take a couple seconds ... ");
        getLog().info(".");
    }

    private void prettyPrint(ProjectJsonResponse response) throws Exception {
        getLog().info(".");
        getLog().info("Dependencies: " + response.getDep_number());
        getLog().info("Outdated: "     + response.getOut_number());
        getLog().info("");
        getLog().info("You can find your updated project here: " + baseUrl + "/user/projects/" + response.getId() );
        getLog().info("");
    }

}
