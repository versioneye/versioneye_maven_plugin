package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.ByteArrayOutputStream;

@Mojo( name = "securityAndLicenseCheck", defaultPhase = LifecyclePhase.VERIFY )
public class SecurityAndLicenseCheckMojo extends UpdateMojo {

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
            System.out.println("sv_count: " +  response.getSv_count());
            if (response.getSv_count() > 0){
                throw new MojoExecutionException("Some components have security vulnerabilities! " +
                        "More details here: " + fetchBaseUrl() + "/user/projects/" + response.getId() );
            }

            System.out.println("licenses_red: " +  response.getLicenses_red());

            if (response.getLicenses_red() > 0){
                throw new MojoExecutionException("Some components violate the license whitelist! " +
                        "More details here: " + fetchBaseUrl() + "/user/projects/" + response.getId() );
            }

            if (response.getLicenses_unknown() > 0 && licenseCheckBreakByUnknown == true ){
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
