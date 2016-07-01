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

    @Parameter( property = "nameStrategy" )
    protected String nameStrategy = "name";

    @Parameter( property = "trackPlugins" )
    protected Boolean trackPlugins = Boolean.TRUE;

    @Parameter( property = "licenseCheckBreakByUnknown" )
    protected Boolean licenseCheckBreakByUnknown = Boolean.FALSE;

    @Parameter( property = "skipScopes" )
    protected String skipScopes = null;

    @Parameter( property = "organisation" )
    protected String organisation = null;

    @Parameter( property = "team" )
    protected String team = null;

    @Parameter( property = "visibility" )
    protected String visibility = null;

    @Parameter( property = "name" )
    protected String name = null;

    @Parameter( property = "ignoreDependencyManagement" )
    protected boolean ignoreDependencyManagement = false;

    @Parameter( property = "transitiveDependencies" )
    protected boolean transitiveDependencies = false;

    protected Properties properties = null;     // Properties in src/main/resources
    protected Properties homeProperties = null; // Properties in ~/.m2/

    public void execute() throws MojoExecutionException, MojoFailureException {  }

    protected String fetchApiKey() throws Exception {
        if (apiKey != null && !apiKey.isEmpty() )
            return apiKey;

        apiKey = System.getenv().get("VERSIONEYE_API_KEY");

        String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
        String key = getPropertyFromPath(propertiesPath, "api_key");
        if (key != null && !key.isEmpty()){
            apiKey = key;
        }

        propertiesPath = projectDirectory + "/src/qa/resources/" + propertiesFile;
        key = getPropertyFromPath(propertiesPath, "api_key");
        if (key != null && !key.isEmpty()){
            apiKey = key;
        }

        propertiesPath = projectDirectory + "/src/main/resources/" + propertiesFile;
        key = getPropertyFromPath(propertiesPath, "api_key");
        if (key != null && !key.isEmpty()){
            apiKey = key;
        }

        return apiKey;
    }

    protected String fetchProjectId() throws Exception {
        if (projectId != null && !projectId.isEmpty() )
            return projectId;

        propertiesPath = projectDirectory + "/src/qa/resources/" + propertiesFile;
        String project_id = getPropertyFromPath(propertiesPath, "project_id");
        if (project_id != null && !project_id.isEmpty()){
            projectId = project_id;
        }

        propertiesPath = projectDirectory + "/src/main/resources/" + propertiesFile;
        project_id = getPropertyFromPath(propertiesPath, "project_id");
        if (project_id != null && !project_id.isEmpty()){
            projectId = project_id;
        }

        if (projectId == null || projectId.isEmpty()){
            String msg = "versioneye.properties found but without project_id! Read the instructions at https://github.com/versioneye/versioneye_maven_plugin";
            getLog().error(msg);
            throw new MojoExecutionException(msg);
        }

        return projectId;
    }

    protected Properties fetchProjectProperties() throws Exception {
        if (properties != null)
            return properties;
        String propertiesPath = getPropertiesPath();
        System.out.println("propertiesPath: " + propertiesPath);
        File file = new File(propertiesPath);
        if (!file.exists())
            createPropertiesFile(file);
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        properties = propertiesUtils.readProperties(propertiesPath);
        return properties;
    }

    protected String getPropertiesPath() throws Exception {
        if (this.propertiesPath != null && !this.propertiesPath.isEmpty())
            return this.propertiesPath;
        String propertiesPath = projectDirectory + "/src/qa/resources/" + propertiesFile;
        File file = new File(propertiesPath);
        if (!file.exists()) {
            propertiesPath = projectDirectory + "/src/main/resources/" + propertiesFile;
            new File(propertiesPath);
        }
        this.propertiesPath = propertiesPath;
        return propertiesPath;
    }

    private void createPropertiesFile(File file) throws IOException {
        File parent = file.getParentFile();
        if (!parent.exists()){
            File grandpa = parent.getParentFile();
            if (!grandpa.exists()){
                grandpa.mkdirs();
            }
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
        try{
            if (proxyHost == null || proxyHost.isEmpty()){
                String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
                String host = getPropertyFromPath(propertiesPath, "proxyHost");
                if (host != null && !host.isEmpty()){
                    proxyHost = host;
                }
            }

            if (proxyPort == null || proxyPort.isEmpty() ){
                String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
                String port = getPropertyFromPath(propertiesPath, "proxyPort");
                if (port != null && !port.isEmpty()){
                    proxyPort = port;
                }
            }

            if (proxyUser == null || proxyUser.isEmpty()){
                String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
                String user = getPropertyFromPath(propertiesPath, "proxyUser");
                if (user != null && !user.isEmpty()){
                    proxyUser = user;
                }
            }

            if (proxyPassword == null || proxyPassword.isEmpty()){
                String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
                String password = getPropertyFromPath(propertiesPath, "proxyPassword");
                if (password != null && !password.isEmpty()){
                    proxyPassword = password;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

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

    private String getPropertyFromPath(String propertiesPath, String propertiesKey ) throws Exception {
        File file = new File(propertiesPath);
        if (file.exists()){
            PropertiesUtils propertiesUtils = new PropertiesUtils();
            Properties homeProperties = propertiesUtils.readProperties(propertiesPath);
            return homeProperties.getProperty(propertiesKey);
        }
        return null;
    }

}
