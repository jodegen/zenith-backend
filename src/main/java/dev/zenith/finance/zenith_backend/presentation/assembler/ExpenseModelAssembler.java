package dev.zenith.finance.zenith_backend.presentation.assembler;

import dev.zenith.finance.zenith_backend.presentation.ExpenseController;
import dev.zenith.finance.zenith_backend.presentation.model.ExpenseResponse;
import jakarta.annotation.Nonnull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ExpenseModelAssembler
        implements RepresentationModelAssembler<ExpenseResponse, EntityModel<ExpenseResponse>> {

    @Override
    @Nonnull
    public EntityModel<ExpenseResponse> toModel(@Nonnull ExpenseResponse expense) {
        Link selfLink = linkTo(methodOn(ExpenseController.class)
                .getExpenseById(expense.getId()))
                .withSelfRel();

        Link collectionLink = linkTo(methodOn(ExpenseController.class)
                .listExpenses(0, 20, null, null, null))
                .withRel("expenses");

        Link updateLink = linkTo(methodOn(ExpenseController.class)
                .updateExpense(expense.getId(), null))
                .withRel("update");

        Link deleteLink = linkTo(methodOn(ExpenseController.class)
                .deleteExpense(expense.getId()))
                .withRel("delete");

        return EntityModel.of(expense, selfLink, collectionLink, updateLink, deleteLink);
    }
}
