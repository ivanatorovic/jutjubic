package com.example.jutjubic.controller;

import com.example.jutjubic.model.WatchPartyRoom;
import com.example.jutjubic.repository.UserRepository;
import com.example.jutjubic.service.WatchPartyService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static com.example.jutjubic.dto.WatchPartyDtos.*;

@RestController
@RequestMapping("/api/watch-party")
public class WatchPartyController {

    private final WatchPartyService watchPartyService;
    private final UserRepository userRepository;

    public WatchPartyController(WatchPartyService watchPartyService, UserRepository userRepository) {
        this.watchPartyService = watchPartyService;
        this.userRepository = userRepository;
    }

    @PostMapping("/rooms")
    public RoomRes createRoom(@RequestBody CreateRoomReq req) {
        var user = currentUser();
        WatchPartyRoom room = watchPartyService.createRoom(user.getId(), user.getUsername(), req.isPublic());

        return toRoomRes(room);
    }

    @GetMapping("/rooms")
    public List<RoomRes> listPublicRooms() {
        var user = currentUser();

        return watchPartyService.listPublicRooms(user.getId()).stream()
                .map(this::toRoomRes)
                .toList();
    }


    @GetMapping("/my-rooms")
    public List<RoomRes> myRooms() {
        var user = currentUser();
        return watchPartyService.listRoomsByHost(user.getId()).stream()
                .map(this::toRoomRes)
                .toList();
    }

    @GetMapping("/rooms/{roomId}")
    public RoomDetailsRes getRoom(@PathVariable String roomId) {
        WatchPartyRoom room = watchPartyService.getRoom(roomId);

        List<MemberDto> members = room.getMembersSnapshot().stream()
                .map(uid -> new MemberDto(uid, resolveUsername(uid)))
                .toList();

        return new RoomDetailsRes(
                room.getRoomId(),
                room.getHostUserId(),
                room.getHostUsername(),
                room.isPublic(),
                room.getMemberCount(),
                room.getVideoCount(),
                room.getVideosSnapshot(),
                members,
                room.getCurrentVideoId()
        );
    }

    @PostMapping("/rooms/{roomId}/videos")
    public RoomRes addVideoToRoom(@PathVariable String roomId, @RequestBody AddVideoReq req) {
        var user = currentUser();
        boolean added = watchPartyService.addVideo(roomId, user.getId(), req.videoId());

        if (!added) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Video je vec u ovoj sobi.");
        }

        WatchPartyRoom room = watchPartyService.getRoom(roomId);
        return toRoomRes(room);
    }

    private RoomRes toRoomRes(WatchPartyRoom room) {
        return new RoomRes(
                room.getRoomId(),
                room.getHostUserId(),
                room.getHostUsername(),
                room.isPublic(),
                room.getMemberCount(),
                room.getVideoCount()
        );
    }

    private com.example.jutjubic.model.User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email).orElseThrow();
    }

    private String resolveUsername(Long userId) {
        return userRepository.findById(userId)
                .map(u -> {
                    if (u.getUsername() != null && !u.getUsername().isBlank()) return u.getUsername();
                    return u.getEmail();
                })
                .orElse("user#" + userId);
    }
}
