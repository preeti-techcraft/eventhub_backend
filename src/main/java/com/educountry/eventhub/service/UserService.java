package com.educountry.eventhub.service;

import com.educountry.eventhub.model.User;
import com.educountry.eventhub.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(id);
    }

    public User updateUser(Long id, User updates) {
        User existing = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        existing.setName(updates.getName());
        existing.setEmail(updates.getEmail());
        if (updates.getPassword() != null && !updates.getPassword().isEmpty()) {
            existing.setPassword(updates.getPassword()); // In Phase 5 this will be hashed
        }
        existing.setRole(updates.getRole());
        existing.setPhone(updates.getPhone());
        if (updates.getProfileImage() != null) {
            existing.setProfileImage(updates.getProfileImage());
        }
        return userRepository.save(existing);
    }
}
