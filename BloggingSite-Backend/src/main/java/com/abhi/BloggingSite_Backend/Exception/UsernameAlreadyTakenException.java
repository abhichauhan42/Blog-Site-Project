package com.abhi.BloggingSite_Backend.Exception;

public class UsernameAlreadyTakenException extends Exception {
    public UsernameAlreadyTakenException(String message) {
        super(message);
    }
}
