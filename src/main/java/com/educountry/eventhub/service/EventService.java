package com.educountry.eventhub.service;

import com.educountry.eventhub.dto.EventRequest;
import com.educountry.eventhub.model.Event;
import com.educountry.eventhub.model.User;
import com.educountry.eventhub.repository.EventRepository;
import com.educountry.eventhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @Transactional
    public Event createEvent(EventRequest request) {
        User organizer = userRepository.findById(request.getOrganizerId())
                .orElseThrow(() -> new RuntimeException("Organizer not found"));

        Event event = new Event(
                request.getTitle(),
                request.getDesc(),
                request.getDate(),
                request.getTime(),
                request.getLocation(),
                request.getCapacity(),
                request.getIsFree(),
                request.getPrice(),
                organizer
        );
        
        return eventRepository.save(event);
    }

    @Transactional
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Event not found");
        }
        eventRepository.deleteById(id);
    }

    @Transactional
    public Event updateEventStatus(Long id, String status) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        event.setStatus(status);
        return eventRepository.save(event);
    }

    @Transactional
    public Event addGalleryImage(Long eventId, String imageUrl) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        event.getGalleryImages().add(imageUrl);
        return eventRepository.save(event);
    }

    @Transactional
    public Event removeGalleryImage(Long eventId, String imageUrl) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        
        event.getGalleryImages().remove(imageUrl);
        return eventRepository.save(event);
    }
}
