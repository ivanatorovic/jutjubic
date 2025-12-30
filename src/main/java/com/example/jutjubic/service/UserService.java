package com.example.jutjubic.service;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.jutjubic.dto.RegisterRequest;
import com.example.jutjubic.model.User;
import com.example.jutjubic.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ================== READ ==================

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("Korisnik sa datim email-om ne postoji"));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Korisnik ne postoji"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    // ================== REGISTER ==================

    public User register(RegisterRequest req) {

        // ===== VALIDACIJE =====
        if (req.email == null || req.email.isBlank()) {
            throw new RuntimeException("Email je obavezan");
        }

        if (req.username == null || req.username.isBlank()) {
            throw new RuntimeException("Korisničko ime je obavezno");
        }


        if (!req.password.equals(req.confirmPassword)) {
            throw new RuntimeException("Lozinke se ne poklapaju");
        }

        if (userRepository.existsByEmail(req.email)) {
            throw new RuntimeException("Email je već zauzet");
        }

        if (userRepository.existsByUsername(req.username)) {
            throw new RuntimeException("Korisničko ime je već zauzeto");
        }

        // ===== KREIRANJE KORISNIKA =====
        User user = new User();
        user.setEmail(req.email);
        user.setUsername(req.username);
        user.setPassword(passwordEncoder.encode(req.password));
        user.setFirstName(req.firstName);
        user.setLastName(req.lastName);
        user.setAddress(req.address);

        // bez email aktivacije (za 3.2)
        user.setEnabled(true);
        user.setActivationToken(null);

        return userRepository.save(user);
    }
}
