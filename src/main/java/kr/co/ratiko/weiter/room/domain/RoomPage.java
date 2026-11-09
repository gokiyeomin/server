package kr.co.ratiko.weiter.room.domain;

import java.util.List;
import java.util.Objects;

public record RoomPage(List<Room> rooms, long total) {

    public RoomPage {
        rooms = List.copyOf(Objects.requireNonNull(rooms, "방 목록은 필수입니다."));
    }
}
