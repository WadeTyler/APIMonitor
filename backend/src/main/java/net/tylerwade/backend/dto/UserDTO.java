package net.tylerwade.backend.dto;

public class UserDTO {

    private String id;

    public UserDTO(String id) {
        this.id = id;
    }

    public UserDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
