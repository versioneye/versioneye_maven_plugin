package com.versioneye.utils;


import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

public class HttpUtilsTest {

    @Test(expectedExceptions = Exception.class)
    public void postTestData() throws Exception{
        List<Map<String, Object>> input = new Vector<Map<String, Object>>(2);
        HashMap<String, Object> hash = new HashMap<String, Object>(2);
        hash.put("name", "junit");
        hash.put("version", "4.0");
        input.add(hash);

        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.writeValue(outstream, input);

        String url = "http://localhost:3000/api/v2/projects/1_54d11ffa6c13297974000002?api_key=f511fb2";
        HttpUtils.post(url, outstream.toByteArray(), "project_file", null, null, null, null);
    }

    @Test
    public void myProjectTest() throws Exception {

        String url = "https://www.versioneye.com/api/v2/projects/570d35bffcd19a0045440b35?api_key=1d207d70adb7021e7bc9";
    }

}
