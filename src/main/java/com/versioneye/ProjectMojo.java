package com.versioneye;

import org.sonatype.aether.artifact.Artifact;
import org.sonatype.aether.collection.CollectRequest;
import org.sonatype.aether.graph.DependencyNode;
import org.sonatype.aether.resolution.DependencyRequest;
import org.sonatype.aether.util.graph.PreorderNodeListGenerator;
import com.versioneye.utils.DependencyUtils;
import com.versioneye.utils.JsonUtils;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Methods required to deal with projects resource
 */
public class ProjectMojo extends SuperMojo {

    protected ByteArrayOutputStream getDirectDependenciesJsonStream() throws Exception {
        JsonUtils jsonUtils = new JsonUtils();
        return jsonUtils.dependenciesToJson(project.getName(), project.getDependencies());
    }

    protected ByteArrayOutputStream getDirectArtifactsJsonStream() throws Exception {
        DependencyUtils dependencyUtils = new DependencyUtils();
        DependencyNode root = getDependencyNode(new PreorderNodeListGenerator());
        List<Artifact> directDependencies = dependencyUtils.collectDirectDependencies(root.getChildren());
        JsonUtils jsonUtils = new JsonUtils();
        return jsonUtils.artifactsToJson(directDependencies);
    }

    protected DependencyNode getDependencyNode(PreorderNodeListGenerator nlg) throws Exception {
        DependencyUtils dependencyUtils = new DependencyUtils();
        CollectRequest collectRequest = dependencyUtils.getCollectRequest(project, repos);
        DependencyNode root = system.collectDependencies(session, collectRequest).getRoot();
        DependencyRequest dependencyRequest = new DependencyRequest(root, null);
        system.resolveDependencies(session, dependencyRequest);
        root.accept(nlg);
        return root;
    }

}
