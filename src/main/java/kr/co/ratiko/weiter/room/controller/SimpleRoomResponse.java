package kr.co.ratiko.weiter.room.controller;

import java.util.List;
import kr.co.ratiko.weiter.room.domain.Room;

public record SimpleRoomResponse(
        Long id,
        String title,
        GateResponse gate,
        int depositWon,
        int monthlyRentWon,
        int maintenanceFeeWon,
        String extraCostDescription,
        boolean transferBenefit,
        String transferBenefitDetail,
        String floor,
        String roomType,
        List<String> imageUrls) {

    static SimpleRoomResponse from(Room room) {
        RoomView view = new RoomView(room);
        return new SimpleRoomResponse(
                room.id(),
                room.title().text(),
                view.gate(),
                room.price().depositWon(),
                room.price().monthlyRentWon(),
                room.price().maintenanceFeeWon(),
                room.details().text("extraCostDescription").orElse(null),
                room.transferBenefit().provided(),
                room.transferBenefit().detail(),
                view.floor(),
                view.roomType(),
                view.imageUrls());
    }
}
