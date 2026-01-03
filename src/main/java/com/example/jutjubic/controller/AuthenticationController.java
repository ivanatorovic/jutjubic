package com.example.jutjubic.controller;

import com.example.jutjubic.dto.JwtAuthenticationRequest;
import com.example.jutjubic.dto.RegisterRequest;
import com.example.jutjubic.dto.UserTokenState;
import com.example.jutjubic.service.UserService;
import com.example.jutjubic.util.TokenUtils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final TokenUtils tokenUtils;
    private final UserService userService;

    public AuthenticationController(AuthenticationManager authenticationManager,
                                    TokenUtils tokenUtils,
                                    UserService userService) {
        this.authenticationManager = authenticationManager;
        this.tokenUtils = tokenUtils;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<UserTokenState> login(@RequestBody JwtAuthenticationRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            String email = authentication.getName();
            String jwt = tokenUtils.generateToken(email);
            long expiresIn = tokenUtils.getExpiredIn();

            return ResponseEntity.ok(new UserTokenState(jwt, expiresIn));

        } catch (DisabledException ex) {
            // ✅ nalog postoji ali nije aktiviran
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Nalog nije aktiviran. Proverite mejl i kliknite na aktivacioni link ili se registrujte ponovo."
            );

        } catch (BadCredentialsException ex) {
            // ✅ pogrešan email ili lozinka
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Ne postoji korisnik sa datim emailom i lozinkom."
            );
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok(
                "Proverite mejl i kliknite na link u poruci."
        );
    }

    @GetMapping("/activate")
    public ResponseEntity<?> activate(@RequestParam("token") String token) {
        userService.activateAccount(token);
        return ResponseEntity.ok("Nalog je uspešno aktiviran. Sada možete da se prijavite.");
    }
}
