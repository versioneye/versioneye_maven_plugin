package com.versioneye;

import com.versioneye.utils.PropertiesUtils;
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

    if (apiKey == null){
      getLog().error("API Key can not be found!");
      throw new Exception("API Key can not be found!");
    }

    return apiKey;
  }


  protected String fetchBaseUrl() throws Exception {
    if (baseUrl != null && !baseUrl.isEmpty() )
      return baseUrl;

    baseUrl = System.getenv().get("VERSIONEYE_BASE_URL");

    String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
    String key = getPropertyFromPath(propertiesPath, "base_url");
    if (key != null && !key.isEmpty()){
      baseUrl = key;
    }

    propertiesPath = projectDirectory + "/src/qa/resources/" + propertiesFile;
    key = getPropertyFromPath(propertiesPath, "base_url");
    if (key != null && !key.isEmpty()){
      baseUrl = key;
    }

    propertiesPath = projectDirectory + "/src/main/resources/" + propertiesFile;
    key = getPropertyFromPath(propertiesPath, "base_url");
    if (key != null && !key.isEmpty()){
      baseUrl = key;
    }

    return baseUrl;
  }


  protected String fetchProjectId() throws Exception {
    if (projectId != null && !projectId.isEmpty() )
      return projectId;

    if (propertiesPath != null && !propertiesPath.isEmpty()){
      String project_id = getPropertyFromPath(propertiesPath, "project_id");
      if (project_id != null && !project_id.isEmpty()){
        projectId = project_id;
      }
    }

    String pPath1 = projectDirectory + "/src/qa/resources/" + propertiesFile;
    String project_id = getPropertyFromPath(pPath1, "project_id");
    if (project_id != null && !project_id.isEmpty()){
      projectId = project_id;
      propertiesPath = pPath1;
    }

    String pPath2 = projectDirectory + "/src/main/resources/" + propertiesFile;
    project_id = getPropertyFromPath(pPath2, "project_id");
    if (project_id != null && !project_id.isEmpty()){
      projectId = project_id;
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
      proxyHost = fetchProxyHost();
      proxyPort = fetchProxyPort();
      proxyUser = fetchProxyUser();
      proxyPassword = fetchProxyPassword();

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
      if (emptyProxyUser && emptyProxyPass ) {
        return ;
      }

      System.setProperty("http.proxyUser", proxyUser);
      System.setProperty("http.proxyPassword", proxyPassword);
      System.setProperty("https.proxyUser", proxyUser);
      System.setProperty("https.proxyPassword", proxyPassword);
    } catch (Exception ex) {
      ex.printStackTrace();
    }
  }


  private String fetchProxyHost(){
    if (proxyHost != null && !proxyHost.isEmpty()){
      return proxyHost;
    }
    try{
      String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
      String host = getPropertyFromPath(propertiesPath, "proxyHost");
      if (host != null && !host.isEmpty()){
        proxyHost = host;
        return proxyHost;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    String host = System.getenv().get("VERSIONEYE_PROXY_HOST");
    if (host != null && !host.isEmpty()){
      proxyHost = host;
      return proxyHost;
    }
    return null;
  }

  private String fetchProxyPort(){
    if (proxyPort != null && !proxyPort.isEmpty()){
      return proxyPort;
    }
    try{
      String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
      String port = getPropertyFromPath(propertiesPath, "proxyPort");
      if (port != null && !port.isEmpty()){
        proxyPort = port;
        return proxyPort;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    String port = System.getenv().get("VERSIONEYE_PROXY_PORT");
    if (port != null && !port.isEmpty()){
      proxyPort = port;
      return proxyPort;
    }
    return null;
  }

  private String fetchProxyUser(){
    if (proxyUser != null && !proxyUser.isEmpty()){
      return proxyUser;
    }
    try{
      String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
      String user = getPropertyFromPath(propertiesPath, "proxyUser");
      if (user != null && !user.isEmpty()){
        proxyUser = user;
        return proxyUser;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    String user = System.getenv().get("VERSIONEYE_PROXY_USER");
    if (user != null && !user.isEmpty()){
      proxyUser = user;
      return proxyUser;
    }
    return null;
  }

  private String fetchProxyPassword(){
    if (proxyPassword != null && !proxyPassword.isEmpty()){
      return proxyPassword;
    }
    try{
      String propertiesPath = homeDirectory + "/.m2/" + propertiesFile;
      String pass = getPropertyFromPath(propertiesPath, "proxyPassword");
      if (pass != null && !pass.isEmpty()){
        proxyPassword = pass;
        return proxyPassword;
      }
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    String password = System.getenv().get("VERSIONEYE_PROXY_PASSWORD");
    if (password != null && !password.isEmpty()){
      proxyPassword = password;
      return proxyPassword;
    }
    return null;
  }


  private String getPropertyFromPath(String propertiesPath, String propertiesKey ) throws Exception {
    File file = new File(propertiesPath);
    if (file.exists()){
      PropertiesUtils propertiesUtils = new PropertiesUtils();
      Properties homeProperties = propertiesUtils.readProperties(propertiesPath);
      return homeProperties.getProperty(propertiesKey);
    } else {
      getLog().debug("File " + propertiesPath + " does not exist");
    }
    return null;
  }

}
