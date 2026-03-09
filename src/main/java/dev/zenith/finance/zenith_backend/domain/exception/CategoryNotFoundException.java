package dev.zenith.finance.zenith_backend.domain.exception;

import java.util.UUID;

public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(UUID categoryId) {
        super("Category not found: " + categoryId);
    }
}

