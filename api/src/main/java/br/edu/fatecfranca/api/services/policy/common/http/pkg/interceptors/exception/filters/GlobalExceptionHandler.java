package br.edu.fatecfranca.api.services.policy.common.http.pkg.interceptors.exception.filters;

import java.util.List;

import jakarta.validation.ConstraintViolationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import br.edu.fatecfranca.api.services.policy.common.http.pkg.errors.ApiError;
import br.edu.fatecfranca.api.services.policy.common.http.pkg.errors.FieldError;
import br.edu.fatecfranca.api.services.policy.contracts.Contract;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Erros de validação provenientes de @Valid em @RequestBody.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Contract<Void>> handleValidation(
            MethodArgumentNotValidException exception
    ) {

        List<FieldError> errors = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> new FieldError(
                        error.getField(),
                        error.getCode(),
                        error.getDefaultMessage()
                ))
                .toList();

        ApiError apiError = ApiError.validation(
                "Existem campos inválidos.",
                errors
        );

        Contract<Void> body = Contract.badRequest(apiError);
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    /**
     * Erros de validação provenientes de @RequestParam,
     * @PathVariable etc.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Contract<Void>> handleConstraintViolation(
            ConstraintViolationException exception
    ) {

        List<FieldError> errors = exception
                .getConstraintViolations()
                .stream()
                .map(error -> new FieldError(
                        error.getPropertyPath().toString(),
                        error.getConstraintDescriptor()
                                .getAnnotation()
                                .annotationType()
                                .getSimpleName(),
                        error.getMessage()
                ))
                .toList();

        ApiError apiError = ApiError.validation(
                "Existem parâmetros inválidos.",
                errors
        );

        Contract<Void> body = Contract.badRequest(apiError);
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    /**
     * Conflito de dados no banco (ex.: valor único duplicado).
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Contract<Void>> handleDataIntegrityViolation(
            DataIntegrityViolationException exception
    ) {
        ApiError error = ApiError.of(
                "CONFLICT",
                "Dados duplicados ou violando restrições do banco."
        );

        Contract<Void> body = Contract.conflict(error.getCode(), error.getMessage());
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    /**
     * Recurso não encontrado.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Contract<Void>> handleNotFound(
            ResourceNotFoundException exception
    ) {

        ApiError error = ApiError.of(
                exception.getCode(),
                exception.getMessage()
        );

        Contract<Void> body = Contract.notFound(error);
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    /**
     * Regra de negócio violada.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Contract<Void>> handleBusiness(
            BusinessException exception
    ) {

        ApiError error = ApiError.of(
                exception.getCode(),
                exception.getMessage()
        );

        Contract<Void> body = Contract.unprocessableEntity(error);
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }

    /**
     * Erro inesperado.
     *
     * IMPORTANTE:
     * Não retornamos exception.getMessage() para o cliente.
     * Isso evita vazar detalhes internos da aplicação.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Contract<Void>> handleGeneric(
            Exception exception
    ) {

        ApiError error = ApiError.of(
                "INTERNAL_ERROR",
                "Ocorreu um erro interno. Tente novamente mais tarde."
        );

        logger.error("Unhandled exception caught by GlobalExceptionHandler", exception);

        Contract<Void> body = Contract.internalServerError(error);
        return ResponseEntity.status(HttpStatus.valueOf(body.getHttpStatus())).body(body);
    }
}