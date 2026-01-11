package com.example.jutjubic.controller;

import com.example.jutjubic.dto.UserPublicDto;
import com.example.jutjubic.dto.VideoPublicDto;
import com.example.jutjubic.mapper.DtoMapper;
import com.example.jutjubic.model.User;
import com.example.jutjubic.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserPublicDto getById(@PathVariable Long id) {
        User u = userService.findById(id);
        return DtoMapper.toUserPublicDto(u);
    }

    @GetMapping("/{id}/videos")
    public List<VideoPublicDto> getUserVideos(@PathVariable Long id) {
        return userService.getUserVideos(id);
    }

}
