package kr.co.ratiko.weiter.room.controller;

import java.util.List;
import kr.co.ratiko.weiter.room.domain.Room;
import kr.co.ratiko.weiter.room.domain.RoomDetails;

public record DetailRoomResponse(
        Long id,
        String title,
        GateResponse gate,
        int depositWon,
        int monthlyRentWon,
        int maintenanceFeeWon,
        String extraCostDescription,
        boolean transferBenefit,
        String transferBenefitDetail,
        String description,
        String floor,
        String roomType,
        String orientation,
        List<String> options,
        List<String> features,
        String parkingNote,
        String managementNote,
        String moveInNote,
        String extensionNote,
        String viewingNote,
        String contactUrl,
        String originalUrl,
        List<String> imageUrls) {

    static DetailRoomResponse from(Room room) {
        RoomView view = new RoomView(room);
        RoomDetails details = room.details();
        return new DetailRoomResponse(
                room.id(),
                room.title().text(),
                view.gate(),
                room.price().depositWon(),
                room.price().monthlyRentWon(),
                room.price().maintenanceFeeWon(),
                details.text("extraCostDescription").orElse(null),
                room.transferBenefit().provided(),
                room.transferBenefit().detail(),
                room.description().text(),
                view.floor(),
                view.roomType(),
                details.text("orientation").orElse(null),
                details.texts("options"),
                details.texts("features"),
                details.text("parkingNote").orElse(null),
                details.text("managementNote").orElse(null),
                details.text("moveInNote").orElse(null),
                details.text("extensionNote").orElse(null),
                details.text("viewingNote").orElse(null),
                room.contact().url(),
                view.originalUrl(),
                view.imageUrls());
    }
}
