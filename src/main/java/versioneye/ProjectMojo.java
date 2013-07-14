package versioneye;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.sonatype.aether.RepositorySystem;
import org.sonatype.aether.RepositorySystemSession;
import org.sonatype.aether.repository.RemoteRepository;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: robertreiz
 * Date: 7/14/13
 * Time: 2:07 PM
 */
public class ProjectMojo extends AbstractMojo {

    protected static final String propertiesFile = "versioneye.properties";

    @Parameter( defaultValue = "${basedir}", property = "basedir", required = true)
    protected File projectDirectory;

    @Parameter( property = "create.baseUrl", defaultValue = "http://www.versioneye.com/" )
    protected String baseUrl;

    @Component
    protected RepositorySystem system;

    @Parameter( defaultValue="${project}" )
    protected MavenProject project;

    @Parameter( defaultValue="${repositorySystemSession}" )
    protected RepositorySystemSession session;

    @Parameter( defaultValue = "${project.remoteProjectRepositories}")
    protected List<RemoteRepository> repos;

    public void execute() throws MojoExecutionException, MojoFailureException {
        // implement me in child class.
    }

    protected void writeProperties(Properties properties, ProjectJsonResponse response) throws Exception {
        properties.setProperty("project_key", response.getProject_key());
        properties.setProperty("project_id", response.getId());
        PropertiesUtils utils = new PropertiesUtils();
        utils.writeProperties(properties, projectDirectory + "/" + propertiesFile);
    }

}
