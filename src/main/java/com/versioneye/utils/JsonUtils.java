package com.versioneye.utils;

import org.apache.commons.lang3.StringUtils;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Methods to deal with JSON.
 */
@SuppressWarnings("WeakerAccess")
public class JsonUtils {

    private static final String VERSION = "version";
    private static final String NAME = "name";
    private static final String SCOPE = "scope";

    public ByteArrayOutputStream dependenciesToJson(MavenProject project, List<Dependency> dependencies, List<Plugin> plugins, String nameStrategy) throws Exception {
        List<Map<String, Object>> dependencyHashes = new ArrayList<>();
        if ((CollectionUtils.collectionNotEmpty(dependencies)) || (CollectionUtils.collectionNotEmpty(plugins))) {
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

    @SuppressWarnings("UnusedParameters")
    public void dependenciesToJsonFile(String name, Map<String, Object> directDependencies, String file) throws Exception {
        File targetFile = getTargetFile(file);
        toJson(new FileOutputStream(targetFile), directDependencies);
    }

    @SuppressWarnings("unused")
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
        List<Map<String, Object>> hashes = new ArrayList<>(directDependencies.size());
        hashes.addAll( generateHashForJsonOutput( directDependencies));
        return hashes;
    }

    public List<Map<String, Object>> getDependencyHashes(List<Dependency> directDependencies, List<Plugin> plugins){

        List<Map<String, Object>> hashes = new ArrayList<>();

        if (CollectionUtils.collectionNotEmpty(directDependencies)) {
            hashes.addAll( generateHashFromDependencyList( directDependencies));
        }

        if (CollectionUtils.collectionNotEmpty(plugins)) {
            hashes.addAll( generateHashFromPluginList(plugins));
        }

        return hashes;
    }

    public static List<Map<String, Object>> generateHashForJsonOutput(List<Artifact> input) {
         List<Map<String, Object>> output = new ArrayList<>(input.size());
         for (Artifact artifact : input) {
             Map<String, Object> map = new HashMap<>(2);
             map.put(VERSION, artifact.getVersion());
             map.put(NAME, artifact.getGroupId() + ":" + artifact.getArtifactId());
             output.add(map);
         }
         return output;
    }

    public static List<Map<String, Object>> generateHashFromDependencyList(List<Dependency> input) {
        if (CollectionUtils.collectionIsEmpty(input)){
            return Collections.emptyList();
        }

        List<Map<String, Object>> output = new ArrayList<>(input.size());
        for (Dependency dependency : input) {
            Map<String, Object> map = new HashMap<>(2);
            map.put(VERSION, dependency.getVersion());
            map.put(NAME, dependency.getGroupId() + ":" + dependency.getArtifactId());
            map.put(SCOPE, dependency.getScope() );
            output.add(map);
        }

        return output;
    }

    public static List<Map<String, Object>> generateHashFromPluginList(List<Plugin> input) {
        if (CollectionUtils.collectionIsEmpty(input)){
            return Collections.emptyList();
        }

        List<Map<String, Object>> output = new ArrayList<>(input.size());
        for (Plugin plugin : input) {
            Map<String, Object> map = new HashMap<>(2);
            map.put(VERSION, plugin.getVersion());
            map.put(NAME, plugin.getGroupId() + ":" + plugin.getArtifactId());
            map.put(SCOPE, "plugin" );
            output.add(map);
        }

        return output;
    }

    public Map<String, Object> getJsonPom(MavenProject project, List<Map<String, Object>> dependencyHashes, String nameStrategy){
        Map<String, Object> pom = new HashMap<>();
        pom.put(NAME, getNameFor(project, nameStrategy));
        pom.put("group_id", project.getGroupId());
        pom.put("artifact_id", project.getArtifactId());
        pom.put(VERSION, project.getVersion());
        pom.put("language", "Java");
        pom.put("prod_type", "Maven2");
        pom.put("licenses", project.getLicenses());
        pom.put("dependencies", dependencyHashes);
        return pom;
    }

    private String getNameFor(MavenProject project, String nameStrategy){
        String name = "project";

        String localNameStrategy = StringUtils.isBlank(nameStrategy) ? NAME : nameStrategy;

        switch (localNameStrategy)
        {
            case NAME:
              name = project.getName();
              if (StringUtils.isBlank(name))
                  name = project.getArtifactId();
              break;
            case "artifact_id":
                  name = project.getArtifactId();
                  break;
            case "GA":
                name = project.getGroupId() + "/" + project.getArtifactId();
                break;
            default:
                break;
        }

        return name;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private File getTargetFile(String file){
        File targetFile = new File(file);
        File parent = targetFile.getParentFile();
        if (!parent.exists()){
            parent.mkdirs();
        }
        return targetFile;
    }
}
