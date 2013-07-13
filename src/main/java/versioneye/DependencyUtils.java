package versioneye;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.graph.Dependency;
import org.sonatype.aether.graph.DependencyNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: robertreiz
 * Date: 7/11/13
 * Time: 8:12 PM
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

}
