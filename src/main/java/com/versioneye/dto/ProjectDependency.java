package com.versioneye.dto;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectDependency {

    private String name;
    private String prod_key;
    private String group_id;
    private String artifact_id;
    private String language;
    private String version_current;
    private String version_requested;
    private Boolean outdated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Boolean getOutdated() {
        return outdated;
    }

    public void setOutdated(Boolean outdated) {
        this.outdated = outdated;
    }

    public String getProd_key() {
        return prod_key;
    }

    public void setProd_key(String prod_key) {
        this.prod_key = prod_key;
    }

    public String getGroup_id() {
        return group_id;
    }

    public void setGroup_id(String group_id) {
        this.group_id = group_id;
    }

    public String getArtifact_id() {
        return artifact_id;
    }

    public void setArtifact_id(String artifact_id) {
        this.artifact_id = artifact_id;
    }

    public String getVersion_current() {
        return version_current;
    }

    public void setVersion_current(String version_current) {
        this.version_current = version_current;
    }

    public String getVersion_requested() {
        return version_requested;
    }

    public void setVersion_requested(String version_requested) {
        this.version_requested = version_requested;
    }
}
