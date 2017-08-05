package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import com.versioneye.utils.HttpUtils;
import com.versioneye.utils.PropertiesUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
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

      if (mavenSession.getTopLevelProject().getId().equals(mavenSession.getCurrentProject().getId())){
        mavenSession.getTopLevelProject().setContextValue("veye_project_id", response.getId());
      }

      merge( response.getId() );
      if (updatePropertiesAfterCreate) {
        writeProperties( response );
      }
      prettyPrint(response);
    } catch( Exception exception ){
      throw new MojoExecutionException("Oh no! Something went wrong :-( " +
        "Get in touch with the VersionEye guys and give them feedback." +
        "You find them on Twitter at https//twitter.com/VersionEye. ", exception);
    }
  }


  private ProjectJsonResponse uploadDependencies(ByteArrayOutputStream outStream) throws Exception {
    return createNewProject(resource, outStream);
  }


  private void prettyPrintStart(){
    getLog().info(".");
    getLog().info("Starting to upload dependencies. This can take a couple seconds ... ");
    getLog().info(".");
  }


}
