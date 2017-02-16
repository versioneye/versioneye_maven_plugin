package com.versioneye.dependency;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import org.apache.maven.shared.dependency.graph.traversal.DependencyNodeVisitor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DirectVisitor implements DependencyNodeVisitor {

    private Map<String, Artifact> directArtifacts;
    private DependencyNode self;

    public DirectVisitor(DependencyNode self) {
        this.self = self;
        this.directArtifacts = new HashMap<>();
    }

    @Override
    public boolean visit(DependencyNode node) {
        if (node.getParent() != null) {
            if (node.getParent() == self) {
                directArtifacts.put(node.toNodeString(), node.getArtifact());
            }
        }
        return true;
    }

    @Override
    public boolean endVisit(DependencyNode dependencyNode) {
        return true;
    }

    public Set<Artifact> getDirectArtifacts() {
        return new HashSet<>(directArtifacts.values());
    }

}