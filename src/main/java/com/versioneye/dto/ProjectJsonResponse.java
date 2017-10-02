package com.versioneye.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Java representation of the project JSON response from VersionEye API.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProjectJsonResponse {

    private String name;
    private String group_id;
    private String artifact_id;
    private String id;
    private Integer dep_number;
    private Integer out_number;
    private Integer licenses_red = 0;
    private Integer licenses_unknown = 0;
    private Integer sv_count = 0;

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

    public Integer getSv_count() {
        return sv_count;
    }

    public void setSv_count(Integer sv_count) {
        this.sv_count = sv_count;
    }

    private ProjectDependency[] dependencies;

    public Integer getLicenses_red() {
        return licenses_red;
    }

    public void setLicenses_red(Integer licenses_red) {
        this.licenses_red = licenses_red;
    }

    public Integer getLicenses_unknown() {
        return licenses_unknown;
    }

    public void setLicenses_unknown(Integer licenses_unknown) {
        this.licenses_unknown = licenses_unknown;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDep_number() {
        return dep_number;
    }

    public void setDep_number(Integer dep_number) {
        this.dep_number = dep_number;
    }

    public Integer getOut_number() {
        return out_number;
    }

    public void setOut_number(Integer out_number) {
        this.out_number = out_number;
    }

    public ProjectDependency[] getDependencies() {
        return dependencies;
    }

    public void setDependencies(ProjectDependency[] dependencies) {
        this.dependencies = dependencies;
    }
}
