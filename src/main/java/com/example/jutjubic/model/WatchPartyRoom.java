package com.example.jutjubic.model;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class WatchPartyRoom {

    private final String roomId;
    private final Long hostUserId;
    private final String hostUsername;
    private final boolean isPublic;

    private final Set<Long> members = ConcurrentHashMap.newKeySet();
    private final List<Long> videos = Collections.synchronizedList(new ArrayList<>());

    private volatile Long currentVideoId;

    public WatchPartyRoom(String roomId, Long hostUserId, String hostUsername, boolean isPublic) {
        this.roomId = roomId;
        this.hostUserId = hostUserId;
        this.hostUsername = hostUsername;
        this.isPublic = isPublic;
        this.members.add(hostUserId);
    }

    public String getRoomId() { return roomId; }
    public Long getHostUserId() { return hostUserId; }
    public String getHostUsername() { return hostUsername; }
    public boolean isPublic() { return isPublic; }

    public Long getCurrentVideoId() { return currentVideoId; }
    public void setCurrentVideoId(Long currentVideoId) { this.currentVideoId = currentVideoId; }

    public void addMember(Long userId) { members.add(userId); }
    public void removeMember(Long userId) { members.remove(userId); }

    public boolean addVideo(Long videoId) {
        synchronized (videos) {
            if (videos.contains(videoId)) return false;
            videos.add(videoId);
            return true;
        }
    }

    public Set<Long> getMembersSnapshot() {
        synchronized (members) {
            return Set.copyOf(members);
        }
    }

    public List<Long> getVideosSnapshot() {
        synchronized (videos) {
            return List.copyOf(videos);
        }
    }

    public int getMemberCount() { return members.size(); }

    public int getVideoCount() {
        synchronized (videos) {
            return videos.size();
        }
    }

}
