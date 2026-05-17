package com.educountry.eventhub.repository;

import com.educountry.eventhub.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Id(Long userId);
    List<Booking> findByEvent_Id(Long eventId);
    List<Booking> findByEvent_IdAndStatusNot(Long eventId, String status);
    boolean existsByEvent_IdAndUser_IdAndStatusNot(Long eventId, Long userId, String status);
}
