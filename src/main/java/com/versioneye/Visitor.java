package com.versioneye;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Visitor implements DependencyNodeVisitor {

    Map<String, Artifact> artifacts;

    public Visitor() {
        this.artifacts = new HashMap<>();
    }

    @Override
    public boolean visit(DependencyNode node) {
        if( node.getParent() != null ) {

            artifacts.put(node.toNodeString(), node.getArtifact());
        }
        return true;
    }

    @Override
    public boolean endVisit(DependencyNode dependencyNode) {
        return true;
    }

    public Set<Artifact> getArtifacts() {
        return new HashSet<>(artifacts.values());
    }
}