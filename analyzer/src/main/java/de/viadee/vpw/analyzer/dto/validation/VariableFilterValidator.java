package de.viadee.vpw.analyzer.dto.validation;


import de.viadee.vpw.analyzer.dto.entity.filter.VariableFilter;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class VariableFilterValidator implements ConstraintValidator<VariableFilterConstraint, VariableFilter> {

    @Override
    public boolean isValid(VariableFilter filter, ConstraintValidatorContext context) {
        return filter.getComparator() == null || valueRequiredAndNotNull(filter) || valueNotRequiredAndNull(filter);
    }

    private boolean valueRequiredAndNotNull(VariableFilter filter) {
        return filter.getComparator().isValueRequired() && filter.getValue() != null;
    }

    private boolean valueNotRequiredAndNull(VariableFilter filter) {
        return !filter.getComparator().isValueRequired() && filter.getValue() == null;
    }
}
