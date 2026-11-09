package kr.co.ratiko.weiter.room.dao;

import kr.co.ratiko.weiter.room.domain.LeasePeriod;
import kr.co.ratiko.weiter.room.domain.Room;
import kr.co.ratiko.weiter.room.domain.RoomContact;
import kr.co.ratiko.weiter.room.domain.RoomDescription;
import kr.co.ratiko.weiter.room.domain.RoomDetails;
import kr.co.ratiko.weiter.room.domain.RoomImages;
import kr.co.ratiko.weiter.room.domain.RoomPrice;
import kr.co.ratiko.weiter.room.domain.RoomTitle;
import kr.co.ratiko.weiter.room.domain.TransferBenefit;

public class RoomMapper {

    public RoomEntity toEntity(Room room) {
        RoomEntity entity = new RoomEntity(
                room.id(),
                room.title().text(),
                room.price().depositWon(),
                room.price().monthlyRentWon(),
                room.price().maintenanceFeeWon(),
                room.leasePeriod().moveInDate(),
                room.leasePeriod().expiryDate(),
                room.gate(),
                room.transferBenefit().provided(),
                room.transferBenefit().detail(),
                room.tradeStatus(),
                room.description().text(),
                room.contact().url(),
                room.details().values());
        room.images().urls().forEach(entity::addImage);
        return entity;
    }

    public Room toDomain(RoomEntity entity) {
        return new Room(
                entity.id(),
                new RoomTitle(entity.title()),
                new RoomPrice(entity.depositWon(), entity.monthlyRentWon(), entity.maintenanceFeeWon()),
                new LeasePeriod(entity.moveInDate(), entity.expiryDate()),
                entity.gate(),
                new TransferBenefit(entity.transferBenefit(), entity.transferBenefitDetail()),
                entity.tradeStatus(),
                new RoomDescription(entity.description()),
                new RoomContact(entity.contactUrl()),
                new RoomDetails(entity.details()),
                new RoomImages(entity.imageUrls()));
    }
}
