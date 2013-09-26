package versioneye.utils;

import org.apache.maven.model.Dependency;
import org.codehaus.jackson.map.ObjectMapper;
import org.sonatype.aether.artifact.Artifact;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * Methods to deal with JSON.
 */
public class JsonUtils {

    public ByteArrayOutputStream dependenciesToJson(List<Dependency> dependencies) throws Exception {
        List<Map<String, Object>> hashes = getDependencyHashes(dependencies);
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        toJson(outstream, hashes);
        return outstream;
    }

    public ByteArrayOutputStream artifactsToJson(List<Artifact> directDependencies) throws Exception {
        List<Map<String, Object>> hashes = getHashes(directDependencies);
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        toJson(outstream, hashes);
        return outstream;
    }

    public void dependenciesToJsonFile(List<Artifact> directDependencies, String file) throws Exception {
        List<Map<String, Object>> hashes = getHashes(directDependencies);
        File targetFile = new File(file);
        File parent = targetFile.getParentFile();
        if (!parent.exists()){
            parent.mkdirs();
        }
        toJson(new FileOutputStream(targetFile), hashes);
    }

    public static void toJson(OutputStream output, Object input) throws Exception {
         ObjectMapper mapper = new ObjectMapper();
         mapper.writeValue(output, input);
    }

    public List<Map<String, Object>> getHashes(List<Artifact> directDependencies){
        List<Map<String, Object>> hashes = (List<Map<String, Object>>) new Vector<Map<String, Object>>(directDependencies.size());
        hashes.addAll( generateHashForJsonOutput( directDependencies));
        return hashes;
    }

    public List<Map<String, Object>> getDependencyHashes(List<Dependency> directDependencies){
        List<Map<String, Object>> hashes = (List<Map<String, Object>>) new Vector<Map<String, Object>>(directDependencies.size());
        hashes.addAll( generateHashFromDependencyList( directDependencies));
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
        List<Map<String, Object>> output = new Vector<Map<String, Object>>(input.size());
        for (Dependency dependency : input) {
            HashMap<String, Object> hash = new HashMap<String, Object>(2);
            hash.put("version", dependency.getVersion());
            hash.put("name", dependency.getGroupId() + ":" + dependency.getArtifactId());
            output.add(hash);
        }
        return output;
    }

}
