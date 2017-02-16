package com.versioneye;

import com.versioneye.dependency.ExclusiveScopeFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.testng.annotations.Test;

import java.util.Arrays;

import static org.testng.AssertJUnit.assertFalse;
import static org.testng.AssertJUnit.assertTrue;

public class ExclusiveScopeFilterTest {

    @Test
    public void providedArtifactIsIncludedWhenTestIsExcluded() throws Exception {
        String[] scopes = {"test"};
        ArtifactFilter filter = new ExclusiveScopeFilter(Arrays.asList(scopes));

        Artifact providedArtifact = new DefaultArtifact("groupid", "artifactid", "version", "provided", "type", "classifier", null);

        boolean result = filter.include(providedArtifact);

        assertTrue(result);
    }

    @Test
    public void testArtifactIsIncludedWhenTestIsExcluded() throws Exception {
        String[] scopes = {"test"};
        ArtifactFilter filter = new ExclusiveScopeFilter(Arrays.asList(scopes));

        Artifact providedArtifact = new DefaultArtifact("groupid", "artifactid", "version", "test", "type", "classifier", null);

        boolean result = filter.include(providedArtifact);

        assertFalse(result);
    }
}
