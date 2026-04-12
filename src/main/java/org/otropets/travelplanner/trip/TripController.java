package org.otropets.travelplanner.trip;

import jakarta.validation.Valid;
import org.otropets.travelplanner.trip.dto.CreateTripRequest;
import org.otropets.travelplanner.trip.dto.TripResponse;
import org.otropets.travelplanner.trip.dto.UpdateTripRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {

    private final TripService tripService;
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody CreateTripRequest createTripRequest){
        TripResponse response = tripService.createTrip(createTripRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable Long id){
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{id}")
    public ResponseEntity<TripResponse> updateTrip(@Valid @RequestBody UpdateTripRequest updateTripRequest, @PathVariable Long id){
        TripResponse response = tripService.updateTrip(id, updateTripRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TripResponse> getTrip(@PathVariable Long id){
        TripResponse response = tripService.getTrip(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<TripResponse>> getUserTrips(){
        List<TripResponse> trips = tripService.getUserTrips();
        return ResponseEntity.ok(trips);
    }

}
