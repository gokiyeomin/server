package kr.co.ratiko.weiter.room.dao;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import kr.co.ratiko.weiter.room.domain.Gate;
import kr.co.ratiko.weiter.room.domain.LeasePeriod;
import kr.co.ratiko.weiter.room.domain.Room;
import kr.co.ratiko.weiter.room.domain.RoomContact;
import kr.co.ratiko.weiter.room.domain.RoomDescription;
import kr.co.ratiko.weiter.room.domain.RoomDetails;
import kr.co.ratiko.weiter.room.domain.RoomImages;
import kr.co.ratiko.weiter.room.domain.RoomPrice;
import kr.co.ratiko.weiter.room.domain.RoomTitle;
import kr.co.ratiko.weiter.room.domain.TradeStatus;
import kr.co.ratiko.weiter.room.domain.TransferBenefit;
import org.junit.jupiter.api.Test;

class RoomMapperTest {

    private final RoomMapper mapper = new RoomMapper();

    @Test
    void everyDomainValueSurvivesTheRoundTripThroughTheEntity() {
        Room room = room(new TransferBenefit(true, "월세 1개월 지원"),
                new RoomDetails(Map.of("roomType", "ONE_ROOM", "options", List.of("에어컨", "세탁기"))),
                new RoomImages(List.of("https://example.com/a.jpg", "https://example.com/b.jpg")));

        Room restored = mapper.toDomain(mapper.toEntity(room));

        assertThat(restored).usingRecursiveComparison().isEqualTo(room);
    }

    @Test
    void roomWithoutTransferBenefitDetailOrImagesKeepsThemEmpty() {
        Room room = room(new TransferBenefit(false, null), new RoomDetails(Map.of()), new RoomImages(List.of()));

        Room restored = mapper.toDomain(mapper.toEntity(room));

        assertThat(restored).usingRecursiveComparison().isEqualTo(room);
    }

    @Test
    void imagesAreNumberedByTheirPositionInTheList() {
        Room room = room(new TransferBenefit(false, null), new RoomDetails(Map.of()),
                new RoomImages(List.of("https://example.com/b.jpg", "https://example.com/a.jpg")));

        RoomEntity entity = mapper.toEntity(room);

        assertThat(entity.imageUrls()).containsExactly("https://example.com/b.jpg", "https://example.com/a.jpg");
    }

    private static Room room(TransferBenefit transferBenefit, RoomDetails details, RoomImages images) {
        return new Room(
                null,
                new RoomTitle("북문 도보 3분 원룸"),
                new RoomPrice(1_000_000, 300_000, 50_000),
                new LeasePeriod(LocalDate.of(2027, 3, 1), LocalDate.of(2027, 8, 31)),
                Gate.NORTH_GATE,
                transferBenefit,
                TradeStatus.TRADING,
                new RoomDescription("북문 도보 3분"),
                new RoomContact("https://open.kakao.com/o/abc"),
                details,
                images);
    }
}
