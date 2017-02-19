package com.versioneye;

import com.versioneye.dependency.DependencyResolver;
import com.versioneye.log.Logger;
import com.versioneye.log.MavenLogger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import java.util.Set;

import static com.versioneye.dependency.DependencyResolver.asSortedList;
import static com.versioneye.dependency.DependencyResolver.mergeArtifactsWithStrongestScope;

@Mojo(name = "aggregated-list", defaultPhase = LifecyclePhase.PROCESS_SOURCES)
public class AggregatedListMojo extends ListMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        log = getLog();
        Logger logger = new MavenLogger(log);

        final int size = reactorProjects.size();
        MavenProject lastProject = reactorProjects.get(size - 1);
        if (lastProject != project) {
            // Skip all projects except the last, to make sure all dependencies in all reactor project have been initialized
            logger.info("Skipping");
            return;
        }

        try {
            versionEyeOutput();

            DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);

            Set<Artifact> directArtifacts = dependencyResolver.getDirectDependencies();
            Set<Artifact> transitiveDependencies = dependencyResolver.getTransitiveDependencies();

            for (MavenProject project : reactorProjects) {
                logger.debug("---------------------------------------------");
                logger.debug(" --- Project: " + project.getArtifactId());
                DependencyResolver reactorProjectDependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);
                logger.debug(" --- Direct: ");
                directArtifacts = mergeArtifactsWithStrongestScope(directArtifacts, reactorProjectDependencyResolver.getDirectDependencies());
                logger.debug(" --- Transitive: ");
                transitiveDependencies = mergeArtifactsWithStrongestScope(transitiveDependencies, reactorProjectDependencyResolver.getTransitiveDependencies());
            }

            transitiveDependencies.removeAll(directArtifacts);
            logArtifactsList("Direct", asSortedList(directArtifacts));
            logArtifactsList("Transitive", asSortedList(transitiveDependencies));
            logSummary(directArtifacts.size(), transitiveDependencies.size());
        } catch (Exception exception) {
            throw new MojoExecutionException("Oh no! Something went wrong. " +
                    "Get in touch with the VersionEye guys and give them feedback. " +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception);
        }

    }
}
