package dev.zenith.finance.zenith_backend.presentation;

import dev.zenith.finance.zenith_backend.application.command.CreateExpenseCommand;
import dev.zenith.finance.zenith_backend.application.command.DeleteExpenseCommand;
import dev.zenith.finance.zenith_backend.application.command.UpdateExpenseCommand;
import dev.zenith.finance.zenith_backend.application.dto.ExpenseDto;
import dev.zenith.finance.zenith_backend.application.dto.PagedResponse;
import dev.zenith.finance.zenith_backend.application.handler.CreateExpenseCommandHandler;
import dev.zenith.finance.zenith_backend.application.handler.DeleteExpenseCommandHandler;
import dev.zenith.finance.zenith_backend.application.handler.GetExpenseByIdQueryHandler;
import dev.zenith.finance.zenith_backend.application.handler.GetExpensesByUserQueryHandler;
import dev.zenith.finance.zenith_backend.application.handler.UpdateExpenseCommandHandler;
import dev.zenith.finance.zenith_backend.application.query.GetExpenseByIdQuery;
import dev.zenith.finance.zenith_backend.application.query.GetExpensesByUserQuery;
import dev.zenith.finance.zenith_backend.presentation.api.ExpensesApi;
import dev.zenith.finance.zenith_backend.presentation.assembler.ExpenseModelAssembler;
import dev.zenith.finance.zenith_backend.presentation.assembler.ExpensePagedModelAssembler;
import dev.zenith.finance.zenith_backend.presentation.model.CreateExpenseRequest;
import dev.zenith.finance.zenith_backend.presentation.model.ExpenseResponse;
import dev.zenith.finance.zenith_backend.presentation.model.PagedExpenseResponse;
import dev.zenith.finance.zenith_backend.presentation.model.UpdateExpenseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * REST controller for expense management.
 *
 * <p>Implements {@link ExpensesApi} — the interface generated from {@code zenith-api.yml}.
 * The generated interface declares {@code ResponseEntity<ExpenseResponse>} return types.
 * At runtime, the body is an {@code EntityModel<ExpenseResponse>} (Spring HATEOAS), which
 * Jackson serialises correctly as HAL JSON because it serialises the actual runtime type,
 * not the declared generic type. The {@code @SuppressWarnings("unchecked")} cast is
 * therefore safe.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Expense tracking endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController implements ExpensesApi {

    private final CreateExpenseCommandHandler createExpenseCommandHandler;
    private final UpdateExpenseCommandHandler updateExpenseCommandHandler;
    private final DeleteExpenseCommandHandler deleteExpenseCommandHandler;
    private final GetExpensesByUserQueryHandler getExpensesByUserQueryHandler;
    private final GetExpenseByIdQueryHandler getExpenseByIdQueryHandler;
    private final ExpenseModelAssembler expenseModelAssembler;
    private final ExpensePagedModelAssembler expensePagedModelAssembler;

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<ExpenseResponse> createExpense(CreateExpenseRequest createExpenseRequest) {
        ExpenseDto dto = createExpenseCommandHandler.handle(new CreateExpenseCommand(
                cognitoSub(),
                createExpenseRequest.getCategoryId(),
                BigDecimal.valueOf(createExpenseRequest.getAmount()),
                createExpenseRequest.getCurrency(),
                createExpenseRequest.getDescription(),
                createExpenseRequest.getDate()));
        return (ResponseEntity<ExpenseResponse>) (ResponseEntity<?>)
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(expenseModelAssembler.toModel(toResponse(dto)));
    }

    @Override
    public ResponseEntity<Void> deleteExpense(UUID id) {
        deleteExpenseCommandHandler.handle(new DeleteExpenseCommand(id, cognitoSub()));
        return ResponseEntity.noContent().build();
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<ExpenseResponse> getExpenseById(UUID id) {
        ExpenseDto dto = getExpenseByIdQueryHandler.handle(new GetExpenseByIdQuery(id, cognitoSub()));
        return (ResponseEntity<ExpenseResponse>) (ResponseEntity<?>)
                ResponseEntity.ok(expenseModelAssembler.toModel(toResponse(dto)));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<PagedExpenseResponse> listExpenses(
            Integer page, Integer size, UUID categoryId, LocalDate from, LocalDate to) {
        PagedResponse<ExpenseDto> paged = getExpensesByUserQueryHandler.handle(
                new GetExpensesByUserQuery(cognitoSub(), page, size, categoryId, from, to));
        PagedResponse<ExpenseResponse> pagedResponse = new PagedResponse<>(
                paged.content().stream().map(this::toResponse).toList(),
                paged.page(), paged.size(), paged.totalElements(), paged.totalPages());
        return (ResponseEntity<PagedExpenseResponse>) (ResponseEntity<?>)
                ResponseEntity.ok(expensePagedModelAssembler.toPagedModel(
                        pagedResponse, page, size, categoryId, from, to));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<ExpenseResponse> updateExpense(UUID id, UpdateExpenseRequest updateExpenseRequest) {
        ExpenseDto dto = updateExpenseCommandHandler.handle(new UpdateExpenseCommand(
                id,
                cognitoSub(),
                updateExpenseRequest.getCategoryId(),
                updateExpenseRequest.getAmount() != null
                        ? BigDecimal.valueOf(updateExpenseRequest.getAmount()) : null,
                updateExpenseRequest.getCurrency(),
                updateExpenseRequest.getDescription(),
                updateExpenseRequest.getDate()));
        return (ResponseEntity<ExpenseResponse>) (ResponseEntity<?>)
                ResponseEntity.ok(expenseModelAssembler.toModel(toResponse(dto)));
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private ExpenseResponse toResponse(ExpenseDto dto) {
        return new ExpenseResponse()
                .id(dto.id())
                .categoryId(dto.categoryId())
                .categoryName(dto.categoryName())
                .amount(dto.amount().doubleValue())
                .currency(dto.currency())
                .description(dto.description())
                .date(dto.date());
    }

    private String cognitoSub() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated JWT found in SecurityContext");
        }
        return jwt.getSubject();
    }
}
