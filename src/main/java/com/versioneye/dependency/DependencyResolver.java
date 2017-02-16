package com.versioneye.dependency;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.util.List;
import java.util.Set;

public class DependencyResolver {

    private final MavenProject project;
    private DependencyGraphBuilder dependencyGraphBuilder;
    private final List<String> excludedScopes;

    public DependencyResolver(MavenProject project, DependencyGraphBuilder dependencyGraphBuilder, List<String> excludedScopes) {
        this.project = project;
        this.dependencyGraphBuilder = dependencyGraphBuilder;
        this.excludedScopes = excludedScopes;
    }

    public Set<Artifact> getTransitiveDependencies() throws DependencyGraphBuilderException {
        ArtifactFilter filter = new ExclusiveScopeFilter(excludedScopes);
        DependencyNode node = dependencyGraphBuilder.buildDependencyGraph(project, filter);
        TransitiveVisitor visitor = new TransitiveVisitor(node);
        node.accept(visitor);
        return visitor.getTransitiveArtifacts();
    }

    public Set<Artifact> getDirectDependencies() throws DependencyGraphBuilderException {
        ArtifactFilter filter = new ExclusiveScopeFilter(excludedScopes);
        DependencyNode node = dependencyGraphBuilder.buildDependencyGraph(project, filter);
        DirectVisitor visitor = new DirectVisitor(node);
        node.accept(visitor);
        return visitor.getDirectArtifacts();
    }
}
