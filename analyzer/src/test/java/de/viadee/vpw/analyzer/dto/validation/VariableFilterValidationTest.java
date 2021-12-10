package de.viadee.vpw.analyzer.dto.validation;

import java.util.Locale;
import java.util.Set;

import de.viadee.vpw.analyzer.dto.entity.filter.VariableFilter;
import de.viadee.vpw.analyzer.dto.typelist.FilterValueComparator;
import jakarta.validation.ConstraintViolation;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VariableFilterValidationTest {

    private static ValidatorFactory validatorFactory;

    private static Validator validator;


    @BeforeAll
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @Test
    public void testValidation_valueRequired_valid() {
        VariableFilter filter = new VariableFilter("var", FilterValueComparator.EQ, 1);
        validateAndExpectNoViolation(filter);
    }

    @Test
    public void testValidation_valueRequired_invalid() {
        VariableFilter filter = new VariableFilter("var", FilterValueComparator.EQ, null);
        ConstraintViolation<VariableFilter> violation = validateAndExpectViolation(filter);
        assertEquals("value is required", violation.getMessage());
    }

    @Test
    public void testValidation_valueNotAllowed_valid() {
        VariableFilter filter = new VariableFilter("var", FilterValueComparator.NULL, null);
        validateAndExpectNoViolation(filter);
    }

    @Test
    public void testValidation_valueNotAllowed_invalid() {
        VariableFilter filter = new VariableFilter("var", FilterValueComparator.NULL, 1);
        ConstraintViolation<VariableFilter> violation = validateAndExpectViolation(filter);
        assertEquals("value not allowed", violation.getMessage());
    }

    @Test
    public void testValidation_comparatorIsNull() {
        VariableFilter filter = new VariableFilter("var", null, 1);
        ConstraintViolation<VariableFilter> violation = validateAndExpectViolation(filter);
        assertEquals(mustNotBeNull(), violation.getMessage());
        assertEquals("comparator", violation.getPropertyPath().toString());
    }

    @Test
    public void testValidation_keyIsNull() {
        VariableFilter filter = new VariableFilter(null, FilterValueComparator.EQ, 1);
        ConstraintViolation<VariableFilter> violation = validateAndExpectViolation(filter);
        assertEquals(mustNotBeBlank(), violation.getMessage());
        assertEquals("key", violation.getPropertyPath().toString());
    }

    private void validateAndExpectNoViolation(VariableFilter filter) {
        assertTrue(validator.validate(filter).isEmpty());
    }

    private ConstraintViolation<VariableFilter> validateAndExpectViolation(VariableFilter filter) {
        Set<ConstraintViolation<VariableFilter>> violations = validator.validate(filter);
        assertEquals(1, violations.size());
        return violations.iterator().next();
    }

    private String mustNotBeNull() {
        return "de".equals(getLanguage()) ? "darf nicht null sein" : "must not be null";
    }

    private String mustNotBeBlank() {
        return "de".equals(getLanguage()) ? "darf nicht leer sein" : "must not be blank";
    }

    private String getLanguage() {
        return Locale.getDefault().getLanguage();
    }
}