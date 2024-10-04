package com.camelsoft.portal.customExceptions;

public class RequirementNotFoundException extends RuntimeException {
    public RequirementNotFoundException(String message) {
        super(message);
    }
}