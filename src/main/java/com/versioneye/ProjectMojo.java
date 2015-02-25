package com.versioneye;

import com.versioneye.dto.ProjectJsonResponse;
import com.versioneye.utils.DependencyUtils;
import com.versioneye.utils.JsonUtils;
import org.apache.maven.model.Dependency;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.DependencyNode;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

/**
 * Methods required to deal with projects resource
 */
public class ProjectMojo extends SuperMojo {

    protected ByteArrayOutputStream getDirectDependenciesJsonStream(String nameStrategy) throws Exception {
        List<Dependency> dependencies = project.getDependencies();
        JsonUtils jsonUtils = new JsonUtils();
        return jsonUtils.dependenciesToJson(project, dependencies, nameStrategy);
    }

    protected Map<String, Object> getDirectDependenciesJsonMap(String nameStrategy) throws Exception {
        List<Dependency> dependencies = project.getDependencies();
        if (dependencies == null || dependencies.isEmpty()){
            return null;
        } else {
            iterateThrough(dependencies);
        }
        JsonUtils jsonUtils = new JsonUtils();
        List<Map<String, Object>> dependencyHashes = jsonUtils.getDependencyHashes(dependencies);
        return jsonUtils.getJsonPom(project, dependencyHashes, nameStrategy);
    }

    protected ByteArrayOutputStream getDirectArtifactsJsonStream() throws Exception {
        DependencyNode root = getDependencyNode(new PreorderNodeListGenerator());
        List<Artifact> directDependencies = DependencyUtils.collectDirectDependencies(root.getChildren());
        JsonUtils jsonUtils = new JsonUtils();
        return jsonUtils.artifactsToJson(directDependencies);
    }

    protected DependencyNode getDependencyNode(PreorderNodeListGenerator nlg) throws Exception {
        CollectRequest collectRequest = DependencyUtils.getCollectRequest(project, repos);
        DependencyNode root = system.collectDependencies(session, collectRequest).getRoot();
        DependencyRequest dependencyRequest = new DependencyRequest(root, null);
        system.resolveDependencies(session, dependencyRequest);
        root.accept(nlg);
        return root;
    }

    protected void prettyPrint0End() throws Exception {
        getLog().info(".");
        getLog().info("There are no dependencies in this project! - " + project.getGroupId() + "/" + project.getArtifactId() );
        getLog().info(".");
    }

    protected void prettyPrint(ProjectJsonResponse response) throws Exception {
        getLog().info(".");
        getLog().info("Project name: " + response.getName());
        getLog().info("Project id: "   + response.getId());
        getLog().info("Dependencies: " + response.getDep_number());
        getLog().info("Outdated: "     + response.getOut_number());
        getLog().info("");
        getLog().info("You can find your updated project here: " + baseUrl + "/user/projects/" + response.getId() );
        getLog().info("");
    }

    private void iterateThrough(List<Dependency> dependencies){
        for(Dependency dep: dependencies){
            getLog().info(" - dependency: " + dep.getGroupId() + "/" + dep.getArtifactId() + " " + dep.getVersion());
        }
    }

}
