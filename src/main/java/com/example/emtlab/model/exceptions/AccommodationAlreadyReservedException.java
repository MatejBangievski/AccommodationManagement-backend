package com.example.emtlab.model.exceptions;

public class AccommodationAlreadyReservedException extends RuntimeException {
    public AccommodationAlreadyReservedException(String name) {
        super("Accommodation " + name + " is already reserved");
    }
}
