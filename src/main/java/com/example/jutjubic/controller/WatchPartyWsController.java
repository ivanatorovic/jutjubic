package com.example.jutjubic.controller;

import com.example.jutjubic.model.WatchPartyRoom;
import com.example.jutjubic.repository.UserRepository;
import com.example.jutjubic.service.WatchPartyService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.UUID;

import static com.example.jutjubic.dto.WatchPartyWsDtos.*;

@Controller
public class WatchPartyWsController {

    private final WatchPartyService watchPartyService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserRepository userRepository;

    public WatchPartyWsController(WatchPartyService watchPartyService,
                                  SimpMessagingTemplate messagingTemplate,
                                  UserRepository userRepository) {
        this.watchPartyService = watchPartyService;
        this.messagingTemplate = messagingTemplate;
        this.userRepository = userRepository;
    }

    @MessageMapping("/watch-party/join")
    public void join(JoinRoomMsg msg, Principal principal) {

        System.out.println("PRINCIPAL = " + principal);
        System.out.println("NAME = " + (principal != null ? principal.getName() : null));

        if (principal == null) {
            return;
        }

        Long userId = currentUserId(principal);
        watchPartyService.addMember(msg.roomId(), userId);

        WatchPartyRoom updated = watchPartyService.getRoom(msg.roomId());
        broadcastRoomState(updated);
    }


    @MessageMapping("/watch-party/start")
    public void start(StartVideoMsg msg, Principal principal) {
        Long userId = currentUserId(principal);

        try {
            watchPartyService.setCurrentVideo(msg.roomId(), userId, msg.videoId());
        } catch (RuntimeException ex) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/watch-party/errors",
                    new ErrorMsg(ex.getMessage())
            );
            return;
        }

        WatchPartyRoom room = watchPartyService.getRoom(msg.roomId());

        String eventId = UUID.randomUUID().toString();

        messagingTemplate.convertAndSend(
                "/topic/watch-party/" + room.getRoomId() + "/events",
                new VideoStartedMsg(room.getRoomId(), msg.videoId(), eventId)
        );

        broadcastRoomState(room);
    }

    @MessageMapping("/watch-party/state")
    public void state(JoinRoomMsg msg, Principal principal) {
        System.out.println("[WS STATE HANDLER] called, roomId=" + msg.roomId());
        WatchPartyRoom room = watchPartyService.getRoom(msg.roomId());
        broadcastRoomState(room);
    }

    @MessageMapping("/watch-party/stop")
    public void stop(StopVideoMsg msg, Principal principal) {
        Long userId = currentUserId(principal);

        try {
            watchPartyService.clearCurrentVideo(msg.roomId(), userId);
        } catch (RuntimeException ex) {
            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/watch-party/errors",
                    new ErrorMsg(ex.getMessage())
            );
            return;
        }

        WatchPartyRoom room = watchPartyService.getRoom(msg.roomId());
        broadcastRoomState(room);
    }


    private Long currentUserId(Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new RuntimeException("WS unauthorized: missing Principal (token nije prosleđen u CONNECT)");
        }
        String email = principal.getName();
        return userRepository.findByEmail(email).orElseThrow().getId();
    }

    private void broadcastRoomState(WatchPartyRoom room) {
        var members = room.getMembersSnapshot().stream()
                .map(uid -> new MemberDto(uid, resolveUsername(uid), resolveEmail(uid)))
                .toList();

        messagingTemplate.convertAndSend(
                "/topic/watch-party/" + room.getRoomId() + "/state",
                new RoomStateMsg(
                        room.getRoomId(),
                        room.getHostUserId(),
                        room.getHostUsername(),
                        members,
                        room.getVideosSnapshot(),
                        room.getCurrentVideoId()
                )
        );
    }

    private String resolveUsername(Long userId) {
        return userRepository.findById(userId)
                .map(u -> (u.getUsername() != null && !u.getUsername().isBlank()) ? u.getUsername() : u.getEmail())
                .orElse("user#" + userId);
    }

    private String resolveEmail(Long userId) {
        return userRepository.findById(userId)
                .map(u -> u.getEmail())
                .orElse(null);
    }
}
