package com.versioneye;

import com.versioneye.utils.PropertiesUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Properties;

/**
 * The Mother of all Mojos!
 */
public class SuperMojo extends AbstractMojo {

    protected static final String propertiesFile = "versioneye.properties";

    @Component
    protected RepositorySystem system;

    @Parameter( defaultValue="${project}" )
    protected MavenProject project;

    @Parameter( defaultValue="${repositorySystemSession}" )
    protected RepositorySystemSession session;

    @Parameter( defaultValue = "${project.remoteProjectRepositories}")
    protected List<RemoteRepository> repos;

    @Parameter( defaultValue = "${basedir}", property = "basedir", required = true)
    protected File projectDirectory;

    @Parameter( defaultValue = "${project.build.directory}", property = "outputDir", required = true )
    protected File outputDirectory;

    @Parameter( defaultValue = "${user.home}" )
    protected File homeDirectory;

    @Parameter( property = "baseUrl", defaultValue = "https://www.versioneye.com" )
    protected String baseUrl;

    @Parameter( property = "apiPath", defaultValue = "/api/v2" )
    protected String apiPath;

    @Parameter( property = "projectId" )
    protected String projectId;

    @Parameter( property = "apiKey" )
    protected String apiKey;

    @Parameter( property = "propertiesPath" )
    protected String propertiesPath = null;

    @Parameter( property = "proxyHost" )
    protected String proxyHost = null;

    @Parameter( property = "proxyPort" )
    protected String proxyPort = null;

    @Parameter( property = "proxyUser" )
    protected String proxyUser = null;

    @Parameter( property = "proxyPassword" )
    protected String proxyPassword = null;

    @Parameter( property = "updatePropertiesAfterCreate" )
    protected boolean updatePropertiesAfterCreate = true;

    @Parameter( property = "mergeAfterCreate" )
    protected boolean mergeAfterCreate = true;

    @Parameter( property = "parentGroupId" )
    protected String parentGroupId = null;

    @Parameter( property = "parentArtifactId" )
    protected String parentArtifactId = null;

    protected Properties properties = null;     // Properties in src/main/resources
    protected Properties homeProperties = null; // Properties in ~/.m2/

    public void execute() throws MojoExecutionException, MojoFailureException {  }

    protected String fetchApiKey() throws Exception {
        if (apiKey != null && !apiKey.isEmpty() )
            return apiKey;
        Properties properties = fetchPropertiesFor("api_key");
        apiKey = properties.getProperty("api_key");
        if (apiKey == null || apiKey.isEmpty()){
            String msg = "versioneye.properties found but without an API Key! Read the instructions at https://github.com/versioneye/versioneye_maven_plugin";
            getLog().error(msg);
            throw new MojoExecutionException(msg);
        }
        return apiKey;
    }

    protected String fetchProjectId() throws Exception {
        if (projectId != null && !projectId.isEmpty() )
            return projectId;
        Properties properties = fetchPropertiesFor("project_id");
        projectId = properties.getProperty("project_id");
        if (projectId == null || projectId.isEmpty()){
            String msg = "versioneye.properties found but without project_id! Read the instructions at https://github.com/versioneye/versioneye_maven_plugin";
            getLog().error(msg);
            throw new MojoExecutionException(msg);
        }

        return projectId;
    }

    protected Properties fetchPropertiesFor( String key ) throws Exception {
        Properties properties = fetchProjectProperties();
        if (properties == null || properties.getProperty( key ) == null)
            properties = fetchHomeProperties();
        return properties;
    }

    protected Properties fetchProjectProperties() throws Exception {
        if (properties != null)
            return properties;
        String propertiesPath = getPropertiesPath();
        File file = new File(propertiesPath);
        if (!file.exists())
            createPropertiesFile(file);
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        properties = propertiesUtils.readProperties(propertiesPath);
        return properties;
    }

    protected Properties fetchHomeProperties() throws Exception {
        if (homeProperties != null)
            return homeProperties;
        String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
        File file = new File(propertiesPath);
        if (!file.exists()) {
            propertiesPath = projectDirectory + "/src/main/resources/" + propertiesFile;
            file = new File(propertiesPath);
            if (file.exists()) {
                getLog().warn(propertiesFile + " exists in src/main/resources, should be moved to src/qa/resources");
            }
        }
        if (!file.exists()){
            String msg = propertiesPath + " is missing! Read the instructions at " +
                    "https://github.com/versioneye/versioneye_maven_plugin";
            getLog().error(msg);
            throw new MojoExecutionException(msg);
        }
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        homeProperties = propertiesUtils.readProperties(propertiesPath);
        return homeProperties;
    }

    protected String getPropertiesPath() throws Exception {
        if (this.propertiesPath != null)
            return this.propertiesPath;
        String propertiesPath = projectDirectory + "/src/qa/resources/" + propertiesFile;
        File file = new File(propertiesPath);
        if (!file.exists()) {
            propertiesPath = projectDirectory + "/src/main/resources/" + propertiesFile;
            file = new File(propertiesPath);
        }
        if (!file.exists()){
            propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
            file = new File(propertiesPath);
        }
        if (!file.exists()){
            propertiesPath = projectDirectory + "/src/main/resources/" + propertiesFile;
            file = new File(propertiesPath);
        }
        this.propertiesPath = propertiesPath;
        return propertiesPath;
    }

    private void createPropertiesFile(File file) throws IOException {
        File parent = file.getParentFile();
        if (!parent.exists()){
            parent.mkdirs();
        }
        file.createNewFile();
    }

    protected void initTls(){
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager(){
            public X509Certificate[] getAcceptedIssuers(){return null;}
            public void checkClientTrusted(X509Certificate[] certs, String authType){}
            public void checkServerTrusted(X509Certificate[] certs, String authType){}
        }};
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void setProxy(){
        boolean emptyProxyHost = proxyHost == null || proxyHost.isEmpty();
        boolean emptyProxyPort = proxyPort == null || proxyPort.isEmpty();
        if (emptyProxyHost && emptyProxyPort){
            return ;
        }
        System.setProperty("proxySet", "true");
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);

        boolean emptyProxyUser = proxyUser == null || proxyUser.isEmpty();
        boolean emptyProxyPass = proxyPassword == null || proxyPassword.isEmpty();
        if (emptyProxyUser && emptyProxyPass){
            return ;
        }
        System.getProperties().put("http.proxyUser", proxyUser);
        System.getProperties().put("http.proxyPassword", proxyPassword);
    }

}
