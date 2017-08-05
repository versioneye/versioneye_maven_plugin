package com.versioneye;

import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import com.versioneye.dto.ProjectJsonResponse;

import java.io.ByteArrayOutputStream;

/**
 * Updates an existing project at VersionEye with the dependencies from the current project AND
 * ensures that all used licenses are on a whitelist. If that is not the case it breaks the build.
 */
@SuppressWarnings("unused")
@Mojo( name = "licenseCheck", defaultPhase = LifecyclePhase.VERIFY )
public class LicenseCheckMojo extends UpdateMojo {

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
            System.out.println(response.getLicenses_red());
            if (response.getLicenses_red() > 0){
                throw new MojoExecutionException("Some components violate the license whitelist! " +
                    "More details here: " + fetchBaseUrl() + "/user/projects/" + response.getId() );
            }

            if (response.getLicenses_unknown() > 0 && licenseCheckBreakByUnknown) {
                throw new MojoExecutionException("Some components are without any license! " +
                    "More details here: " + fetchBaseUrl() + "/user/projects/" + response.getId() );
            }

            prettyPrint( response );
        } catch( Exception exception ){
            exception.printStackTrace();
            throw new MojoExecutionException("Oh no! Something went wrong. " +
                "Get in touch with the VersionEye guys and give them feedback. " +
                "You find them on Twitter at https//twitter.com/VersionEye. ", exception);
        }
    }
}
