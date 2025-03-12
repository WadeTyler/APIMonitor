package net.tylerwade.backend.dto;

public class UserProfileDTO {

    private String userId;
    private String email;

    private Integer applicationCount;
    private Long totalAPIRequests;
    private Long totalUniqueRemoteAddresses;

    public UserProfileDTO(String userId, String email, Integer applicationCount, Long totalAPIRequests, Long totalUniqueRemoteAddresses) {
        this.userId = userId;
        this.email = email;
        this.applicationCount = applicationCount;
        this.totalAPIRequests = totalAPIRequests;
        this.totalUniqueRemoteAddresses = totalUniqueRemoteAddresses;
    }

    public UserProfileDTO() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
    }

    public Long getTotalAPIRequests() {
        return totalAPIRequests;
    }

    public void setTotalAPIRequests(Long totalAPIRequests) {
        this.totalAPIRequests = totalAPIRequests;
    }

    public Long getTotalUniqueRemoteAddresses() {
        return totalUniqueRemoteAddresses;
    }

    public void setTotalUniqueRemoteAddresses(Long totalUniqueRemoteAddresses) {
        this.totalUniqueRemoteAddresses = totalUniqueRemoteAddresses;
    }
}
