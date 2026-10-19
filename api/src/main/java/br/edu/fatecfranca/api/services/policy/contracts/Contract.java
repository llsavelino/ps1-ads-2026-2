package br.edu.fatecfranca.api.services.policy.contracts;

import java.time.Instant;

import br.edu.fatecfranca.api.services.policy.common.http.pkg.errors.ApiError;
import br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.TraceId;
import br.edu.fatecfranca.api.services.policy.common.http.pkg.metadata.Meta;
import br.edu.fatecfranca.api.services.policy.common.http.pkg.metadata.PageMeta;

/**
 * Contrato padrão de resposta da API.
 *
 * @param <T> tipo do payload retornado em data
 */
public final class Contract<T> {

    private final boolean success;
    private final int httpStatus;
    private final T data;
    private final ApiError error;
    private final Meta meta;
    private final Instant timestamp;
    private final String traceId;

    private Contract(
            boolean success,
            int httpStatus,
            T data,
            ApiError error,
            Meta meta,
            Instant timestamp,
            String traceId
    ) {
        this.success = success;
        this.httpStatus = httpStatus;
        this.data = data;
        this.error = error;
        this.meta = meta;
        this.timestamp = timestamp;
        this.traceId = traceId;
    }

    // ==========================================
    // RESPOSTAS DE SUCESSO (2xx)
    // ==========================================

    /** 200 OK - Sucesso sem metadados */
    public static <T> Contract<T> ok(T data) {
        return ok(200, data, null);
    }

    /** 200 OK - Sucesso com metadados */
    public static <T> Contract<T> ok(T data, Meta meta) {
        return ok(200, data, meta);
    }

    /** 200 OK - Resposta paginada */
    public static <T> Contract<T> okPage(T data, PageMeta page) {
        return ok(200, data, Meta.page(page));
    }

    /** 201 Created - Recurso criado com sucesso */
    public static <T> Contract<T> created(T data) {
        return ok(201, data, null);
    }

    /** 202 Accepted - Requisição aceita para processamento */
    public static <T> Contract<T> accepted(T data) {
        return ok(202, data, null);
    }

    /** 204 No Content - Sucesso sem corpo de retorno */
    public static <T> Contract<T> noContent() {
        return ok(204, null, null);
    }

    /** Sucesso com status HTTP dinâmico */
    public static <T> Contract<T> ok(int httpStatus, T data, Meta meta) {
        return new Contract<>(
                true,
                httpStatus,
                data,
                null,
                meta,
                Instant.now(),
                TraceId.current()
        );
    }

    // ==========================================
    // RESPOSTAS DE ERRO (4xx / 5xx)
    // ==========================================

    /** Erro genérico especificando o HTTP Status */
    public static <T> Contract<T> error(int httpStatus, ApiError error) {
        return new Contract<>(
                false,
                httpStatus,
                null,
                error,
                null,
                Instant.now(),
                TraceId.current()
        );
    }

    /** Erro genérico simplificado especificando o HTTP Status */
    public static <T> Contract<T> error(int httpStatus, String code, String message) {
        return error(httpStatus, ApiError.of(code, message));
    }

    /** 400 Bad Request com ApiError */
    public static <T> Contract<T> badRequest(ApiError error) {
        return error(400, error);
    }

    /** 400 Bad Request simplificado */
    public static <T> Contract<T> badRequest(String code, String message) {
        return error(400, code, message);
    }

    /** 401 Unauthorized */
    public static <T> Contract<T> unauthorized(String code, String message) {
        return error(401, code, message);
    }

    /** 403 Forbidden */
    public static <T> Contract<T> forbidden(String code, String message) {
        return error(403, code, message);
    }

    /** 404 Not Found com ApiError */
    public static <T> Contract<T> notFound(ApiError error) {
        return error(404, error);
    }

    /** 404 Not Found simplificado */
    public static <T> Contract<T> notFound(String code, String message) {
        return error(404, code, message);
    }

    /** 409 Conflict */
    public static <T> Contract<T> conflict(String code, String message) {
        return error(409, code, message);
    }

    /** 422 Unprocessable Entity (Erros de validação de regras de negócio) */
    public static <T> Contract<T> unprocessableEntity(ApiError error) {
        return error(422, error);
    }

    /** 500 Internal Server Error com ApiError */
    public static <T> Contract<T> internalServerError(ApiError error) {
        return error(500, error);
    }

    /** 500 Internal Server Error simplificado */
    public static <T> Contract<T> internalServerError(String code, String message) {
        return error(500, code, message);
    }

    // ==========================================
    // GETTERS
    // ==========================================

    public boolean isSuccess() { return success; }
    public int getHttpStatus() { return httpStatus; }
    public T getData() { return data; }
    public ApiError getError() { return error; }
    public Meta getMeta() { return meta; }
    public Instant getTimestamp() { return timestamp; }
    public String getTraceId() { return traceId; }
}