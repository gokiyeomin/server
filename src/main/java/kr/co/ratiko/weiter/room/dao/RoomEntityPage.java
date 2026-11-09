package kr.co.ratiko.weiter.room.dao;

import java.util.List;

public record RoomEntityPage(List<RoomEntity> rooms, long total) {
}
