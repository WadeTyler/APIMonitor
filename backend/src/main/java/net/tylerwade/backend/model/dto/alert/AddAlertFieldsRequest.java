package net.tylerwade.backend.model.dto.alert;

public class AddAlertFieldsRequest {

    private String path;
    private String method;
    private Integer responseStatus;
    private String remoteAddress;

    public AddAlertFieldsRequest() {
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public Integer getResponseStatus() {
        return responseStatus;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }
}
