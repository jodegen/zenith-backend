package dev.zenith.finance.zenith_backend.presentation;

import dev.zenith.finance.zenith_backend.domain.exception.CategoryNotFoundException;
import dev.zenith.finance.zenith_backend.domain.exception.ExpenseNotFoundException;
import dev.zenith.finance.zenith_backend.domain.exception.UnauthorizedAccessException;
import dev.zenith.finance.zenith_backend.domain.exception.UserAccountNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final HttpStatusCode UNPROCESSABLE_ENTITY = HttpStatusCode.valueOf(422);

    @ExceptionHandler(ExpenseNotFoundException.class)
    public ProblemDetail handleExpenseNotFound(ExpenseNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("/errors/expense-not-found"));
        return pd;
    }

    @ExceptionHandler(CategoryNotFoundException.class)
    public ProblemDetail handleCategoryNotFound(CategoryNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("/errors/category-not-found"));
        return pd;
    }

    @ExceptionHandler(UserAccountNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserAccountNotFoundException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        pd.setType(URI.create("/errors/user-not-found"));
        return pd;
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ProblemDetail handleUnauthorized(UnauthorizedAccessException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        pd.setType(URI.create("/errors/forbidden"));
        return pd;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, details);
        pd.setType(URI.create("/errors/validation"));
        return pd;
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolation(ConstraintViolationException ex) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(UNPROCESSABLE_ENTITY, ex.getMessage());
        pd.setType(URI.create("/errors/validation"));
        return pd;
    }
}

