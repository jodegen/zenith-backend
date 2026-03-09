package dev.zenith.finance.zenith_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Category {

    private final UUID id;
    private final UserAccount owner;
    private final String name;
    private final CategoryType type;
    private final boolean defaultCategory;
}

