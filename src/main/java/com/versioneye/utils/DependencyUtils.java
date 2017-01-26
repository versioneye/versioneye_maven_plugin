package com.versioneye.utils;

import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utility methods for Maven Dependencies.
 */
public class DependencyUtils {

    private DependencyUtils() {}

    public static List<Artifact> collectAllDependencies(List<Dependency> dependencies) {

      List<Artifact> artifacts = dependencies.stream()
          .map(Dependency::getArtifact)
          .collect(Collectors.toList());

      return artifacts;
    }

    public static List<Artifact> collectDirectDependencies(List<DependencyNode> dependencies) {

      List<Artifact> artifacts = dependencies.stream()
          .map(DependencyNode::getDependency)
          .map(Dependency::getArtifact)
          .collect(Collectors.toList());

      return artifacts;
    }

    public static CollectRequest getCollectRequest(MavenProject project, List<RemoteRepository> repos) {
          Artifact a = new DefaultArtifact( project.getArtifact().toString() );
          DefaultArtifact pom = new DefaultArtifact( a.getGroupId(), a.getArtifactId(), "pom", a.getVersion() );
          CollectRequest collectRequest = new CollectRequest();
          collectRequest.setRoot(new Dependency(pom, "compile"));
          collectRequest.setRepositories(repos);
          return collectRequest;
    }
}
