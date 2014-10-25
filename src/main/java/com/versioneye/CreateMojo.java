package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import com.versioneye.utils.HttpUtils;
import com.versioneye.utils.PropertiesUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.util.Properties;

/**
 * Creates a project at VersionEye based on the dependencies from the current project.
 */
@Mojo( name = "create", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class CreateMojo extends ProjectMojo {

    @Parameter( property = "resource", defaultValue = "/projects?api_key=")
    private String resource;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            setProxy();
            prettyPrintStart();
            ByteArrayOutputStream jsonDirectDependenciesStream = getDirectDependenciesJsonStream();
            if (jsonDirectDependenciesStream == null){
                prettyPrint0End();
                return ;
            }
            ProjectJsonResponse response = uploadDependencies(jsonDirectDependenciesStream);
            if (updatePropertiesAfterCreate) {
                writeProperties( response );
            }
            prettyPrintEnd(response);
        } catch( Exception exception ){
            throw new MojoExecutionException("Oh no! Something went wrong :-( " +
                    "Get in touch with the VersionEye guys and give them feedback." +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception);
        }
    }

    private ProjectJsonResponse uploadDependencies(ByteArrayOutputStream outStream) throws Exception {
        String apiKey = fetchApiKey();
        String url = baseUrl + apiPath + resource + apiKey;
        Reader reader = HttpUtils.post(url, outStream.toByteArray(), "upload");
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, ProjectJsonResponse.class );
    }

    private void prettyPrintStart(){
        getLog().info(".");
        getLog().info("Starting to upload dependencies. This can take a couple seconds ... ");
        getLog().info(".");
    }

    private void prettyPrintEnd(ProjectJsonResponse response) throws Exception {
        getLog().info(".");
        getLog().info("Project name: " + response.getName());
        getLog().info("Project id: "   + response.getId());
        getLog().info("Dependencies: " + response.getDep_number());
        getLog().info("Outdated: "     + response.getOut_number());
        getLog().info(".");
        getLog().info("You can find your project here: " + baseUrl + "/user/projects/" + response.getId() );
        getLog().info(".");
    }

    private void prettyPrint0End() throws Exception {
        getLog().info(".");
        getLog().info("There are no dependencies in this project! - " + project.getGroupId() + "/" + project.getArtifactId() );
        getLog().info(".");
    }

    protected void writeProperties(ProjectJsonResponse response) throws Exception {
        Properties properties = fetchProjectProperties();
        if (response.getId() != null) {
            properties.setProperty("project_id", response.getId());
        }
        PropertiesUtils utils = new PropertiesUtils();
        utils.writeProperties(properties, getPropertiesPath());
    }

}
