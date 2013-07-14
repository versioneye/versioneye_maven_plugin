package versioneye;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Created with IntelliJ IDEA.
 * User: robertreiz
 * Date: 7/14/13
 * Time: 12:21 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectJsonResponse {

    private String name;
    private String project_key;
    private String id;
    private String dep_number;
    private String out_number;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProject_key() {
        return project_key;
    }

    public void setProject_key(String project_key) {
        this.project_key = project_key;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDep_number() {
        return dep_number;
    }

    public void setDep_number(String dep_number) {
        this.dep_number = dep_number;
    }

    public String getOut_number() {
        return out_number;
    }

    public void setOut_number(String out_number) {
        this.out_number = out_number;
    }

}
