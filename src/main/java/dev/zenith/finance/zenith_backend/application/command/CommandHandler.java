package dev.zenith.finance.zenith_backend.application.command;

public interface CommandHandler<C, R> {
    R handle(C command);
}

