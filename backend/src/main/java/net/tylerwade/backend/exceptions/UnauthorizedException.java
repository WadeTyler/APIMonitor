package net.tylerwade.backend.exceptions;

import java.io.IOException;

public class UnauthorizedException extends IOException {
    public UnauthorizedException(Exception e) {
        super(e);
    }

    public UnauthorizedException(String message) {
        super(message);
    }
}
