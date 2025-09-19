package com.example.emtlab.web.controllers;

import com.example.emtlab.dto.*;
import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.exceptions.InvalidArgumentsException;
import com.example.emtlab.model.exceptions.InvalidUserCredentialsException;
import com.example.emtlab.model.exceptions.PasswordsDoNotMatchException;
import com.example.emtlab.service.application.UserApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User API", description = "Endpoints for user authentication and registration") // Swagger tag
public class UserController {

    private final UserApplicationService userApplicationService;

    public UserController(UserApplicationService userApplicationService) {
        this.userApplicationService = userApplicationService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponses(
            value = {@ApiResponse(
                    responseCode = "200",
                    description = "User registered successfully"
            ), @ApiResponse(
                    responseCode = "400", description = "Invalid input or passwords do not match"
            )}
    )
    @PostMapping("/register")
    public ResponseEntity<DisplayUserDto> register(@RequestBody CreateUserDto createUserDto) {
        try {
            return userApplicationService.register(createUserDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (InvalidArgumentsException | PasswordsDoNotMatchException exception) {
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "User login", description = "Authenticates a user and generates a JWT")
    @ApiResponses(
            value = {@ApiResponse(
                    responseCode = "200",
                    description = "User authenticated successfully"
            ), @ApiResponse(responseCode = "404", description = "Invalid username or password")}
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginUserDto loginUserDto) {
        try {
            return userApplicationService.login(loginUserDto)
                    .map(ResponseEntity::ok)
                    .orElseThrow(InvalidUserCredentialsException::new);
        } catch (InvalidUserCredentialsException e) {
            return ResponseEntity.notFound().build();
        }
    }

//    @Operation(summary = "User logout", description = "Ends the user's session")
//    @ApiResponse(responseCode = "200", description = "User logged out successfully")
//    @GetMapping("/logout")
//    public void logout(HttpServletRequest request) {
//        request.getSession().invalidate();
//    }

    @Operation(summary = "Find user by username", description = "Returns user details by username")
    @ApiResponse(responseCode = "200", description = "User found")
    @GetMapping("/{username}")
    public ResponseEntity<DisplayUserDto> findByUsername(@PathVariable String username) {
        return userApplicationService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Find where a user is staying by username", description = "Returns accommodation details by username")
    @ApiResponses(
            value = {@ApiResponse(
                    responseCode = "200",
                    description = "Accommodation found"
            ), @ApiResponse(responseCode = "404", description = "Invalid username or he's not staying anywhere")}
    )    @GetMapping("/findStayingAccommodation/{username}")
    public ResponseEntity<DisplayAccommodationDto> findStayingAccommodation(@PathVariable String username) {
        return userApplicationService.findWhereIsStaying(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Reserve accommodation", description = "Reserves an accommodation for the user")
    @ApiResponse(responseCode = "200", description = "Accommodation reserved")
    @PostMapping("/{username}/reserve/{accommodationId}")
    public ResponseEntity<DisplayUserDto> reserveAccommodation(@PathVariable Long accommodationId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userApplicationService.reserveAccommodation(user.getUsername(), accommodationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cancel accommodation reservation", description = "Cancels an accommodation reservation for the user")
    @ApiResponse(responseCode = "200", description = "Accommodation reservation canceled")
    @PostMapping("/{username}/cancel/{accommodationId}")
    public ResponseEntity<DisplayUserDto> cancelAccommodation(@PathVariable Long accommodationId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userApplicationService.cancelAccommodation(user.getUsername(), accommodationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Book accommodation", description = "Books a reserved accommodation for the user")
    @ApiResponse(responseCode = "200", description = "Accommodation booked")
    @PostMapping("/{username}/book/{accommodationId}")
    public ResponseEntity<DisplayUserDto> bookAccommodation(@PathVariable Long accommodationId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userApplicationService.bookAccommodation(user.getUsername(), accommodationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Find all reservations", description = "Returns a list of all reservations for the user")
    @ApiResponse(responseCode = "200", description = "List of reservations")
    @GetMapping("/{username}/reservations")
    public ResponseEntity<List<Accommodation>> findAllReservations(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        List<Accommodation> accommodations = userApplicationService.findAllReservations(user.getUsername());
        return accommodations.isEmpty() ?
                ResponseEntity.notFound().build() : ResponseEntity.ok(accommodations);
    }

    @Operation(summary = "Book all reservations", description = "Books all reservations for the user")
    @ApiResponse(responseCode = "200", description = "All reservations booked")
    @PostMapping("/{username}/bookAll")
    public ResponseEntity<DisplayUserDto> bookAllReservations(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return userApplicationService.bookAllReservations(user.getUsername())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get all users", description = "Retrieves a list of all users without reservations.")
    @ApiResponse(responseCode = "200", description = "List fetched successfully")
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userApplicationService.getAllUsers();
        return users.isEmpty() ?
                ResponseEntity.notFound().build() : ResponseEntity.ok(users);
    }
}
