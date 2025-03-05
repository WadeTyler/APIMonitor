package net.tylerwade.backend.APIMonitorLib;

import java.util.Date;

public class APICall {

    // Used by the user to mark the api call under their service.
    private String serviceId;

    private String path;
    private String method;
    private int responseStatus;
    private String remoteAddress;
    private Date timestamp = new Date();

    public APICall(String path, String method, Integer responseStatus, String remoteAddress) {
        this.path = path;
        this.method = method;
        this.responseStatus = responseStatus;
        this.remoteAddress = remoteAddress;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
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

    public int getResponseStatus() {
        return responseStatus;
    }

    public void setResponseStatus(int responseStatus) {
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
