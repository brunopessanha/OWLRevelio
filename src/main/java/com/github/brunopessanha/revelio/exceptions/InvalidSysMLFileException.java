package com.github.brunopessanha.revelio.exceptions;

import java.io.IOException;

public class InvalidSysMLFileException extends IOException {

    public InvalidSysMLFileException(Exception ex) {
        super(ex);
    }

    public InvalidSysMLFileException(String message) {
        super(message);
    }
}
