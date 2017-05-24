package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.ByteArrayOutputStream;

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
        try {
            String projectId = fetchProjectId();
            if (mavenSession.getTopLevelProject().getId().equals(mavenSession.getCurrentProject().getId())){
                mavenSession.getTopLevelProject().setContextValue("veye_project_id", projectId);
            }
            return updateExistingProject(resource, projectId, outStream);
        } catch (Exception ex) {
            getLog().error("Error in UpdateMojo.uploadDependencies " + ex.getMessage());
            ProjectJsonResponse response = createNewProject("/projects?api_key=", outStream);
            if (updatePropertiesAfterCreate) {
                writeProperties( response );
            }
            merge( response.getId() );
            return response;
        }
    }


    protected void prettyPrintStart(){
        getLog().info(".");
        getLog().info("Starting to update dependencies to server. This can take a couple seconds ... ");
        getLog().info(".");
    }


}
