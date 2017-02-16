package com.versioneye.dependency;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TransitiveVisitor implements DependencyNodeVisitor {

    private Map<String, Artifact> transitiveArtifacts;
    private DependencyNode self;

    public TransitiveVisitor(DependencyNode self) {
        this.self = self;
        this.transitiveArtifacts = new HashMap<>();
    }

    @Override
    public boolean visit(DependencyNode node) {
        if (node.getParent() != null && node.getParent() != self) {
            transitiveArtifacts.put(node.toNodeString(), node.getArtifact());
        }
        return true;
    }

    @Override
    public boolean endVisit(DependencyNode dependencyNode) {
        return true;
    }

    public Set<Artifact> getTransitiveArtifacts() {
        return new HashSet<>(transitiveArtifacts.values());
    }

}