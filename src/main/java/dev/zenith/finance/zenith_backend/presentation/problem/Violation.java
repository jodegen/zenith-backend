package dev.zenith.finance.zenith_backend.presentation.problem;

/**
 * Represents a single constraint violation with a field path and a human-readable message.
 * Modelled after the Zalando Problem Violations library for structural compatibility.
 */
public record Violation(String field, String message) {}

