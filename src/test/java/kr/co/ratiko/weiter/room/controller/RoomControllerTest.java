package kr.co.ratiko.weiter.room.controller;

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
import kr.co.ratiko.weiter.room.domain.RoomPage;
import kr.co.ratiko.weiter.room.domain.RoomPrice;
import kr.co.ratiko.weiter.room.domain.RoomSearchCondition;
import kr.co.ratiko.weiter.room.domain.RoomSort;
import kr.co.ratiko.weiter.room.domain.RoomTitle;
import kr.co.ratiko.weiter.room.domain.TradeStatus;
import kr.co.ratiko.weiter.room.domain.TransferBenefit;
import kr.co.ratiko.weiter.room.service.RoomQueryService;
import org.junit.jupiter.api.Test;

class RoomControllerTest {

    private static final String EVERYTIME_POST = "https://everytime.kr/441673/v/9001";
    private static final String KAKAO = "https://open.kakao.com/o/abc";

    private final FakeRoomQueryService service = new FakeRoomQueryService();
    private final RoomController controller = new RoomController(service);

    @Test
    void searchPassesEveryRequestValueToTheServiceAsOneCondition() {
        controller.search(3, 12, Gate.MAIN_GATE, 100_000, 500_000, 1_000_000, 5_000_000, true, true,
                RoomSort.DEPOSIT_DESC, List.of(1L, 2L));

        assertThat(service.condition).isEqualTo(new RoomSearchCondition(
                Gate.MAIN_GATE, 100_000, 500_000, 1_000_000, 5_000_000, true, true, RoomSort.DEPOSIT_DESC,
                List.of(1L, 2L), 3, 12));
    }

    @Test
    void searchAnswersWithSimpleRoomsTheTotalAndThePageRequested() {
        service.page = new RoomPage(List.of(room(7L, KAKAO, Map.of("floorLabel", "2층", "roomType", "원룸",
                "extraCostDescription", "인터넷 별도"), List.of("a.jpg", "b.jpg"))), 31);

        RoomSearchResponse response = controller.search(2, 12, null, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE,
                false, false, RoomSort.RECENT, null);

        assertThat(response.total()).isEqualTo(31);
        assertThat(response.page()).isEqualTo(2);
        assertThat(response.size()).isEqualTo(12);
        assertThat(response.items()).containsExactly(new SimpleRoomResponse(
                7L, "북문 도보 3분 원룸", new GateResponse("NORTH_GATE", "북문"), 1_000_000, 300_000, 50_000,
                "인터넷 별도", true, "월세 1개월 지원", "2층", "원룸", List.of("a.jpg", "b.jpg")));
    }

    @Test
    void detailAnswersWithEveryFieldOfTheRoom() {
        service.room = room(7L, KAKAO, Map.ofEntries(
                Map.entry("floorLabel", "3층"),
                Map.entry("roomType", "주방 분리형"),
                Map.entry("orientation", "남향"),
                Map.entry("options", List.of("에어컨", "세탁기")),
                Map.entry("features", List.of("역세권")),
                Map.entry("parkingNote", "주차 가능"),
                Map.entry("managementNote", "관리비에 인터넷 포함"),
                Map.entry("moveInNote", "즉시 입주"),
                Map.entry("extensionNote", "연장 가능"),
                Map.entry("viewingNote", "주말 방문"),
                Map.entry("extraCostDescription", "수도 별도")),
                List.of("a.jpg"));

        DetailRoomResponse response = controller.detail(7L);

        assertThat(response.id()).isEqualTo(7L);
        assertThat(response.gate()).isEqualTo(new GateResponse("NORTH_GATE", "북문"));
        assertThat(response.transferBenefit()).isTrue();
        assertThat(response.transferBenefitDetail()).isEqualTo("월세 1개월 지원");
        assertThat(response.description()).isEqualTo("북문 도보 3분");
        assertThat(response.floor()).isEqualTo("3층");
        assertThat(response.roomType()).isEqualTo("주방 분리형");
        assertThat(response.orientation()).isEqualTo("남향");
        assertThat(response.options()).containsExactly("에어컨", "세탁기");
        assertThat(response.features()).containsExactly("역세권");
        assertThat(response.parkingNote()).isEqualTo("주차 가능");
        assertThat(response.managementNote()).isEqualTo("관리비에 인터넷 포함");
        assertThat(response.moveInNote()).isEqualTo("즉시 입주");
        assertThat(response.extensionNote()).isEqualTo("연장 가능");
        assertThat(response.viewingNote()).isEqualTo("주말 방문");
        assertThat(response.extraCostDescription()).isEqualTo("수도 별도");
        assertThat(response.contactUrl()).isEqualTo(KAKAO);
        assertThat(response.imageUrls()).containsExactly("a.jpg");
    }

