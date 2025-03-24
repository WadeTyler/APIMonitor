package net.tylerwade.backend.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class APIResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;

    public APIResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // Static factory methods
    public static <T> APIResponse<T> success(T data) {
        return new APIResponse<>(true, null, data);
    }

    public static <T> APIResponse<T> error(String message) {
        return new APIResponse<>(false, message, null);
    }

    public static <T> APIResponse<T> success(T data, String message) {
        return new APIResponse<>(true, message, data);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

}
