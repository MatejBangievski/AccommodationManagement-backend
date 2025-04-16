package com.example.emtlab.model.exceptions;

public class AccommodationNotReservedException extends RuntimeException {
    public AccommodationNotReservedException(String name) {
        super("Accommodation" + name + " is not reserved");
    }
}
