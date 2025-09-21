package com.example.emtlab.model.exceptions;

public class AccommodationAlreadyBookedException extends RuntimeException {
    public AccommodationAlreadyBookedException(String name) {
        super("Accommodation " + name + " is already booked");
    }
}
