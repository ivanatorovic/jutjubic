package com.example.jutjubic.dto;

import java.util.List;
import java.util.Set;

public final class WatchPartyWsDtos {
    private WatchPartyWsDtos() {}

    public record JoinRoomMsg(String roomId) {}
    public record StartVideoMsg(String roomId, Long videoId) {}
    public record ErrorMsg(String message) {}
    public record MemberDto(Long userId, String username, String email) {}


    public record RoomStateMsg(
            String roomId,
            Long hostUserId,
            String hostUsername,
            List<MemberDto> members,
            List<Long> videoIds,
            Long currentVideoId
    ) {}

    public record StopVideoMsg(String roomId) {}


    public record VideoStartedMsg(String roomId, Long videoId, String eventId) {}
}
