package versioneye;

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

    public ByteArrayOutputStream dependenciesToJson(List<Artifact> directDependencies) throws Exception {
        List<Map<String, Object>> hashes = getHashes(directDependencies);
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        toJson(outstream, hashes);
        return outstream;
    }

    public void dependenciesToJsonFile(List<Artifact> directDependencies, String file) throws Exception {
        List<Map<String, Object>> hashes = getHashes(directDependencies);
        toJson(new FileOutputStream(new File(file)), hashes);
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

}
