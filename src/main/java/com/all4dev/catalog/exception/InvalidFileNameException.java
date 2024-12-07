package com.all4dev.catalog.exception;

import java.io.IOException;

public class InvalidFileNameException extends IOException {
    public InvalidFileNameException() {
    }

    // Constructor that accepts a message
    public InvalidFileNameException(String message) {
        super(message);
    }
}
