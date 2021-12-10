package de.viadee.vpw.analyzer.dto.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = VariableFilterValidator.class)
public @interface VariableFilterConstraint {

    String message() default "${validatedValue.comparator.valueRequired ? 'value is required' : 'value not allowed'}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
