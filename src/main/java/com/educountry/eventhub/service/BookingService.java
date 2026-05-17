package com.educountry.eventhub.service;

import com.educountry.eventhub.dto.BookingRequest;
import com.educountry.eventhub.model.Booking;
import com.educountry.eventhub.model.Event;
import com.educountry.eventhub.model.User;
import com.educountry.eventhub.repository.BookingRepository;
import com.educountry.eventhub.repository.EventRepository;
import com.educountry.eventhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Booking bookSpot(BookingRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if user already booked
        if (bookingRepository.existsByEvent_IdAndUser_IdAndStatusNot(event.getId(), user.getId(), "CANCELLED")) {
            throw new RuntimeException("You have already booked a spot for this event");
        }

        // Prevent Overbooking (This logic is protected by @Transactional)
        long currentBookings = bookingRepository.findByEvent_IdAndStatusNot(event.getId(), "CANCELLED").size();
        if (currentBookings >= event.getCapacity()) {
            throw new RuntimeException("Event is fully booked!");
        }

        Booking booking = new Booking(event, user, "BOOKED", LocalDateTime.now());
        return bookingRepository.save(booking);
    }

    public List<Booking> getUserBookings(Long userId) {
        return bookingRepository.findByUser_Id(userId);
    }

    public List<Booking> getEventAttendees(Long eventId) {
        return bookingRepository.findByEvent_Id(eventId);
    }

    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        
        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }
}
