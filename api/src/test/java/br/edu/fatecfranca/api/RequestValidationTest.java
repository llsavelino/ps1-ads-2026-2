package br.edu.fatecfranca.api;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import br.edu.fatecfranca.api.controllers.pipes.reqs.CarReq;
import br.edu.fatecfranca.api.controllers.pipes.reqs.UserReq;
import br.edu.fatecfranca.api.controllers.pipes.reqs.ValidationGroups;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

class RequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void patchAllowsOmittedFieldsButValidatesProvidedValues() {
        CarReq request = new CarReq();
        request.setBrand("Honda");

        assertTrue(validator.validate(request).isEmpty());

        request.setYearManufacture(2101);
        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void fullUserRequestRequiresFieldsAndValidatesPasswordLength() {
        UserReq request = new UserReq();
        request.setFullname("Ana Souza");
        request.setUsername("ana.souza");
        request.setEmail("ana@example.com");
        request.setPassword("short");

        Set<?> violations = validator.validate(request, ValidationGroups.Required.class);

        assertTrue(violations.stream().anyMatch(violation ->
            violation.toString().contains("password")
        ));
        assertFalse(validator.validate(new UserReq(), ValidationGroups.Required.class).isEmpty());
    }
}