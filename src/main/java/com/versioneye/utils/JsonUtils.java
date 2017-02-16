package com.versioneye.utils;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.eclipse.aether.artifact.Artifact;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.*;

/**
 * Methods to deal with JSON.
 */
public class JsonUtils {

    public static ByteArrayOutputStream dependenciesToJson(MavenProject project, List<Dependency> dependencies, List<Plugin> plugins, String nameStrategy) throws Exception {
        List<Map<String, Object>> dependencyHashes = new ArrayList<Map<String, Object>>();
        if ((dependencies != null && !dependencies.isEmpty())
                || (plugins != null && !plugins.isEmpty())) {
            dependencyHashes = getDependencyHashes(dependencies, plugins);
        }
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        toJson(outstream, getJsonPom(project, dependencyHashes, nameStrategy));
        return outstream;
    }

    public ByteArrayOutputStream artifactsToJson(List<Artifact> directDependencies) throws Exception {
        List<Map<String, Object>> hashes = getHashes(directDependencies);
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        toJson(outstream, hashes);
        return outstream;
    }

    public void dependenciesToJsonFile(String name, Map<String, Object> directDependencies, String file) throws Exception {
        File targetFile = getTargetFile(file);
        toJson(new FileOutputStream(targetFile), directDependencies);
    }

    public void dependenciesToJsonFile(MavenProject project, List<Artifact> directDependencies, String file, String nameStrategy) throws Exception {
        List<Map<String, Object>> dependencyHashes = getHashes(directDependencies);
        File targetFile = getTargetFile(file);
        toJson(new FileOutputStream(targetFile), getJsonPom(project, dependencyHashes, nameStrategy));
    }

    public static void toJson(OutputStream output, Object input) throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
         mapper.writeValue(output, input);
    }

    public List<Map<String, Object>> getHashes(List<Artifact> directDependencies){
        List<Map<String, Object>> hashes = (List<Map<String, Object>>) new Vector<Map<String, Object>>(directDependencies.size());
        hashes.addAll( generateHashForJsonOutput( directDependencies));
        return hashes;
    }

    public static List<Map<String, Object>> getDependencyHashes(List<Dependency> directDependencies, List<Plugin> plugins){
        List<Map<String, Object>> hashes = (List<Map<String, Object>>) new Vector<Map<String, Object>>();
        if (directDependencies != null && directDependencies.size() > 0){
            hashes.addAll( generateHashFromDependencyList( directDependencies));
        }
        if (plugins != null && plugins.size() > 0){
            hashes.addAll( generateHashFromPluginList(plugins));
        }
        return hashes;
    }

    public static List<Map<String, Object>> generateHashForJsonOutput(List<Artifact> input) {
         List<Map<String, Object>> output = new Vector<Map<String, Object>>(input.size());
         for (Artifact artifact : input) {
             HashMap<String, Object> hash = new HashMap<String, Object>(2);
             hash.put("version", artifact.getVersion());
             hash.put("name", artifact.getGroupId() + ":" + artifact.getArtifactId());
             output.add(hash);
         }
         return output;
    }

    public static List<Map<String, Object>> generateHashFromDependencyList(List<Dependency> input) {
        if (input == null || input.isEmpty()){
            return null;
        }
        List<Map<String, Object>> output = new Vector<Map<String, Object>>(input.size());
        for (Dependency dependency : input) {
            HashMap<String, Object> hash = new HashMap<String, Object>(2);
            hash.put("version", dependency.getVersion());
            hash.put("name", dependency.getGroupId() + ":" + dependency.getArtifactId());
            hash.put("scope", dependency.getScope() );
            output.add(hash);
        }
        return output;
    }

    public static List<Map<String, Object>> generateHashFromPluginList(List<Plugin> input) {
        if (input == null || input.isEmpty()){
            return null;
        }
        List<Map<String, Object>> output = new Vector<Map<String, Object>>(input.size());
        for (Plugin plugin : input) {
            HashMap<String, Object> hash = new HashMap<String, Object>(2);
            hash.put("version", plugin.getVersion());
            hash.put("name", plugin.getGroupId() + ":" + plugin.getArtifactId());
            hash.put("scope", "plugin" );
            output.add(hash);
        }
        return output;
    }

    public static Map<String, Object> getJsonPom(MavenProject project, List<Map<String, Object>> dependencyHashes, String nameStrategy){
        Map<String, Object> pom = new HashMap<String, Object>();
        pom.put("name", getNameFor(project, nameStrategy));
        pom.put("group_id", project.getGroupId());
        pom.put("artifact_id", project.getArtifactId());
        pom.put("version", project.getVersion());
        pom.put("language", "Java");
        pom.put("prod_type", "Maven2");
        pom.put("licenses", project.getLicenses());
        pom.put("dependencies", dependencyHashes);
        return pom;
    }

    private static String getNameFor(MavenProject project, String nameStrategy){
        String name = "project";
        if (nameStrategy == null || nameStrategy.isEmpty()){
            nameStrategy = "name";
        }
        if (nameStrategy.equals("name")){
            name = project.getName();
            if (name == null || name.isEmpty()){
                name = project.getArtifactId();
            }
        } else if (nameStrategy.equals("artifact_id")){
            name = project.getArtifactId();
        } else if (nameStrategy.equals("GA")){
            name = project.getGroupId() + "/" + project.getArtifactId();
        }
        return name;
    }

    private File getTargetFile(String file){
        File targetFile = new File(file);
        File parent = targetFile.getParentFile();
        if (!parent.exists()){
            parent.mkdirs();
        }
        return targetFile;
    }

}
