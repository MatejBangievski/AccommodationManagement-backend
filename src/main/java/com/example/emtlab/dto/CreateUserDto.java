package com.example.emtlab.dto;

import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.enumerations.Role;
import com.example.emtlab.model.exceptions.PasswordsDoNotMatchException;


public record CreateUserDto(
        String username,
        String password,
        String repeatPassword,
        String name,
        String surname,
        Role role
) {


    public User toUser() {
        if (!password.equals(repeatPassword)) {
            throw new PasswordsDoNotMatchException();
        }
        return new User(username, password, name, surname, role);
    }
}


