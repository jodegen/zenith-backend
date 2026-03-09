package dev.zenith.finance.zenith_backend.presentation.assembler;

import dev.zenith.finance.zenith_backend.application.dto.PagedResponse;
import dev.zenith.finance.zenith_backend.presentation.ExpenseController;
import dev.zenith.finance.zenith_backend.presentation.model.ExpenseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.PagedModel.PageMetadata;
import jakarta.annotation.Nonnull;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
@RequiredArgsConstructor
public class ExpensePagedModelAssembler {

    private final ExpenseModelAssembler expenseModelAssembler;

    public PagedModel<EntityModel<ExpenseResponse>> toPagedModel(
            @Nonnull PagedResponse<ExpenseResponse> paged,
            Integer page,
            Integer size,
            UUID categoryId,
            LocalDate from,
            LocalDate to) {

        List<EntityModel<ExpenseResponse>> content = paged.content().stream()
                .map(expenseModelAssembler::toModel)
                .toList();

        PageMetadata metadata = new PageMetadata(paged.size(), paged.page(), paged.totalElements(), paged.totalPages());

        Link selfLink = linkTo(methodOn(ExpenseController.class)
                .listExpenses(page, size, categoryId, from, to))
                .withSelfRel();

        PagedModel<EntityModel<ExpenseResponse>> pagedModel = PagedModel.of(content, metadata, selfLink);

        // Add prev / next navigation links
        if (page > 0) {
            Link prevLink = linkTo(methodOn(ExpenseController.class)
                    .listExpenses(page - 1, size, categoryId, from, to))
                    .withRel("prev");
            pagedModel.add(prevLink);
        }
        if (page < paged.totalPages() - 1) {
            Link nextLink = linkTo(methodOn(ExpenseController.class)
                    .listExpenses(page + 1, size, categoryId, from, to))
                    .withRel("next");
            pagedModel.add(nextLink);
        }
        if (paged.totalPages() > 0) {
            Link firstLink = linkTo(methodOn(ExpenseController.class)
                    .listExpenses(0, size, categoryId, from, to))
                    .withRel("first");
            Link lastLink = linkTo(methodOn(ExpenseController.class)
                    .listExpenses(paged.totalPages() - 1, size, categoryId, from, to))
                    .withRel("last");
            pagedModel.add(firstLink, lastLink);
        }

        return pagedModel;
    }
}



