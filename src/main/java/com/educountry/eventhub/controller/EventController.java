package com.educountry.eventhub.controller;

import com.educountry.eventhub.dto.ApiResponse;
import com.educountry.eventhub.dto.EventRequest;
import com.educountry.eventhub.model.Event;
import com.educountry.eventhub.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    @Autowired
    private EventService eventService;

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        // Keeping it simple: returning the list directly for existing frontend compatibility
        // In a strict production environment, we'd wrap this in ApiResponse too.
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(@Valid @RequestBody EventRequest request) {
        Event createdEvent = eventService.createEvent(request);
        return ResponseEntity.ok(createdEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Event deleted successfully", null));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateEventStatus(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String status = payload.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Status is required", null));
        }
        Event updatedEvent = eventService.updateEventStatus(id, status);
        return ResponseEntity.ok(updatedEvent);
    }

    @PostMapping("/{id}/gallery")
    public ResponseEntity<?> addGalleryImage(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String imageUrl = payload.get("imageUrl");
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Image URL is required", null));
        }
        Event updatedEvent = eventService.addGalleryImage(id, imageUrl);
        return ResponseEntity.ok(new ApiResponse<>(true, "Image added to gallery", updatedEvent));
    }

    @DeleteMapping("/{id}/gallery")
    public ResponseEntity<?> removeGalleryImage(@PathVariable Long id, @RequestBody Map<String, String> payload) {
        String imageUrl = payload.get("imageUrl");
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Image URL is required", null));
        }
        Event updatedEvent = eventService.removeGalleryImage(id, imageUrl);
        return ResponseEntity.ok(new ApiResponse<>(true, "Image removed from gallery", updatedEvent));
    }

    @PostMapping("/{id}/gallery/upload")
    public ResponseEntity<?> uploadGalleryImage(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "File is empty", null));
        }

        try {
            // Ensure uploads directory exists
            String uploadDir = "uploads/gallery/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Generate a unique file name to avoid collisions
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
            
            Path filePath = Paths.get(uploadDir + uniqueFileName);
            Files.write(filePath, file.getBytes());

            // The URL path will be accessible via our WebConfig resource handler mapped to /uploads/**
            String imageUrl = "http://localhost:8080/uploads/gallery/" + uniqueFileName;
            
            Event updatedEvent = eventService.addGalleryImage(id, imageUrl);
            return ResponseEntity.ok(new ApiResponse<>(true, "Image uploaded successfully", updatedEvent));

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new ApiResponse<>(false, "Failed to upload image", null));
        }
    }
}
