package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import com.versioneye.utils.HttpUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.Reader;

/**
 * Updates an existing project at VersionEye with the dependencies from the current project.
 */
@Mojo( name = "update", defaultPhase = LifecyclePhase.PACKAGE )
public class UpdateMojo extends ProjectMojo {

    @Parameter( property = "resource", defaultValue = "/projects")
    private String resource;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            setProxy();
            prettyPrintStart();

            ByteArrayOutputStream jsonDependenciesStream = null;
            if (transitiveDependencies == true){
                jsonDependenciesStream = getTransitiveDependenciesJsonStream(nameStrategy);
            } else {
                jsonDependenciesStream = getDirectDependenciesJsonStream(nameStrategy);
            }

            if (jsonDependenciesStream == null){
                prettyPrint0End();
                return ;
            }

            ProjectJsonResponse response = uploadDependencies(jsonDependenciesStream);
            prettyPrint( response );
        } catch( Exception exception ){
            exception.printStackTrace();
            throw new MojoExecutionException("Oh no! Something went wrong. " +
                    "Get in touch with the VersionEye guys and give them feedback. " +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception);
        }
    }

    protected ProjectJsonResponse uploadDependencies(ByteArrayOutputStream outStream) throws Exception {
        String apiKey = fetchApiKey();
        String projectId = fetchProjectId();
        String url = baseUrl + apiPath + resource + "/" + projectId + "?api_key=" + apiKey;
        Reader reader = HttpUtils.post(url, outStream.toByteArray(), "project_file", null, null, null, null);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, ProjectJsonResponse.class );
    }

    protected void prettyPrintStart(){
        getLog().info(".");
        getLog().info("Starting to update dependencies to server. This can take a couple seconds ... ");
        getLog().info(".");
    }

}
