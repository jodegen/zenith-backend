package dev.zenith.finance.zenith_backend.presentation;

import dev.zenith.finance.zenith_backend.application.command.CreateCategoryCommand;
import dev.zenith.finance.zenith_backend.application.handler.CreateCategoryCommandHandler;
import dev.zenith.finance.zenith_backend.application.handler.GetCategoriesByUserQueryHandler;
import dev.zenith.finance.zenith_backend.application.query.GetCategoriesByUserQuery;
import dev.zenith.finance.zenith_backend.domain.model.CategoryType;
import dev.zenith.finance.zenith_backend.presentation.api.CategoriesApi;
import dev.zenith.finance.zenith_backend.presentation.assembler.CategoryModelAssembler;
import dev.zenith.finance.zenith_backend.presentation.model.CategoryResponse;
import dev.zenith.finance.zenith_backend.presentation.model.CategoryResponse.TypeEnum;
import dev.zenith.finance.zenith_backend.presentation.model.CreateCategoryRequest;
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

import java.util.List;

/**
 * REST controller for category management.
 *
 * <p>Implements {@link CategoriesApi} — generated from {@code zenith-api.yml}.
 * At runtime the body is wrapped in {@code EntityModel} / {@code CollectionModel}
 * (Spring HATEOAS). The cast is safe because Jackson serialises the actual runtime type.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController implements CategoriesApi {

    private final CreateCategoryCommandHandler createCategoryCommandHandler;
    private final GetCategoriesByUserQueryHandler getCategoriesByUserQueryHandler;
    private final CategoryModelAssembler categoryModelAssembler;

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<CategoryResponse> createCategory(CreateCategoryRequest createCategoryRequest) {
        var dto = createCategoryCommandHandler.handle(new CreateCategoryCommand(
                cognitoSub(),
                createCategoryRequest.getName(),
                CategoryType.valueOf(createCategoryRequest.getType().name())));
        var response = new CategoryResponse()
                .id(dto.id())
                .name(dto.name())
                .type(TypeEnum.fromValue(dto.type().name()))
                .defaultCategory(dto.defaultCategory());
        return (ResponseEntity<CategoryResponse>) (ResponseEntity<?>)
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(categoryModelAssembler.toModel(response));
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<CategoryResponse>> listCategories() {
        List<CategoryResponse> categories = getCategoriesByUserQueryHandler
                .handle(new GetCategoriesByUserQuery(cognitoSub()))
                .stream()
                .map(dto -> new CategoryResponse()
                        .id(dto.id())
                        .name(dto.name())
                        .type(TypeEnum.fromValue(dto.type().name()))
                        .defaultCategory(dto.defaultCategory()))
                .toList();

        var collectionModel = org.springframework.hateoas.CollectionModel.of(
                categories.stream().map(categoryModelAssembler::toModel).toList(),
                org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
                        .linkTo(org.springframework.hateoas.server.mvc.WebMvcLinkBuilder
                                .methodOn(CategoryController.class).listCategories())
                        .withSelfRel());

        return (ResponseEntity<List<CategoryResponse>>) (ResponseEntity<?>)
                ResponseEntity.ok(collectionModel);
    }

    private String cognitoSub() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated JWT found in SecurityContext");
        }
        return jwt.getSubject();
    }
}
