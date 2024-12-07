package com.all4dev.catalog.exception;

import java.io.IOException;

public class ResourceAlreadyExistException extends IOException {

    public ResourceAlreadyExistException() {
    }

    // Constructor that accepts a message
    public ResourceAlreadyExistException(String message) {
        super(message);
    }
}
