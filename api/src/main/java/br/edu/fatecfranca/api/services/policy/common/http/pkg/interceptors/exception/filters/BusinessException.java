package br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.exception.filters;

public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(
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