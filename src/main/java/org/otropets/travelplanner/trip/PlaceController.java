package org.otropets.travelplanner.trip;


import jakarta.validation.Valid;
import org.otropets.travelplanner.trip.dto.CreatePlaceRequest;
import org.otropets.travelplanner.trip.dto.PlaceResponse;
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
    public ResponseEntity<PlaceResponse> addPlace(@PathVariable Long tripId, @Valid @RequestBody CreatePlaceRequest request) {
        PlaceResponse response = service.addPlace(tripId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PlaceResponse>> getPlaces(@PathVariable Long tripId) {
        List<PlaceResponse> response = service.getPlaces(tripId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlace(@PathVariable Long id) {
        service.deletePlace(id);
        return ResponseEntity.noContent().build();
    }


}
