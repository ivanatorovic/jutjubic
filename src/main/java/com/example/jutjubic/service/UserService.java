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

import com.example.jutjubic.dto.RegisterRequest;
import com.example.jutjubic.model.User;
import com.example.jutjubic.repository.UserRepository;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommentRepository commentRepository;
    private final VideoLikeRepository videoLikeRepository;
    private final VideoRepository videoRepository;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder, CommentRepository commentRepository, VideoLikeRepository videoLikeRepository, VideoRepository videoRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.commentRepository = commentRepository;
        this.videoLikeRepository = videoLikeRepository;
        this.videoRepository = videoRepository;
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
