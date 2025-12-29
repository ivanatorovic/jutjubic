package com.example.jutjubic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")   // izbegavamo ime "user" jer je rezervisana reč u PostgreSQL-u
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // prijava ide preko email + password
    @Column(nullable = false, unique = true)
    private String email;

    // korisničko ime koje se prikazuje na profilu
    @Column(nullable = false, unique = true)
    private String username;

    // OVDE ćeš čuvati HASH lozinke, ne plain tekst
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, name = "first_name")
    private String firstName;

    @Column(nullable = false, name = "last_name")
    private String lastName;

    @Column(nullable = false)
    private String address;

    // nalog nije aktivan dok ne klikne na mail link
    @Column(nullable = false)
    private boolean enabled = false;

    // token koji šalješ u mailu za aktivaciju (može biti UUID)
    @Column(name = "activation_token")
    private String activationToken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    // ===== konstruktori =====

    public User() {
    }

    public User(String email, String username, String password,
                String firstName, String lastName, String address) {
        this.email = email;
        this.username = username;
        this.password = password;  // u praksi ovde već dolazi hash
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    // ===== getteri i setteri =====

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password; // kasnije ovde staviš hash
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getActivationToken() {
        return activationToken;
    }

    public void setActivationToken(String activationToken) {
        this.activationToken = activationToken;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
