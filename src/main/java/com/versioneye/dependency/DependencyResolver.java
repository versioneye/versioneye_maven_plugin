package com.versioneye.dependency;

import com.versioneye.log.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.artifact.DefaultArtifact;

import java.util.*;

import static com.versioneye.dependency.Scope.getStrongestScope;
import static com.versioneye.dependency.Scope.scopeFromString;
import static com.versioneye.log.Logger.getLogger;

public class DependencyResolver {
    private static final Logger LOGGER = getLogger();

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

    public static Set<Artifact> mergeArtifactsWithStrongestScope(Set<Artifact> artifactSet1, Set<Artifact> artifactSet2) {
        Set<Artifact> artifacts = new HashSet<>();
        for(Artifact artifactFromSet1 : artifactSet1) {
            DefaultArtifact artifact = new DefaultArtifact(artifactFromSet1.getGroupId(),
                    artifactFromSet1.getArtifactId(),
                    artifactFromSet1.getVersion(),
                    artifactFromSet1.getScope(),
                    artifactFromSet1.getType(),
                    artifactFromSet1.getClassifier(),
                    artifactFromSet1.getArtifactHandler());

            for (Artifact artifactFromSet2 : artifactSet2) {
                if (artifactFromSet2.equals(artifactFromSet1)) {
                    Scope scope1 = scopeFromString(artifactFromSet1.getScope());
                    Scope scope2 = scopeFromString(artifactFromSet2.getScope());
                    Scope strongestScope = getStrongestScope(scope1, scope2);
                    LOGGER.debug("[DEBUG] Setting strongest scope to " + strongestScope.getName() + " for " + artifact.getArtifactId());
                    artifact.setScope(strongestScope.getName());
                    break;
                }
            }
            artifacts.add(artifact);
        }
        artifacts.addAll(artifactSet2);
        return artifacts;
    }

    public static <T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
        List<T> list = new ArrayList<T>(c);
        java.util.Collections.sort(list);
        return list;
    }
}
