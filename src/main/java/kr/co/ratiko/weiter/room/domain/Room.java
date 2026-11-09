package kr.co.ratiko.weiter.room.domain;

import java.util.Objects;

public class Room {
    private final Long id;
    private final RoomTitle title;
    private final RoomPrice price;
    private final LeasePeriod leasePeriod;
    private final Gate gate;
    private final TransferBenefit transferBenefit;
    private final TradeStatus tradeStatus;
    private final RoomDescription description;
    private final RoomContact contact;
    private final RoomDetails details;
    private final RoomImages images;

    public Room(
            Long id,
            RoomTitle title,
            RoomPrice price,
            LeasePeriod leasePeriod,
            Gate gate,
            TransferBenefit transferBenefit,
            TradeStatus tradeStatus,
            RoomDescription description,
            RoomContact contact,
            RoomDetails details,
            RoomImages images) {
        this.id = id;
        this.title = Objects.requireNonNull(title, "방 제목은 필수입니다.");
        this.price = Objects.requireNonNull(price, "방 가격은 필수입니다.");
        this.leasePeriod = Objects.requireNonNull(leasePeriod, "계약 기간은 필수입니다.");
        this.gate = Objects.requireNonNull(gate, "문 정보는 필수입니다.");
        this.transferBenefit = Objects.requireNonNull(transferBenefit, "양도 혜택 정보는 필수입니다.");
        this.tradeStatus = Objects.requireNonNull(tradeStatus, "거래 상태는 필수입니다.");
        this.description = Objects.requireNonNull(description, "본문은 필수입니다.");
        this.contact = Objects.requireNonNull(contact, "연락처는 필수입니다.");
        this.details = Objects.requireNonNull(details, "상세 정보는 필수입니다.");
        this.images = Objects.requireNonNull(images, "방 이미지는 필수입니다. 이미지가 없으면 빈 목록을 가진 RoomImages를 전달하세요.");
    }

    public Long id() {
        return id;
    }

    public RoomTitle title() {
        return title;
    }

    public RoomPrice price() {
        return price;
    }

    public LeasePeriod leasePeriod() {
        return leasePeriod;
    }

    public Gate gate() {
        return gate;
    }

    public TransferBenefit transferBenefit() {
        return transferBenefit;
    }

    public TradeStatus tradeStatus() {
        return tradeStatus;
    }

    public RoomDescription description() {
        return description;
    }

    public RoomContact contact() {
        return contact;
    }

    public RoomDetails details() {
        return details;
    }

    public RoomImages images() {
        return images;
    }
}
