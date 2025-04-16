package com.example.emtlab.model.exceptions;

public class AccommodationNotBookedException extends RuntimeException {
    public AccommodationNotBookedException(String name) {
        super("Accommodation " + name + " is not booked");
    }
}
