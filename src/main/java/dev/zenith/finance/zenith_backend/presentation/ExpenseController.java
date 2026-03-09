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
import dev.zenith.finance.zenith_backend.presentation.assembler.ExpenseModelAssembler;
import dev.zenith.finance.zenith_backend.presentation.assembler.ExpensePagedModelAssembler;
import dev.zenith.finance.zenith_backend.presentation.model.CreateExpenseRequest;
import dev.zenith.finance.zenith_backend.presentation.model.ExpenseResponse;
import dev.zenith.finance.zenith_backend.presentation.model.UpdateExpenseRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Expense tracking endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final CreateExpenseCommandHandler createExpenseCommandHandler;
    private final UpdateExpenseCommandHandler updateExpenseCommandHandler;
    private final DeleteExpenseCommandHandler deleteExpenseCommandHandler;
    private final GetExpensesByUserQueryHandler getExpensesByUserQueryHandler;
    private final GetExpenseByIdQueryHandler getExpenseByIdQueryHandler;
    private final ExpenseModelAssembler expenseModelAssembler;
    private final ExpensePagedModelAssembler expensePagedModelAssembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "listExpenses", summary = "List expenses for the current user (paginated)")
    public ResponseEntity<PagedModel<EntityModel<ExpenseResponse>>> listExpenses(
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

        PagedResponse<ExpenseDto> paged = getExpensesByUserQueryHandler.handle(
                new GetExpensesByUserQuery(cognitoSub(), page, size, categoryId, from, to));

        PagedResponse<ExpenseResponse> pagedResponse = new PagedResponse<>(
                paged.content().stream().map(this::toResponse).toList(),
                paged.page(), paged.size(), paged.totalElements(), paged.totalPages());

        return ResponseEntity.ok(
                expensePagedModelAssembler.toPagedModel(pagedResponse, page, size, categoryId, from, to));
    }

    @GetMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "getExpenseById", summary = "Get a single expense by ID")
    public ResponseEntity<EntityModel<ExpenseResponse>> getExpenseById(@PathVariable UUID id) {
        ExpenseDto dto = getExpenseByIdQueryHandler.handle(new GetExpenseByIdQuery(id, cognitoSub()));
        return ResponseEntity.ok(expenseModelAssembler.toModel(toResponse(dto)));
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "createExpense", summary = "Create a new expense")
    public ResponseEntity<EntityModel<ExpenseResponse>> createExpense(
            @Valid @RequestBody CreateExpenseRequest createExpenseRequest) {
        ExpenseDto dto = createExpenseCommandHandler.handle(new CreateExpenseCommand(
                cognitoSub(),
                createExpenseRequest.getCategoryId(),
                BigDecimal.valueOf(createExpenseRequest.getAmount()),
                createExpenseRequest.getCurrency(),
                createExpenseRequest.getDescription(),
                createExpenseRequest.getDate()));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(expenseModelAssembler.toModel(toResponse(dto)));
    }

    @PutMapping(value = "/{id}", produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "updateExpense", summary = "Update an existing expense")
    public ResponseEntity<EntityModel<ExpenseResponse>> updateExpense(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateExpenseRequest updateExpenseRequest) {
        ExpenseDto dto = updateExpenseCommandHandler.handle(new UpdateExpenseCommand(
                id,
                cognitoSub(),
                updateExpenseRequest.getCategoryId(),
                updateExpenseRequest.getAmount() != null ? BigDecimal.valueOf(updateExpenseRequest.getAmount()) : null,
                updateExpenseRequest.getCurrency(),
                updateExpenseRequest.getDescription(),
                updateExpenseRequest.getDate()));
        return ResponseEntity.ok(expenseModelAssembler.toModel(toResponse(dto)));
    }

    @DeleteMapping("/{id}")
    @Operation(operationId = "deleteExpense", summary = "Soft-delete an expense")
    public ResponseEntity<Void> deleteExpense(@PathVariable UUID id) {
        deleteExpenseCommandHandler.handle(new DeleteExpenseCommand(id, cognitoSub()));
        return ResponseEntity.noContent().build();
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
