package com.versioneye.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Utility methods for Maven Dependencies.
 */
public class DependencyUtils {

    public static List<Artifact> collectAllDependencies(List<Dependency> dependencies) {
        List<Artifact> result = new ArrayList<Artifact>(dependencies.size());
        for (Dependency dependency : dependencies) {
            result.add(dependency.getArtifact());
        }
        return result;
    }

    public static List<Artifact> collectDirectDependencies(List<DependencyNode> dependencies) {
        List<Artifact> result = new ArrayList<Artifact>(dependencies.size());
        for (DependencyNode dependencyNode : dependencies) {
            result.add(dependencyNode.getDependency().getArtifact());
        }
        return result;
    }

    public static CollectRequest getCollectRequest(MavenProject project, List<RemoteRepository> repos){
        Artifact a = new DefaultArtifact( project.getArtifact().toString() );
        DefaultArtifact pom = new DefaultArtifact( a.getGroupId(), a.getArtifactId(), "pom", a.getVersion() );
        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot(new Dependency(pom, "compile"));
        collectRequest.setRepositories(repos);
        return collectRequest;
    }

}