    @Test
    void detailOfARoomWithoutOptionalDetailsLeavesThemEmpty() {
        service.room = room(7L, KAKAO, Map.of(), List.of());

        DetailRoomResponse response = controller.detail(7L);

        assertThat(response.floor()).isNull();
        assertThat(response.roomType()).isNull();
        assertThat(response.options()).isEmpty();
        assertThat(response.originalUrl()).isNull();
        assertThat(response.imageUrls()).isEmpty();
    }

    @Test
    void floorNumberBecomesAFloorLabelWithBasementsMarked() {
        service.room = room(7L, KAKAO, Map.of("floorNumber", -1), List.of());
        assertThat(controller.detail(7L).floor()).isEqualTo("지하 1층");

        service.room = room(7L, KAKAO, Map.of("floorNumber", 4), List.of());
        assertThat(controller.detail(7L).floor()).isEqualTo("4층");
    }

    @Test
    void floorLabelWinsOverFloorNumber() {
        service.room = room(7L, KAKAO, Map.of("floorLabel", "반지하", "floorNumber", -1), List.of());

        assertThat(controller.detail(7L).floor()).isEqualTo("반지하");
    }

    @Test
    void anEverytimeContactIsTheOriginalPost() {
        service.room = room(7L, EVERYTIME_POST, Map.of(), List.of());

        assertThat(controller.detail(7L).originalUrl()).isEqualTo(EVERYTIME_POST);
    }

    @Test
    void anEverytimeSourceWithANumericIdBuildsTheOriginalPostUrl() {
        service.room = room(7L, KAKAO, Map.of("source", "everytime", "sourceId", "9001"), List.of());

        assertThat(controller.detail(7L).originalUrl()).isEqualTo("https://everytime.kr/441673/v/9001");
    }

    @Test
    void aNonNumericSourceIdOrAnotherSourceHasNoOriginalPost() {
        service.room = room(7L, KAKAO, Map.of("source", "everytime", "sourceId", "abc"), List.of());
        assertThat(controller.detail(7L).originalUrl()).isNull();

        service.room = room(7L, KAKAO, Map.of("source", "other", "sourceId", "9001"), List.of());
        assertThat(controller.detail(7L).originalUrl()).isNull();
    }

    private static Room room(Long id, String contactUrl, Map<String, Object> details, List<String> imageUrls) {
        return new Room(
                id,
                new RoomTitle("북문 도보 3분 원룸"),
                new RoomPrice(1_000_000, 300_000, 50_000),
                new LeasePeriod(LocalDate.of(2027, 3, 1), LocalDate.of(2027, 8, 31)),
                Gate.NORTH_GATE,
                new TransferBenefit(true, "월세 1개월 지원"),
                TradeStatus.TRADING,
                new RoomDescription("북문 도보 3분"),
                new RoomContact(contactUrl),
                new RoomDetails(details),
                new RoomImages(imageUrls));
    }

    private static class FakeRoomQueryService extends RoomQueryService {
        private RoomSearchCondition condition;
        private RoomPage page = new RoomPage(List.of(), 0);
        private Room room;

        FakeRoomQueryService() {
            super(null);
        }

        @Override
        public RoomPage search(RoomSearchCondition condition) {
            this.condition = condition;
            return page;
        }

        @Override
        public Room find(Long id) {
            return room;
        }
    }
}
