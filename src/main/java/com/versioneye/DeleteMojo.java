package com.versioneye;


import com.versioneye.utils.HttpUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;

@Mojo( name = "delete", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class DeleteMojo extends ProjectMojo {

    @Parameter( property = "resource", defaultValue = "/projects")
    private String resource;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try{
            setProxy();
            prettyPrintStart();
            deleteProject();
            deletePropertiesFile();
        } catch( Exception exception ){
            exception.printStackTrace();
            throw new MojoExecutionException("Oh no! Something went wrong. " +
                    "Get in touch with the VersionEye guys and give them feedback. " +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception);
        }
    }

    protected void deleteProject() throws Exception {
        String apiKey = fetchApiKey();
        String projectId = fetchProjectId();
        String url = fetchBaseUrl() + apiPath + resource + "/" + projectId + "?api_key=" + apiKey;

        HttpUtils.delete(url);
    }

    protected void deletePropertiesFile() throws Exception{
        String propertiesPath = getPropertiesPath();
        File file = new File(propertiesPath);
        file.delete();
    }

    protected void prettyPrintStart(){
        getLog().info(".");
        getLog().info("Starting to delete this project from the VersionEye server. This can take a couple seconds ... ");
        getLog().info(".");
    }
}
