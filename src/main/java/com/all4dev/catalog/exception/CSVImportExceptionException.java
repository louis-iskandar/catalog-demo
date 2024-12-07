package com.all4dev.catalog.exception;

import lombok.Getter;

import java.io.IOException;
import java.util.Map;

@Getter
public class CSVImportExceptionException extends RuntimeException {
    private Map<Integer, Exception> map;

    public CSVImportExceptionException(Map<Integer, Exception> map) {
        this.map = map;
    }
}
