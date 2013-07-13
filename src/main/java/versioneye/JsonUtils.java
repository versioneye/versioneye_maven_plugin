package versioneye;

import org.apache.maven.plugin.MojoExecutionException;
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
 * Created with IntelliJ IDEA.
 * User: robertreiz
 * Date: 7/13/13
 * Time: 2:25 PM
 */
public class JsonUtils {

    public void dependenciesToJson(List<Artifact> directDependencies) throws Exception {
        List<Map<String, Object>> hashes = getHashes(directDependencies);
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        toJson(outstream, hashes);
    }

    public void dependenciesToJsonFile(List<Artifact> directDependencies, String file) throws Exception {
        List<Map<String, Object>> hashes = getHashes(directDependencies);
        FileOutputStream outstream = new FileOutputStream(new File(file));
        toJson(outstream, hashes);
    }

    public static void toJson(OutputStream output, Object input) throws Exception {
         try {
             ObjectMapper mapper = new ObjectMapper();
             mapper.writeValue(output, input);
         }
         catch (java.lang.Exception e) {
             throw new MojoExecutionException("Fatal error while attempting to construct JSON document", e);
         }
    }

    public List<Map<String, Object>> getHashes(List<Artifact> directDependencies){
        List<Map<String, Object>> hashes = (List<Map<String, Object>>) new Vector<Map<String, Object>>(directDependencies.size());
        hashes.addAll( generateHashForJsonOutput( directDependencies   , true ));
        return hashes;
    }

    public static List<Map<String, Object>> generateHashForJsonOutput(List<Artifact> input, boolean direct) {
         List<Map<String, Object>> output = new Vector<Map<String, Object>>(input.size());
         for (Artifact a : input) {
             HashMap<String, Object> h = new HashMap<String, Object>(3);
             h.put("version", a.getVersion());
             h.put("name", a.getGroupId() + ":" + a.getArtifactId());
             output.add(h);
         }
         return output;
    }

}
