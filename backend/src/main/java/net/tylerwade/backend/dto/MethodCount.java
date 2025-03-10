package net.tylerwade.backend.dto;

public class MethodCount {

    private String method;
    private Long count;

    public MethodCount(String method, Long count) {
        this.method = method;
        this.count = count;
    }

    public MethodCount() {
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
