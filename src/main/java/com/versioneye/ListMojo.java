package com.versioneye;

import com.versioneye.dependency.DependencyResolver;
import com.versioneye.log.Logger;
import com.versioneye.log.MavenLogger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.List;
import java.util.Set;

import static com.versioneye.dependency.DependencyResolver.asSortedList;

/**
 * Lists all direct and recursive dependencies.
 */
@Mojo(name = "list", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class ListMojo extends ProjectMojo {

    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
        try {
            versionEyeOutput();

            DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);

            Set<Artifact> directArtifacts = dependencyResolver.getDirectDependencies();
            Set<Artifact> transitiveArtifacts = dependencyResolver.getTransitiveDependencies();

            transitiveArtifacts.removeAll(directArtifacts);
            logArtifactsList("Direct", asSortedList(directArtifacts));
            logArtifactsList("Transitive", asSortedList(transitiveArtifacts));

            logSummary(directArtifacts.size(), transitiveArtifacts.size());
        } catch (Exception exception) {
            throw new MojoExecutionException("Oh no! Something went wrong. " +
                    "Get in touch with the VersionEye guys and give them feedback. " +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception);
        }
    }

    protected void versionEyeOutput() {
        log.info("");
        log.info("************* \\_/ VersionEye \\_/ *************");
        log.info("");
    }

    protected void logSummary(int directCount, int transitiveCount) {
        int total = directCount + transitiveCount;
        log.info("");
        log.info(directCount + " Direct dependencies and " + transitiveCount + " transitive dependencies. This project has " + total + " dependencies.");
        log.info("");
    }

    protected void logArtifactsList(String type, List<Artifact> artifacts) {
        log.info("");
        log.info(artifacts.size() + " " + type + " dependencies:");
        log.info("--------------------");
        for (Artifact artifact : artifacts) {
            log.info(artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion() + ":" + artifact.getScope());
        }
        log.info("");
    }

}
