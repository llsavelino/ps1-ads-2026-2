package br.edu.fatecfranca.api.services.policy.common.http.pkg.errors;

import java.util.Collections;
import java.util.List;

/**
 * Representa um erro de negócio, validação ou infraestrutura
 * retornado pela API.
 */
public final class ApiError {

    private final String code;
    private final String message;
    private final List<FieldError> details;

    private ApiError(
            String code,
            String message,
            List<FieldError> details
    ) {
        this.code = code;
        this.message = message;
        this.details = details == null
                ? Collections.emptyList()
                : List.copyOf(details);
    }

    /**
     * Cria um erro simples.
     */
    public static ApiError of(
            String code,
            String message
    ) {
        return new ApiError(
                code,
                message,
                Collections.emptyList()
        );
    }

    /**
     * Cria um erro de validação.
     */
    public static ApiError validation(
            String message,
            List<FieldError> details
    ) {
        return new ApiError(
                "VALIDATION_ERROR",
                message,
                details
        );
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<FieldError> getDetails() {
        return details;
    }
}