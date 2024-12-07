package com.all4dev.catalog.exception;

import java.io.IOException;

public class InvalidFileFormatException extends IOException {
    public InvalidFileFormatException() {
    }

    // Constructor that accepts a message
    public InvalidFileFormatException(String message) {
        super(message);
    }
}
