package com.example.jutjubic.dto;

import java.util.List;
import java.util.Set;

public final class WatchPartyDtos {
    private WatchPartyDtos() {}

    public record CreateRoomReq(boolean isPublic) {}
    public record AddVideoReq(Long videoId) {}
    public record MemberDto(Long userId, String username) {}


    public record RoomRes(
            String roomId,
            Long hostUserId,
            String hostUsername,
            boolean isPublic,
            int memberCount,
            int videoCount
    ) {}

    public record RoomDetailsRes(
            String roomId,
            Long hostUserId,
            String hostUsername,
            boolean isPublic,
            int memberCount,
            int videoCount,
            List<Long> videoIds,
            List<MemberDto> members,
            Long currentVideoId
    ) {}

}
