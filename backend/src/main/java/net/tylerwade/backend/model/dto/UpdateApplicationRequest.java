package net.tylerwade.backend.model.dto;

public class UpdateApplicationRequest {
    private String name;

    public UpdateApplicationRequest() {}

    public UpdateApplicationRequest(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
