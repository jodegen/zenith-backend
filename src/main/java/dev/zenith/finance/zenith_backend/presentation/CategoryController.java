package dev.zenith.finance.zenith_backend.presentation;

import dev.zenith.finance.zenith_backend.application.command.CreateCategoryCommand;
import dev.zenith.finance.zenith_backend.application.handler.CreateCategoryCommandHandler;
import dev.zenith.finance.zenith_backend.application.handler.GetCategoriesByUserQueryHandler;
import dev.zenith.finance.zenith_backend.application.query.GetCategoriesByUserQuery;
import dev.zenith.finance.zenith_backend.domain.model.CategoryType;
import dev.zenith.finance.zenith_backend.presentation.assembler.CategoryModelAssembler;
import dev.zenith.finance.zenith_backend.presentation.model.CategoryResponse;
import dev.zenith.finance.zenith_backend.presentation.model.CategoryResponse.TypeEnum;
import dev.zenith.finance.zenith_backend.presentation.model.CreateCategoryRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class CategoryController {

    private final CreateCategoryCommandHandler createCategoryCommandHandler;
    private final GetCategoriesByUserQueryHandler getCategoriesByUserQueryHandler;
    private final CategoryModelAssembler categoryModelAssembler;

    @GetMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "listCategories", summary = "List all categories for the current user")
    public ResponseEntity<CollectionModel<EntityModel<CategoryResponse>>> listCategories() {
        List<EntityModel<CategoryResponse>> categories = getCategoriesByUserQueryHandler
                .handle(new GetCategoriesByUserQuery(cognitoSub()))
                .stream()
                .map(dto -> new CategoryResponse()
                        .id(dto.id())
                        .name(dto.name())
                        .type(TypeEnum.fromValue(dto.type().name()))
                        .defaultCategory(dto.defaultCategory()))
                .map(categoryModelAssembler::toModel)
                .toList();

        CollectionModel<EntityModel<CategoryResponse>> model = CollectionModel.of(
                categories,
                linkTo(methodOn(CategoryController.class).listCategories()).withSelfRel());

        return ResponseEntity.ok(model);
    }

    @PostMapping(produces = MediaTypes.HAL_JSON_VALUE)
    @Operation(operationId = "createCategory", summary = "Create a new category")
    public ResponseEntity<EntityModel<CategoryResponse>> createCategory(
            @Valid @RequestBody CreateCategoryRequest createCategoryRequest) {
        var dto = createCategoryCommandHandler.handle(new CreateCategoryCommand(
                cognitoSub(),
                createCategoryRequest.getName(),
                CategoryType.valueOf(createCategoryRequest.getType().name())));
        var response = new CategoryResponse()
                .id(dto.id())
                .name(dto.name())
                .type(TypeEnum.fromValue(dto.type().name()))
                .defaultCategory(dto.defaultCategory());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(categoryModelAssembler.toModel(response));
    }

    private String cognitoSub() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("No authenticated JWT found in SecurityContext");
        }
        return jwt.getSubject();
    }
}
