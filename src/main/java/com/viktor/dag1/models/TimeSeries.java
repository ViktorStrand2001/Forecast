package com.viktor.dag1.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.List;

public class TimeSeries {
    @JsonProperty("validTime")
    private Date validTime;
    @JsonProperty("parameters")
    private List<Parameter> parameters;

    @JsonProperty("validTime")
    public Date getValidTime() {
        return validTime;
    }

    @JsonProperty("validTime")
    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    @JsonProperty("parameters")
    public List<Parameter> getParameters() {
        return parameters;
    }

    @JsonProperty("parameters")
    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }
}
