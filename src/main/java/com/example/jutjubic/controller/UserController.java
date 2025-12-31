package com.example.jutjubic.controller;

import com.example.jutjubic.dto.UserPublicDto;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.User;
import com.example.jutjubic.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET /users/{id} -> prikaz korisnika (javni profil)
    @GetMapping("/{id}")
    public UserPublicDto getById(@PathVariable Long id) {
        User u = userService.findById(id);
        return DtoMapper.toUserPublicDto(u);
    }

}
