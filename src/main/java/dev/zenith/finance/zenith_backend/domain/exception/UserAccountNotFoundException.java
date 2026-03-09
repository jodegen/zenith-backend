package dev.zenith.finance.zenith_backend.domain.exception;

public class UserAccountNotFoundException extends RuntimeException {
    public UserAccountNotFoundException(String cognitoSub) {
        super("UserAccount not found for cognitoSub: " + cognitoSub);
    }
}

