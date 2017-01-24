package com.versioneye;

import org.apache.commons.lang3.StringUtils;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import com.versioneye.utils.PropertiesUtils;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Properties;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * The Mother of all Mojos!
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public abstract class SuperMojo extends AbstractMojo
{
    protected static final String VERSIONEYE_PROPERTIES_FILE = "versioneye.properties";
    protected static final String API_KEY = "api_key";

    protected static final String BASE_URL = "base_url";

    protected static final String PROJECT_ID = "";

    protected static final String SRC_QA_RESOURCES = "/src/qa/resources/";
    protected static final String SRC_MAIN_RESOURCES = "/src/main/resources/";

    @Component
    protected RepositorySystem system;

    @Component
    protected MavenSession mavenSession;

    @Parameter( defaultValue="${repositorySystemSession}" )
    protected RepositorySystemSession session;

    @Parameter( defaultValue="${project}" )
    protected MavenProject project;

    @Parameter( defaultValue="${reactorProjects}" )
    protected List<MavenProject> reactorProjects;

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

    private static final String M2_PATH = "/.m2X/";

    protected Properties properties = null;     // Properties in src/main/resources
    protected Properties homeProperties = null; // Properties in ~/.m2/


    public abstract void execute() throws MojoExecutionException, MojoFailureException;


    protected String fetchApiKey() throws Exception {
        if (StringUtils.isNotBlank(apiKey) )
            return apiKey;

        apiKey = System.getenv().get("VERSIONEYE_API_KEY");

        String localPropertiesPath = homeDirectory + M2_PATH + VERSIONEYE_PROPERTIES_FILE;
        String key = getPropertyFromPath(localPropertiesPath, API_KEY);
        if (StringUtils.isNotBlank(key)){
            apiKey = key;
        }

        localPropertiesPath = projectDirectory + SRC_QA_RESOURCES + VERSIONEYE_PROPERTIES_FILE;
        key = getPropertyFromPath(localPropertiesPath, API_KEY);
        if (StringUtils.isNotBlank(key)){
            apiKey = key;
        }

        localPropertiesPath = projectDirectory + SRC_MAIN_RESOURCES + VERSIONEYE_PROPERTIES_FILE;
        key = getPropertyFromPath(localPropertiesPath, API_KEY);
        if (StringUtils.isNotBlank(key)) {
            apiKey = key;
        }

        if (apiKey == null){
            getLog().error("API Key can not be found!");
            throw new Exception("API Key can not be found!");
        }

        return apiKey;
    }


    protected String fetchBaseUrl() throws Exception {
        if (StringUtils.isNotBlank(baseUrl))
            return baseUrl;

        baseUrl = System.getenv().get("VERSIONEYE_BASE_URL");

        String localPropertiesPath = homeDirectory + M2_PATH + VERSIONEYE_PROPERTIES_FILE;
        String key = getPropertyFromPath(localPropertiesPath, BASE_URL);
        if (StringUtils.isNotBlank(key)) {
            baseUrl = key;
        }

        localPropertiesPath = projectDirectory + SRC_QA_RESOURCES + VERSIONEYE_PROPERTIES_FILE;
        key = getPropertyFromPath(localPropertiesPath, BASE_URL);
        if (StringUtils.isNotBlank(key)){
            baseUrl = key;
        }

        localPropertiesPath = projectDirectory + SRC_MAIN_RESOURCES + VERSIONEYE_PROPERTIES_FILE;
        key = getPropertyFromPath(localPropertiesPath, BASE_URL);
        if (StringUtils.isNotBlank(key)){
            baseUrl = key;
        }

        return baseUrl;
    }


    protected String fetchProjectId() throws Exception {
        if (StringUtils.isNotBlank(projectId) )
            return projectId;

        String localProjectId;

        if (StringUtils.isNotBlank(propertiesPath)){
            localProjectId = getPropertyFromPath(propertiesPath, PROJECT_ID);
            if (StringUtils.isNotBlank(localProjectId)){
                projectId = localProjectId;
            }
        }

        String pPath1 = projectDirectory + SRC_QA_RESOURCES + VERSIONEYE_PROPERTIES_FILE;
        localProjectId = getPropertyFromPath(pPath1, PROJECT_ID);
        if (localProjectId != null && !localProjectId.isEmpty()){
            projectId = localProjectId;
            propertiesPath = pPath1;
        }

        String pPath2 = projectDirectory + SRC_MAIN_RESOURCES + VERSIONEYE_PROPERTIES_FILE;
        localProjectId = getPropertyFromPath(pPath2, PROJECT_ID);
        if (localProjectId != null && !localProjectId.isEmpty()){
            projectId = localProjectId;
            propertiesPath = pPath2;
        }

        if (projectId == null || projectId.isEmpty()){
            String msg = "Searched in [" + pPath1 + ", " + pPath2 + ", "+ propertiesPath +"] for project_id but could't find any.";
            getLog().error(msg);
            throw new MojoExecutionException(msg);
        }

        return projectId;
    }


    protected Properties fetchProjectProperties() throws Exception {
        if (properties != null)
            return properties;
        String localPropertiesPath = getPropertiesPath();
        System.out.println("propertiesPath: " + localPropertiesPath);
        File file = new File(localPropertiesPath);
        if (!file.exists())
            createPropertiesFile(file);
        PropertiesUtils propertiesUtils = new PropertiesUtils();
        properties = propertiesUtils.readProperties(localPropertiesPath);
        return properties;
    }

    protected String getPropertiesPath() throws Exception {
        if (this.propertiesPath != null && !this.propertiesPath.isEmpty())
            return this.propertiesPath;

        String localPropertiesPath = projectDirectory + SRC_QA_RESOURCES + VERSIONEYE_PROPERTIES_FILE;

        File file = new File(localPropertiesPath);

        if (!file.exists()) {
            localPropertiesPath = projectDirectory + SRC_MAIN_RESOURCES + VERSIONEYE_PROPERTIES_FILE;
            new File(localPropertiesPath);
        }
        this.propertiesPath = localPropertiesPath;
        return localPropertiesPath;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createPropertiesFile(File file) throws IOException
    {
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
            @Override
            public void checkClientTrusted(X509Certificate[] certs, String authType){}
            @Override
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


    protected void setProxy() {

        final String localPropertiesPath = homeDirectory + M2_PATH + VERSIONEYE_PROPERTIES_FILE;

        try{
            if (StringUtils.isBlank(proxyHost)) {
                String host = getPropertyFromPath(localPropertiesPath, "proxyHost");
                if (StringUtils.isNotBlank(host)){
                    proxyHost = host;
                }
            }

            if (StringUtils.isNotBlank(proxyPort)) {
                String port = getPropertyFromPath(localPropertiesPath, "proxyPort");
                if (StringUtils.isNotBlank(port)){
                    proxyPort = port;
                }
            }

            if (StringUtils.isNotBlank(proxyUser)) {
                String user = getPropertyFromPath(localPropertiesPath, "proxyUser");
                if (StringUtils.isNotBlank(user)){
                    proxyUser = user;
                }
            }

            if (StringUtils.isNotBlank(proxyPassword)) {
                String password = getPropertyFromPath(localPropertiesPath, "proxyPassword");
                if (StringUtils.isNotBlank(password)) {
                    proxyPassword = password;
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        boolean emptyProxyHost = StringUtils.isBlank(proxyHost);
        boolean emptyProxyPort = StringUtils.isBlank(proxyPort);

        if (emptyProxyHost && emptyProxyPort){
            return;
        }

        System.setProperty("proxySet", "true");
        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", proxyPort);

        boolean emptyProxyUser = StringUtils.isBlank(proxyUser);
        boolean emptyProxyPass = StringUtils.isBlank(proxyPassword);

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
            Properties localHomeProperties = propertiesUtils.readProperties(propertiesPath);
            return localHomeProperties.getProperty(propertiesKey);
        } else {
            getLog().debug("File " + propertiesPath + " does not exist");
        }
        return null;
    }
}
