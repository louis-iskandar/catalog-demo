package com.all4dev.catalog.exception;

import java.io.IOException;

public class ResourceNotFoundException extends IOException {

    public ResourceNotFoundException() {
    }

    // Constructor that accepts a message
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
