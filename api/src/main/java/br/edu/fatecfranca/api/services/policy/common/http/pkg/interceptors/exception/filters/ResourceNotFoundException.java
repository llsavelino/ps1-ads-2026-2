package br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.exception.filters;

public class ResourceNotFoundException extends RuntimeException {

    private final String code;

    public ResourceNotFoundException(
            String code,
            String message
    ) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}