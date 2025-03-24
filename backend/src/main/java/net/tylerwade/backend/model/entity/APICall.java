package net.tylerwade.backend.model.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity @Table(name = "api_calls")
public class APICall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String appId;
    private String path;
    private String method;
    private Integer responseStatus;
    private String remoteAddress;
    private Date timestamp = new Date();

    public APICall() {
    }

    public APICall(String appId, String path, String method, Integer responseStatus, String remoteAddress) {
        this.appId = appId;
        this.path = path;
        this.method = method;
        this.responseStatus = responseStatus;
        this.remoteAddress = remoteAddress;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String serviceId) {
        this.appId = serviceId;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(Integer responseStatus) {
        this.responseStatus = responseStatus;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    @Override
    public String toString() {
        return "APICall{" +
                "path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", responseStatus=" + responseStatus +
                ", remoteAddress='" + remoteAddress + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
