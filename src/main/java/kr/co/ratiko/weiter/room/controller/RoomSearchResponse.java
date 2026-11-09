package kr.co.ratiko.weiter.room.controller;

import java.util.List;
import kr.co.ratiko.weiter.room.domain.RoomPage;

public record RoomSearchResponse(List<SimpleRoomResponse> items, long total, int page, int size) {

    static RoomSearchResponse from(RoomPage roomPage, int page, int size) {
        return new RoomSearchResponse(
                roomPage.rooms().stream().map(SimpleRoomResponse::from).toList(),
                roomPage.total(),
                page,
                size);
    }
}
