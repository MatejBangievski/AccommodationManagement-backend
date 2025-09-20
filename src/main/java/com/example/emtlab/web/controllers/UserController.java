package com.example.emtlab.web.controllers;

import com.example.emtlab.dto.*;
import com.example.emtlab.model.domain.Accommodation;
import com.example.emtlab.model.domain.User;
import com.example.emtlab.model.exceptions.InvalidArgumentsException;
import com.example.emtlab.model.exceptions.InvalidUserCredentialsException;
import com.example.emtlab.model.exceptions.PasswordsDoNotMatchException;
import com.example.emtlab.model.exceptions.UsernameAlreadyExistsException;
import com.example.emtlab.service.application.UserApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
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
            value = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input or passwords do not match"),
                    @ApiResponse(responseCode = "409", description = "Username already exists")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<DisplayUserDto> register(@RequestBody CreateUserDto createUserDto) {
        try {
            return userApplicationService.register(createUserDto)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (InvalidArgumentsException | PasswordsDoNotMatchException e) {
            return ResponseEntity.badRequest().build(); // 400
        } catch (UsernameAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); // 409
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

    @Operation(summary = "Find user by username", description = "Returns user details by username")
    @ApiResponse(responseCode = "200", description = "User found")
    @GetMapping("/{username}")
    public ResponseEntity<DisplayUserDto> findByUsername(@PathVariable String username) {
        return userApplicationService.findByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Reserve accommodation", description = "Reserves an accommodation for the user")
    @ApiResponse(responseCode = "200", description = "Accommodation reserved")
    @PostMapping("/{username}/reserve/{accommodationId}")
    //Authentication authentication
//        User user = (User) authentication.getPrincipal();
    public ResponseEntity<DisplayUserDto> reserveAccommodation(@PathVariable String username, @PathVariable Long accommodationId) {
        return userApplicationService.reserveAccommodation(username, accommodationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cancel accommodation reservation", description = "Cancels an accommodation reservation for the user")
    @ApiResponse(responseCode = "200", description = "Accommodation reservation canceled")
    @PostMapping("/{username}/cancel/{accommodationId}")
    //Authentication authentication
    //    User user = (User) authentication.getPrincipal();
    public ResponseEntity<DisplayUserDto> cancelAccommodation(@PathVariable String username, @PathVariable Long accommodationId) {
        return userApplicationService.cancelAccommodation(username, accommodationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Book accommodation", description = "Books a reserved accommodation for the user")
    @ApiResponse(responseCode = "200", description = "Accommodation booked")
    @PostMapping("/{username}/book/{accommodationId}")
//    Authentication authentication
//        User user = (User) authentication.getPrincipal();
    public ResponseEntity<DisplayUserDto> bookAccommodation(@PathVariable String username, @PathVariable Long accommodationId) {
        return userApplicationService.bookAccommodation(username, accommodationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Find all reservations", description = "Returns a list of all reservations for the user")
    @ApiResponse(responseCode = "200", description = "List of reservations")
    @GetMapping("/{username}/reservations")
//    public ResponseEntity<List<Accommodation>> findAllReservations(Authentication authentication) {
//        User user = (User) authentication.getPrincipal();
    public ResponseEntity<List<Accommodation>> findAllReservations(@PathVariable String username) {
        List<Accommodation> accommodations = userApplicationService.findAllReservations(username);
        return accommodations.isEmpty() ?
                ResponseEntity.notFound().build() : ResponseEntity.ok(accommodations);
    }

    @Operation(summary = "Find where a user is staying by username", description = "Returns accommodation details by username")
    @ApiResponses(
            value = {@ApiResponse(
                    responseCode = "200",
                    description = "Accommodation found"
            ), @ApiResponse(responseCode = "404", description = "Invalid username or he's not staying anywhere")}
    )
    @GetMapping("/findAllBookings/{username}")
    public ResponseEntity<List<DisplayAccommodationDto>> findAllBookings(@PathVariable String username) {
        List<DisplayAccommodationDto> accommodationDtos = userApplicationService.findAllBookings(username);

        return accommodationDtos.isEmpty() ?
                ResponseEntity.notFound().build() : ResponseEntity.ok(accommodationDtos);
    }

    @Operation(summary = "Book all reservations", description = "Books all reservations for the user")
    @ApiResponse(responseCode = "200", description = "All reservations booked")
    @PostMapping("/{username}/bookAll")
//        public ResponseEntity<DisplayUserDto> bookAllReservations(Authentication authentication) {
//            User user = (User) authentication.getPrincipal();
    public ResponseEntity<DisplayUserDto> bookAllReservations(@PathVariable String username) {
        return userApplicationService.bookAllReservations(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Reserve all free accommodations", description = "Reserves all free accommodations for the user")
    @ApiResponse(responseCode = "200", description = "All free accommodations reserved")
    @PostMapping("/{username}/reserveAll")
    public ResponseEntity<DisplayUserDto> reserveAllAccommodations(@PathVariable String username) {
        return userApplicationService.reserveAllAccommodations(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "A user completes his stay where he's staying by using the accommodation's id", description = "Returns user details")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Stay completed"),
                    @ApiResponse(responseCode = "404", description = "Invalid accommodation id or no user is staying there")}
    )
    @GetMapping("/completeStay/{accommodationId}")
    public ResponseEntity<DisplayUserDto> completeStay(@PathVariable Long accommodationId) {
        return userApplicationService.completeStay(accommodationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Cancels all reservations for the user", description = "Returns user details")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Cancelled all reservations"),
                    @ApiResponse(responseCode = "404", description = "Invalid username or he has no resevations")}
    )
    @GetMapping("/cancelAllReservations/{username}")
    public ResponseEntity<DisplayUserDto> cancelAllReservations(@PathVariable String username) {
        return userApplicationService.cancelAllReservations(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Completes all bookings for the user", description = "Returns user details")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "Completed all bookings"),
                    @ApiResponse(responseCode = "404", description = "Invalid username or he hasn't booked anything")}
    )
    @GetMapping("/completeStayForAllBookings/{username}")
    public ResponseEntity<DisplayUserDto> completeStayForAllBookings(@PathVariable String username) {
        return userApplicationService.completeStayForAllBookings(username)
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
