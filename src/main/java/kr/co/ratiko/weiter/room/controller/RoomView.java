package kr.co.ratiko.weiter.room.controller;

import java.util.List;
import java.util.Optional;
import kr.co.ratiko.weiter.room.domain.Room;
import kr.co.ratiko.weiter.room.domain.RoomDetails;

final class RoomView {

    private static final String EVERYTIME_URL_PREFIX = "https://everytime.kr/";
    private static final String EVERYTIME_BOARD_ID = "441673";

    private final Room room;
    private final RoomDetails details;

    RoomView(Room room) {
        this.room = room;
        this.details = room.details();
    }

    GateResponse gate() {
        return GateResponse.from(room.gate());
    }

    List<String> imageUrls() {
        return room.images().urls();
    }

    String roomType() {
        return details.text("roomType").orElse(null);
    }

    String floor() {
        return details.text("floorLabel")
                .or(() -> details.number("floorNumber").map(RoomView::floorNumberLabel))
                .orElse(null);
    }

    String originalUrl() {
        String contactUrl = room.contact().url();
        if (contactUrl.startsWith(EVERYTIME_URL_PREFIX)) {
            return contactUrl;
        }
        boolean fromEverytime = details.text("source").filter("everytime"::equals).isPresent();
        return details.text("sourceId")
                .filter(id -> fromEverytime && id.chars().allMatch(Character::isDigit))
                .map(id -> EVERYTIME_URL_PREFIX + EVERYTIME_BOARD_ID + "/v/" + id)
                .orElse(null);
    }

    private static String floorNumberLabel(int floorNumber) {
        return floorNumber < 0 ? "지하 " + (-floorNumber) + "층" : floorNumber + "층";
    }
}
