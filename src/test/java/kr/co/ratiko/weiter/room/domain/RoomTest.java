package kr.co.ratiko.weiter.room.domain;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RoomTest {

    private static final RoomPrice PRICE = new RoomPrice(1_000_000, 300_000, 50_000);

    @Test
    void roomWithEveryRequiredValueIsCreatedBeforeItHasAnId() {
        assertThatCode(() -> room(PRICE, new RoomImages(List.of("https://example.com/a.jpg"))))
                .doesNotThrowAnyException();
    }

    @Test
    void roomWithoutImagesIsAllowed() {
        assertThatCode(() -> room(PRICE, new RoomImages(List.of()))).doesNotThrowAnyException();
    }

    @Test
    void roomWithoutPriceIsRejected() {
        assertThatThrownBy(() -> room(null, new RoomImages(List.of()))).isInstanceOf(NullPointerException.class);
    }

    @Test
    void roomWithMissingImageObjectIsRejectedSoAbsenceIsAlwaysAnEmptyList() {
        assertThatThrownBy(() -> room(PRICE, null)).isInstanceOf(NullPointerException.class);
    }

    private static Room room(RoomPrice price, RoomImages images) {
        return new Room(
                null,
                new RoomTitle("북문 도보 3분 원룸"),
                price,
                new LeasePeriod(LocalDate.of(2027, 3, 1), LocalDate.of(2027, 8, 31)),
                Gate.NORTH_GATE,
                new TransferBenefit(true, "월세 1개월 지원"),
                TradeStatus.TRADING,
                new RoomDescription("북문 도보 3분"),
                new RoomContact("https://open.kakao.com/o/abc"),
                new RoomDetails(Map.of()),
                images);
    }
}
