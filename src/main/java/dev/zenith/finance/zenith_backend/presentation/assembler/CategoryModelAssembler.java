package dev.zenith.finance.zenith_backend.presentation.assembler;

import dev.zenith.finance.zenith_backend.presentation.CategoryController;
import dev.zenith.finance.zenith_backend.presentation.model.CategoryResponse;
import jakarta.annotation.Nonnull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class CategoryModelAssembler
        implements RepresentationModelAssembler<CategoryResponse, EntityModel<CategoryResponse>> {

    @Override
    @Nonnull
    public EntityModel<CategoryResponse> toModel(@Nonnull CategoryResponse category) {
        Link selfLink = linkTo(methodOn(CategoryController.class)
                .listCategories())
                .withSelfRel();

        Link collectionLink = linkTo(methodOn(CategoryController.class)
                .listCategories())
                .withRel("categories");

        return EntityModel.of(category, selfLink, collectionLink);
    }
}
