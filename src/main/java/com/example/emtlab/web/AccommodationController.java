package com.example.emtlab.web;

import com.example.emtlab.dto.CreateAccommodationDto;
import com.example.emtlab.dto.DisplayAccommodationDto;
import com.example.emtlab.model.projections.AccommodationProjection;
import com.example.emtlab.model.views.AccommodationsPerHostView;
import com.example.emtlab.service.application.AccommodationApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/accommodations")
@Tag(name = "Accommodation API", description = "Endpoints for managing accommodations.")
public class AccommodationController {

    private final AccommodationApplicationService accommodationApplicationService;

    public AccommodationController(AccommodationApplicationService accommodationApplicationService) {
        this.accommodationApplicationService = accommodationApplicationService;
    }

    @Operation(summary = "Get all accommodations", description = "Retrieves a list of all available accommodations.")
    @GetMapping
    public List<DisplayAccommodationDto> findAll() {
        return accommodationApplicationService.findAll();
    }

    @Operation(summary = "Get accommodation by ID", description = "Finds an accommodation by its ID.")
    @GetMapping("/{id}")
    public ResponseEntity<DisplayAccommodationDto> findById(@PathVariable Long id) {
        return accommodationApplicationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Add a new accommodation",
            description = "Creates a new accommodation based on the given AccommodationDto."
    )
    @PostMapping("/add")
    public ResponseEntity<DisplayAccommodationDto> save(@RequestBody CreateAccommodationDto createAccommodationDto) {
        return accommodationApplicationService.save(createAccommodationDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update an existing accommodation",
            description = "Updates an accommodation by ID using AccommodationDto."
    )
    @PutMapping("/edit/{id}")
    public ResponseEntity<DisplayAccommodationDto> update(@PathVariable Long id, @RequestBody CreateAccommodationDto createAccommodationDto) {
        return accommodationApplicationService.update(id, createAccommodationDto)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete an accommodation", description = "Deletes an accommodation by its ID.")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (accommodationApplicationService.findById(id).isPresent()) {
            accommodationApplicationService.deleteById(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Reserve an accommodation", description = "Reserves an accommodation for the user.")
    @PostMapping("/{id}/reserve")
    public ResponseEntity<DisplayAccommodationDto> reserveAccommodation(@PathVariable Long id, @RequestBody String username) {
        return accommodationApplicationService.reserve(id, username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Remove reservation", description = "Removes the reservation of an accommodation.")
    @PostMapping("/{id}/removeReservation")
    public ResponseEntity<DisplayAccommodationDto> removeReservation(@PathVariable Long id) {
        return accommodationApplicationService.removeReservation(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Book an accommodation", description = "Books an accommodation for the user.")
    @PostMapping("/{id}/book")
    public ResponseEntity<DisplayAccommodationDto> bookAccommodation(@PathVariable Long id, @RequestBody String username) {
        return accommodationApplicationService.book(id, username)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Complete stay", description = "Completes the stay for an accommodation.")
    @PostMapping("/{id}/completeStay")
    public ResponseEntity<DisplayAccommodationDto> completeStay(@PathVariable Long id) {
        return accommodationApplicationService.completeStay(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get count for every category", description = "Retrieves a list of all categories with it's count.")
    @GetMapping("/statistics")
    public List<AccommodationProjection> findStatistics() {
        return accommodationApplicationService.accommodationStatistics();
    }

    @Operation(summary = "Get the number of accommodations for every host", description = "Retrieves a list of hosts with the number of his accommodations.")
    @GetMapping("/by-host")
    public List<AccommodationsPerHostView> findAccommodationsByHost() {
        return accommodationApplicationService.getAccommodationsByHost();
    }

    @Operation(summary = "Force refresh materialized view", description = "Refreshes the materialized view.")
    @GetMapping("/force-refresh")
    public ResponseEntity<Void> refreshMaterializedView() {
        accommodationApplicationService.refreshMaterializedView();
        return ResponseEntity.ok().build();
    }
}
