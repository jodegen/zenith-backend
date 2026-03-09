package dev.zenith.finance.zenith_backend.application.query;

public interface QueryHandler<Q, R> {
    R handle(Q query);
}

