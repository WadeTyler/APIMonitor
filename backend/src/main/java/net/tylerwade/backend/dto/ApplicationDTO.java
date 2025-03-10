package net.tylerwade.backend.dto;

public class ApplicationDTO {

    private String id;
    private String publicToken;
    private String name;
    private String userId;

    private Long totalAPICalls;
    private Long totalUniqueRemoteAddr;
    private String[] uniquePaths;
    private MethodCount[] methodCounts;

    public ApplicationDTO(String id, String publicToken, String name, String userId, Long totalAPICalls, Long totalUniqueRemoteAddr, String[] uniquePaths, MethodCount[] methodCounts) {
        this.id = id;
        this.publicToken = publicToken;
        this.name = name;
        this.userId = userId;
        this.totalAPICalls = totalAPICalls;
        this.totalUniqueRemoteAddr = totalUniqueRemoteAddr;
        this.uniquePaths = uniquePaths;
        this.methodCounts = methodCounts;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPublicToken() {
        return publicToken;
    }

    public void setPublicToken(String publicToken) {
        this.publicToken = publicToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getTotalAPICalls() {
        return totalAPICalls;
    }

    public void setTotalAPICalls(Long totalAPICalls) {
        this.totalAPICalls = totalAPICalls;
    }

    public Long getTotalUniqueRemoteAddr() {
        return totalUniqueRemoteAddr;
    }

    public void setTotalUniqueRemoteAddr(Long totalUniqueRemoteAddr) {
        this.totalUniqueRemoteAddr = totalUniqueRemoteAddr;
    }

    public String[] getUniquePaths() {
        return uniquePaths;
    }

    public void setUniquePaths(String[] uniquePaths) {
        this.uniquePaths = uniquePaths;
    }

    public MethodCount[] getMethodCounts() {
        return methodCounts;
    }

    public void setMethodCounts(MethodCount[] methodCounts) {
        this.methodCounts = methodCounts;
    }
}
