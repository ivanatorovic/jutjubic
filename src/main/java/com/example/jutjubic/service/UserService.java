package com.example.jutjubic.service;

import java.util.List;

import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.Video;
import com.example.jutjubic.repository.CommentRepository;
import com.example.jutjubic.repository.VideoLikeRepository;
import com.example.jutjubic.repository.VideoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


import com.example.jutjubic.dto.RegisterRequest;
import com.example.jutjubic.model.User;
import com.example.jutjubic.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final VideoRepository videoRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final CommentRepository commentRepository;

    @Value("${app.activation.url}")
    private String activationUrl;


    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService, VideoRepository videoRepository, VideoLikeRepository videoLikeRepository, CommentRepository commentRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.videoRepository = videoRepository;
        this.videoLikeRepository = videoLikeRepository;
        this.commentRepository = commentRepository;
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
    @Transactional
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

        // ✅ SA EMAIL AKTIVACIJOM
        user.setEnabled(false);
        String token = UUID.randomUUID().toString();
        user.setActivationToken(token);

        // 1) snimi u bazu
        User saved = userRepository.save(user);

        // 2) pošalji mail
        String link = activationUrl + "?token=" + token;
        emailService.sendActivationEmail(saved.getEmail(), link);

        return saved;
    }

    @Transactional
    public void activateAccount(String token) {
        User user = userRepository.findByActivationToken(token)
                .orElseThrow(() -> new RuntimeException("Nevažeći aktivacioni token"));

        if (user.isEnabled()) {
            return; // već aktiviran (može i da baciš izuzetak ako želiš)
        }

        user.setEnabled(true);
        user.setActivationToken(null);
        userRepository.save(user);
    }

    public List<VideoPublicDto> getUserVideos(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + userId);
        }

        List<Video> videos = videoRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return videos.stream()
                .map(v -> DtoMapper.toVideoPublicDto(
                        v,
                        videoLikeRepository.countByVideoId(v.getId()),
                        commentRepository.countByVideoId(v.getId())
                ))
                .toList();
    }

}