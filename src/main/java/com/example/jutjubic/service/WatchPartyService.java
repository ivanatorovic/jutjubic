package com.example.jutjubic.service;

import com.example.jutjubic.model.WatchPartyRoom;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class WatchPartyService {

    private final Map<String, WatchPartyRoom> rooms = new ConcurrentHashMap<>();

    public WatchPartyRoom createRoom(Long hostUserId, String hostUsername, boolean isPublic) {
        String id = generateRoomId();
        WatchPartyRoom room = new WatchPartyRoom(id, hostUserId, hostUsername, isPublic);
        rooms.put(id, room);
        return room;
    }

    public void clearCurrentVideo(String roomId, Long userId) {
        WatchPartyRoom room = getRoom(roomId);
        if (!room.getHostUserId().equals(userId)) {
            throw new RuntimeException("Samo host može da završi gledanje.");
        }
        room.setCurrentVideoId(null);
    }


    public WatchPartyRoom getRoom(String roomId) {
        WatchPartyRoom r = rooms.get(roomId);
        if (r == null) throw new IllegalArgumentException("Watch Party room not found: " + roomId);
        return r;
    }

    public List<WatchPartyRoom> listPublicRooms(Long currentUserId) {
        return rooms.values().stream()
                .filter(r ->
                        r.isPublic()
                                || r.getHostUserId().equals(currentUserId)
                )
                .toList();
    }


    public void addMember(String roomId, Long userId) {
        getRoom(roomId).addMember(userId);
    }

    public boolean addVideo(String roomId, Long userId, Long videoId) {
        WatchPartyRoom room = getRoom(roomId);
        if (!room.getHostUserId().equals(userId)) {
            throw new RuntimeException("Samo host moze dodati video u sobu.");
        }
        return room.addVideo(videoId);
    }


    public void setCurrentVideo(String roomId, Long userId, Long videoId) {
        WatchPartyRoom room = getRoom(roomId);
        if (!room.getHostUserId().equals(userId)) {
            throw new RuntimeException("Samo host moze zapoceti video.");
        }
        room.setCurrentVideoId(videoId);
    }

    private String generateRoomId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    public List<WatchPartyRoom> listRoomsByHost(Long hostUserId) {
        return rooms.values().stream()
                .filter(r -> r.getHostUserId().equals(hostUserId))
                .toList();
    }

}
