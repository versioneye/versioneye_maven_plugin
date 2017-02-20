package com.versioneye;

import com.versioneye.dependency.ExclusiveScopeFilter;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DefaultArtifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.junit.Test;

import java.util.Arrays;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


public class ExclusiveScopeFilterTest {

    @Test
    public void providedArtifactIsIncludedWhenTestIsExcluded() throws Exception {
        ArtifactFilter filter = new ExclusiveScopeFilter(Arrays.asList("test"));

        Artifact providedArtifact = new DefaultArtifact("groupid", "artifactid", "version", "provided", "type", "classifier", null);

        boolean result = filter.include(providedArtifact);

        assertTrue(result);
    }

    @Test
    public void testArtifactIsIncludedWhenTestIsExcluded() throws Exception {
        ArtifactFilter filter = new ExclusiveScopeFilter(Arrays.asList("test"));

        Artifact providedArtifact = new DefaultArtifact("groupid", "artifactid", "version", "test", "type", "classifier", null);

        boolean result = filter.include(providedArtifact);

        assertFalse(result);
    }
}
