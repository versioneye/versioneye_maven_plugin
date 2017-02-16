package com.versioneye;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

import java.util.ArrayList;
import java.util.List;

public class ExclusiveScopeFilter implements ArtifactFilter {
    private List<String> excludeScopes = new ArrayList<>();

    public ExclusiveScopeFilter(List<String> excludeScopes) {
        for(String excludeScope : excludeScopes) {
            excludeScope = excludeScope.trim().toLowerCase();
            if(!excludeScope.isEmpty()) {
                this.excludeScopes.add(excludeScope);
            }
        }
        this.excludeScopes = excludeScopes;
    }

    public boolean include(Artifact artifact) {
        String scope = artifact.getScope();
        return !excludeScopes.contains(scope);
    }
}