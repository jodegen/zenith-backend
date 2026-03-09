package dev.zenith.finance.zenith_backend.presentation.problem;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;

import java.net.URI;
import java.util.List;

/**
 * Extension of Spring's {@link ProblemDetail} that adds a structured list of
 * {@link Violation}s for validation errors.
 *
 * <p>Produces the following JSON structure (RFC 7807 compliant):
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
public class ViolationProblemDetail extends ProblemDetail {

    @JsonProperty("violations")
    private final List<Violation> violations;

    private ViolationProblemDetail(HttpStatusCode status, List<Violation> violations) {
        super(status.value());
        this.violations = List.copyOf(violations);
    }

    public static ViolationProblemDetail of(
            URI type,
            String title,
            HttpStatusCode status,
            String detail,
            List<Violation> violations) {
        ViolationProblemDetail pd = new ViolationProblemDetail(status, violations);
        pd.setType(type);
        pd.setTitle(title);
        pd.setDetail(detail);
        return pd;
    }

    public List<Violation> getViolations() {
        return violations;
    }
}

