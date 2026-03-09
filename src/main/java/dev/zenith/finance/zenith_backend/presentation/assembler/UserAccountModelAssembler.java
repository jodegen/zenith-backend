package dev.zenith.finance.zenith_backend.presentation.assembler;

import dev.zenith.finance.zenith_backend.presentation.AccountController;
import dev.zenith.finance.zenith_backend.presentation.model.UserAccountResponse;
import jakarta.annotation.Nonnull;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class UserAccountModelAssembler
        implements RepresentationModelAssembler<UserAccountResponse, EntityModel<UserAccountResponse>> {

    @Override
    @Nonnull
    public EntityModel<UserAccountResponse> toModel(@Nonnull UserAccountResponse account) {
        Link selfLink = linkTo(methodOn(AccountController.class)
                .getMe())
                .withSelfRel();

        return EntityModel.of(account, selfLink);
    }
}
