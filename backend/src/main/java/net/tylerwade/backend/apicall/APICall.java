package net.tylerwade.backend.apicall;

import jakarta.persistence.*;

import java.util.Date;

@Entity @Table(name = "api_calls")
public class APICall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String serviceId;
    private String path;
    private String method;
    private int responseStatus;
    private String remoteAddress;
    private Date timestamp = new Date();

    public APICall() {
    }

    public APICall(String serviceId, String path, String method, Integer responseStatus, String remoteAddress) {
        this.serviceId = serviceId;
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
