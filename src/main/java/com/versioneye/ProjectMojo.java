package com.versioneye;

import com.versioneye.dependency.DependencyResolver;
import com.versioneye.dto.ProjectDependency;
import com.versioneye.dto.ProjectJsonResponse;
import com.versioneye.utils.HttpUtils;
import com.versioneye.utils.JsonUtils;
import com.versioneye.utils.PropertiesUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.util.*;

/**
 * Methods required to deal with projects resource
 */
public class ProjectMojo extends SuperMojo {

    protected ByteArrayOutputStream getTransitiveDependenciesJsonStream(String nameStrategy) throws Exception {

        DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);

        Set<Artifact> directArtifacts = dependencyResolver.getDirectDependencies();

        Set<Artifact> transitiveArtifacts = dependencyResolver.getTransitiveDependencies();

        Set<Artifact> artifacts = new HashSet<Artifact>();
        artifacts.addAll(directArtifacts);
        artifacts.addAll(transitiveArtifacts);

        List<Dependency> dependencies = new ArrayList<Dependency>();
        for (Artifact artifact : artifacts) {
            Dependency dep = new Dependency();
            dep.setGroupId(artifact.getGroupId());
            dep.setArtifactId(artifact.getArtifactId());
            dep.setVersion(artifact.getVersion());
            if (directArtifacts.contains(artifact)) {
                dep.setScope("direct");
            } else {
                dep.setScope("transitive");
            }
            dependencies.add(dep);
        }
        return JsonUtils.dependenciesToJson(project, dependencies, null, nameStrategy);
    }

    protected ByteArrayOutputStream getDirectDependenciesJsonStream(String nameStrategy) throws Exception {
        /*List<Plugin> plugins = new ArrayList<Plugin>();
        if (trackPlugins){
            plugins = getPluginsFromXml();
        }*/

        DependencyResolver dependencyResolver = new DependencyResolver(project, dependencyGraphBuilder, excludeScopes);

        Set<Artifact> directArtifacts = dependencyResolver.getDirectDependencies();

        List<Dependency> dependencies = new ArrayList<Dependency>();
        for (Artifact artifact : directArtifacts) {
            Dependency dep = new Dependency();
            dep.setGroupId(artifact.getGroupId());
            dep.setArtifactId(artifact.getArtifactId());
            dep.setVersion(artifact.getVersion());
            dep.setScope("direct");
            dependencies.add(dep);
        }

        return JsonUtils.dependenciesToJson(project, dependencies, null, nameStrategy);
    }

    protected Map<String, Object> getDirectDependenciesJsonMap(String nameStrategy) throws Exception {
        List<Dependency> dependencies = project.getDependencies();
        if (dependencies == null || dependencies.isEmpty()){
            return null;
        } else {
            iterateThrough(dependencies);
        }
        List<Map<String, Object>> dependencyHashes = JsonUtils.getDependencyHashes(dependencies, project.getPluginManagement().getPlugins());
        return JsonUtils.getJsonPom(project, dependencyHashes, nameStrategy);
    }

    protected void prettyPrint0End() throws Exception {
        getLog().info(".");
        getLog().info("There are no dependencies in this project! - " + project.getId() );
        getLog().info(".");
    }

    protected void prettyPrint(ProjectJsonResponse response) throws Exception {
        getLog().info(".");
        getLog().info("Project name: " + response.getName());
        getLog().info("Project id: "   + response.getId());
        getLog().info("Dependencies: " + response.getDep_number());
        getLog().info("Outdated: "     + response.getOut_number());
        for (ProjectDependency dependency : response.getDependencies() ){
            if (dependency.getOutdated() == false){
                continue;
            }
            getLog().info(" - " + dependency.getProd_key() + ":" + dependency.getVersion_requested() + " -> " + dependency.getVersion_current());
        }
        getLog().info("");
        String projectID = (String) mavenSession.getTopLevelProject().getContextValue("veye_project_id");
        getLog().info("You can find your updated project here: " + fetchBaseUrl() + "/user/projects/" + projectID);
        getLog().info("");
    }

    protected ProjectJsonResponse updateExistingProject(String resource, String projectId, ByteArrayOutputStream outStream) throws Exception {
        String apiKey = fetchApiKey();
        String url = fetchBaseUrl() + apiPath + resource + "/" + projectId + "?api_key=" + apiKey;
        Reader reader = HttpUtils.post(url, outStream.toByteArray(), "project_file", null, null, null, null);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, ProjectJsonResponse.class );
    }


    protected ProjectJsonResponse createNewProject(String resource, ByteArrayOutputStream outStream) throws Exception {
        String apiKey = fetchApiKey();
        String url = fetchBaseUrl() + apiPath + resource + apiKey;
        Reader reader = HttpUtils.post(url, outStream.toByteArray(), "upload", visibility, name, organisation, team);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(reader, ProjectJsonResponse.class );
    }

    protected void merge(String childId) {
        if (mergeAfterCreate == false) {
            return ;
        }
        try {
            if (mavenSession.getTopLevelProject().getId().equals(mavenSession.getCurrentProject().getId())){
                return ;
            }

            String parentProjectId = (String) mavenSession.getTopLevelProject().getContextValue("veye_project_id");
            getLog().debug("parentProjectId: " + parentProjectId);
            String url = fetchBaseUrl() + apiPath + "/projects/" + parentProjectId + "/merge/" + childId + "?api_key=" + fetchApiKey();

            String response = HttpUtils.get(url);
            getLog().debug("merge response: " + response);
        } catch (Exception ex) {
            getLog().error(ex);
        }
    }

    protected void writeProperties(ProjectJsonResponse response) throws Exception {
        Properties properties = fetchProjectProperties();
        if (response.getId() != null) {
            properties.setProperty("project_id", response.getId());
        }
        PropertiesUtils utils = new PropertiesUtils();
        utils.writeProperties(properties, getPropertiesPath());
    }

    private void iterateThrough(List<Dependency> dependencies){
        for(Dependency dep: dependencies){
            getLog().info(" - dependency: " + dep.getGroupId() + "/" + dep.getArtifactId() + " " + dep.getVersion());
        }
    }

 /*   private List<Plugin> getPluginsFromXml(){
        List<Plugin> plugins = new ArrayList<Plugin>();
        try {
            File pom = project.getModel().getPomFile();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(pom);
            XPathFactory xPathfactory = XPathFactory.newInstance();
            XPath xpath = xPathfactory.newXPath();
            XPathExpression expr = xpath.compile("//plugins/plugin");

            NodeList nl = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            for (int i = 0 ; i < nl.getLength() ; i++){
                Node node = nl.item(i);
                Plugin plugin = new Plugin();
                fillPlugin(node, plugin);
                if (plugin.getGroupId() != null && plugin.getArtifactId() != null){
                    plugins.add(plugin);
                }
            }
        } catch (Exception exc){
            getLog().error(exc);
        }
        return plugins;
    }*/
/*
    private void fillPlugin(Node node, Plugin plugin){
        for (int xi = 0 ; xi < node.getChildNodes().getLength() ; xi++ ){
            Node child = node.getChildNodes().item(xi);
            if (child == null){
                return ;
            }
            if (child.getNodeName().equals("groupId")){
                plugin.setGroupId(child.getTextContent().trim());
            }
            if (child.getNodeName().equals("artifactId")){
                plugin.setArtifactId(child.getTextContent().trim());
            }
            if (child.getNodeName().equals("version")){
                String version = parseVersionString( child.getTextContent().trim() );
                plugin.setVersion(version);
            }
        }
    }*/
/*

    private String parseVersionString(String version){
        if (version.startsWith("${")){
            String verValue = version.replaceAll("\\$\\{", "").replaceAll("\\}", "");
            version = (String) project.getProperties().get(verValue);
        }
        return version;
    }
*/

}
