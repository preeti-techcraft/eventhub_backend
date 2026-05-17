package com.educountry.eventhub.controller;

import com.educountry.eventhub.dto.ApiResponse;
import com.educountry.eventhub.dto.BookingRequest;
import com.educountry.eventhub.model.Booking;
import com.educountry.eventhub.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping
    public ResponseEntity<ApiResponse<Booking>> bookSpot(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.bookSpot(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking successful", booking));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Booking cancelled", null));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Booking>> getUserBookings(@PathVariable Long userId) {
        return ResponseEntity.ok(bookingService.getUserBookings(userId));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<Booking>> getEventAttendees(@PathVariable Long eventId) {
        return ResponseEntity.ok(bookingService.getEventAttendees(eventId));
    }
}
