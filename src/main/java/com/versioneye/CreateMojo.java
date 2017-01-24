package com.versioneye;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.versioneye.dto.ProjectJsonResponse;
import com.versioneye.utils.HttpUtils;
import com.versioneye.utils.PropertiesUtils;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

/**
 * Creates a project at VersionEye based on the dependencies from the current project.
 */
@SuppressWarnings("unused")
@Mojo( name = "create", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class CreateMojo extends ProjectMojo {

    @Parameter( property = "resource", defaultValue = "/projects?api_key=")
    private String resource;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            setProxy();
            prettyPrintStart();

            ByteArrayOutputStream jsonDependenciesStream;
            if (transitiveDependencies){
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

    @Override
    protected void writeProperties(ProjectJsonResponse response) throws Exception {
        Properties properties = fetchProjectProperties();
        if (response.getId() != null) {
            properties.setProperty("project_id", response.getId());
        }
        PropertiesUtils utils = new PropertiesUtils();
        utils.writeProperties(properties, getPropertiesPath());
    }

    @Override
    protected void merge(String childId) {
        if (!mergeAfterCreate) {
            return ;
        }
        try {
            if (StringUtils.isBlank(parentGroupId) || StringUtils.isBlank(parentArtifactId)) {
                MavenProject mp = project.getParent();
                if (mp == null || StringUtils.isBlank(mp.getGroupId()) || StringUtils.isBlank(mp.getArtifactId()) ){
                    return ;
                }
                parentGroupId = mp.getGroupId();
                parentArtifactId = mp.getArtifactId();
            }

            parentGroupId = parentGroupId.replaceAll("\\.", "~").replaceAll("/", ":");
            parentArtifactId = parentArtifactId.replaceAll("\\.", "~").replaceAll("/", ":");

            if (project.getGroupId().equals(parentGroupId) && project.getArtifactId().equals(parentArtifactId)){
                return ;
            }

            getLog().debug("group: " + parentGroupId + " artifact: " + parentArtifactId);
            String url = fetchBaseUrl() + apiPath + "/projects/" + parentGroupId + "/" + parentArtifactId + "/merge_ga/" + childId + "?api_key=" + fetchApiKey();

            String response = HttpUtils.get(url);
            getLog().debug("merge response: " + response);
        } catch (Exception ex) {
            getLog().error(ex);
        }
    }
}
