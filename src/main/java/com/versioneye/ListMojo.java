package com.versioneye;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import java.util.Set;

/**
 * Lists all direct and recursive dependencies.
 */
@Mojo( name = "list", defaultPhase = LifecyclePhase.PROCESS_SOURCES )
public class ListMojo extends ProjectMojo {

    @Component(hint = "default")
    private DependencyGraphBuilder dependencyGraphBuilder;

    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();
        try{
            versionEyeOutput();
            for(String scope : excludeScopes) log.debug(scope);

            Visitor visitor = new Visitor();
            ArtifactFilter filter = new ExclusiveScopeFilter(excludeScopes);
            DependencyNode node = dependencyGraphBuilder.buildDependencyGraph(project, filter);
            node.accept(visitor);
            Set<Artifact> artifacts = visitor.getArtifacts();
            for(Artifact artifact : artifacts) {
                log.info( artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion() + ":" + artifact.getScope());
            }
            produceNiceOutputSummary(artifacts.size());

        } catch( Exception exception ){
            throw new MojoExecutionException( "Oh no! Something went wrong. " +
                    "Get in touch with the VersionEye guys and give them feedback. " +
                    "You find them on Twitter at https//twitter.com/VersionEye. ", exception );
        }
    }

    private void versionEyeOutput(){
        log.info("");
        log.info("************* \\_/ VersionEye \\_/ *************");
        log.info("");
    }

    private void produceNiceOutputSummary(int count) {
        log.info("");
        log.info("This project has " + count + " dependencies.");
        log.info("");
    }

}
