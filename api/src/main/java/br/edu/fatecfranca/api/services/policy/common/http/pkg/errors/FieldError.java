package br.edu.fatecfranca.api.services.policy.common.http.pkg.errors;

/**
 * Representa um erro relacionado a um campo específico
 * de uma requisição.
 */
public final class FieldError {

    private final String field;
    private final String code;
    private final String message;

    public FieldError(
            String field,
            String code,
            String message
    ) {
        this.field = field;
        this.code = code;
        this.message = message;
    }

    public String getField() {
        return field;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}