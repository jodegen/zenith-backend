package dev.zenith.finance.zenith_backend.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Expense {

    private final UUID id;
    private final UserAccount owner;
    private final Category category;
    private final Money amount;
    private final String description;
    private final LocalDate date;
    private ExpenseStatus status;

    public void delete() {
        this.status = ExpenseStatus.DELETED;
    }
}

