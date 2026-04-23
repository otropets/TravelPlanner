package org.otropets.travelplanner.trip.controller;


import jakarta.validation.Valid;
import org.otropets.travelplanner.trip.dto.CreatePlaceRequest;
import org.otropets.travelplanner.trip.dto.PlaceResponse;
import org.otropets.travelplanner.trip.service.PlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips/{trip_id}/places")
public class PlaceController {

    private final PlaceService service;

    public PlaceController(PlaceService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlaceResponse> getPlace(@PathVariable Long id){
        PlaceResponse response = service.getPlace(id);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<PlaceResponse> addPlace(@PathVariable Long trip_id, @Valid @RequestBody CreatePlaceRequest request) {
        PlaceResponse response = service.addPlace(trip_id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PlaceResponse>> getPlaces(@PathVariable Long trip_id) {
        List<PlaceResponse> response = service.getPlaces(trip_id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        service.deletePlace(id);
        return ResponseEntity.noContent().build();
    }


}
