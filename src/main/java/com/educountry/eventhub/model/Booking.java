package com.educountry.eventhub.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Booking Entity represents a record in the 'bookings' table.
 */
@Entity
@Table(name = "bookings", indexes = {
    @Index(name = "idx_booking_status", columnList = "status")
})
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MANY Bookings can belong to ONE Event
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "bookings", "organizer"})
    private Event event;

    // MANY Bookings can belong to ONE User (Attendee)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "bookings", "organizedEvents", "password"})
    private User user;

    @Column(nullable = false)
    private String status; // BOOKED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime bookingDate;

    public Booking() {
    }

    public Booking(Event event, User user, String status, LocalDateTime bookingDate) {
        this.event = event;
        this.user = user;
        this.status = status;
        this.bookingDate = bookingDate;
    }

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }
}
