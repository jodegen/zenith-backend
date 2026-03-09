package dev.zenith.finance.zenith_backend.presentation;

import dev.zenith.finance.zenith_backend.domain.exception.CategoryNotFoundException;
import dev.zenith.finance.zenith_backend.domain.exception.ExpenseNotFoundException;
import dev.zenith.finance.zenith_backend.domain.exception.UnauthorizedAccessException;
import dev.zenith.finance.zenith_backend.domain.exception.UserAccountNotFoundException;
import dev.zenith.finance.zenith_backend.presentation.problem.Violation;
import dev.zenith.finance.zenith_backend.presentation.problem.ViolationProblemDetail;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;

/**
 * Global exception handler producing RFC 7807 Problem Detail responses.
 *
 * <p>Extends {@link ResponseEntityExceptionHandler} to inherit automatic handling of all
 * standard Spring MVC exceptions (400 Bad Request, 404 Not Found, 405 Method Not Allowed,
 * etc.) as {@code application/problem+json}.
 *
 * <p>Domain exceptions are mapped to structured {@link ProblemDetail} responses.
 * Validation errors are mapped to {@link ViolationProblemDetail} which includes
 * a structured {@code violations} array — similar to the Zalando Problem Violations format.
 *
 * <p>Example validation error response:
 * <pre>{@code
 * {
 *   "type":       "https://zenith.dev/errors/validation-error",
 *   "title":      "Validation Error",
 *   "status":     422,
 *   "detail":     "Request validation failed",
 *   "violations": [
 *     { "field": "amount",   "message": "must be greater than 0" },
 *     { "field": "currency", "message": "must not be blank" }
 *   ]
 * }
 * }</pre>
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final URI ERROR_BASE = URI.create("https://zenith.dev/errors");

    // ── Domain Exceptions ─────────────────────────────────────────────────────

    @ExceptionHandler(ExpenseNotFoundException.class)
    ProblemDetail handleExpenseNotFound(ExpenseNotFoundException ex) {
        return notFound(ERROR_BASE + "/expense-not-found", "Expense Not Found", ex.getMessage());
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    ProblemDetail handleCategoryNotFound(CategoryNotFoundException ex) {
        return notFound(ERROR_BASE + "/category-not-found", "Category Not Found", ex.getMessage());
    }

    @ExceptionHandler(UserAccountNotFoundException.class)
    ProblemDetail handleUserNotFound(UserAccountNotFoundException ex) {
        return notFound(ERROR_BASE + "/user-not-found", "User Not Found", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    ProblemDetail handleUnauthorizedAccess(UnauthorizedAccessException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        pd.setType(URI.create(ERROR_BASE + "/forbidden"));
        pd.setTitle("Forbidden");
        return pd;
    }

    // ── Validation Exceptions ─────────────────────────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ViolationProblemDetail handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<Violation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new Violation(fe.getField(), fe.getDefaultMessage()))
                .toList();
        return validationError(violations);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    ViolationProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        List<Violation> violations = ex.getConstraintViolations().stream()
                .map(cv -> new Violation(
                        cv.getPropertyPath().toString(),
                        cv.getMessage()))
                .toList();
        return validationError(violations);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static ProblemDetail notFound(String type, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail);
        pd.setType(URI.create(type));
        pd.setTitle(title);
        return pd;
    }

    private static ViolationProblemDetail validationError(List<Violation> violations) {
        return ViolationProblemDetail.of(
                URI.create(ERROR_BASE + "/validation-error"),
                "Validation Error",
                HttpStatusCode.valueOf(422),
                "Request validation failed",
                violations);
    }
}
