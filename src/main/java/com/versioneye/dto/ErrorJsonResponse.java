package com.versioneye.dto;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorJsonResponse {

    private String error;

    public String getError() {
        return error;
    }

    @SuppressWarnings("unused")
    public void setError(String error) {
        this.error = error;
    }
}
