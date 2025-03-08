package net.tylerwade.backend.exceptions;

import java.io.IOException;

public class NotAcceptableException extends IOException {
    public NotAcceptableException(String message) {
        super(message);
    }
}
