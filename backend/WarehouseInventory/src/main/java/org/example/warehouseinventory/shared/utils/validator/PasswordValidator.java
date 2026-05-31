package org.example.warehouseinventory.shared.utils.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.warehouseinventory.shared.domain.StrongPassword;

public class PasswordValidator implements ConstraintValidator<StrongPassword, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        if (password.length() < 8) {
            return false;
        }

        boolean hasUppercase = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSymbol = password.matches(".*[!@#$%^&*()].*");
        boolean hasLowercase = password.matches(".*[a-z].*");

        return hasUppercase && hasDigit && hasSymbol && hasLowercase;
    }
}
