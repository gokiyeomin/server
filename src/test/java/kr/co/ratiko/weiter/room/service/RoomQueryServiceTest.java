package kr.co.ratiko.weiter.room.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;
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
import kr.co.ratiko.weiter.room.repository.RoomRepository;
import org.junit.jupiter.api.Test;

class RoomQueryServiceTest {

    private final FakeRoomRepository repository = new FakeRoomRepository();
    private final RoomQueryService service = new RoomQueryService(repository);

    @Test
    void searchReturnsWhatTheRepositoryFoundForTheCondition() {
        Room room = room(7L);
        repository.page = new RoomPage(List.of(room), 31);
        RoomSearchCondition condition = new RoomSearchCondition(
                Gate.MAIN_GATE, 0, 100, 0, 100, false, false, RoomSort.RECENT, null, 2, 12);

        RoomPage page = service.search(condition);

        assertThat(page.rooms()).containsExactly(room);
        assertThat(page.total()).isEqualTo(31);
        assertThat(repository.lastCondition).isSameAs(condition);
    }

    @Test
    void findReturnsTheRoomWithThatId() {
        Room room = room(7L);
        repository.rooms.put(7L, room);

        assertThat(service.find(7L)).isSameAs(room);
    }

    @Test
    void findOfAMissingRoomFailsAsRoomNotFound() {
        assertThatThrownBy(() -> service.find(404L))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.clientError()).isEqualTo(ClientError.ROOM_NOT_FOUND));
    }

    private static Room room(Long id) {
        return new Room(
                id,
                new RoomTitle("북문 도보 3분 원룸"),
                new RoomPrice(1_000_000, 300_000, 50_000),
                new LeasePeriod(LocalDate.of(2027, 3, 1), LocalDate.of(2027, 8, 31)),
                Gate.NORTH_GATE,
                new TransferBenefit(false, null),
                TradeStatus.TRADING,
                new RoomDescription("북문 도보 3분"),
                new RoomContact("https://open.kakao.com/o/abc"),
                new RoomDetails(Map.of()),
                new RoomImages(List.of()));
    }

    private static class FakeRoomRepository extends RoomRepository {
        private final Map<Long, Room> rooms = new HashMap<>();
        private RoomPage page = new RoomPage(List.of(), 0);
        private RoomSearchCondition lastCondition;

        FakeRoomRepository() {
            super(null, null);
        }

        @Override
        public Optional<Room> findById(Long id) {
            return Optional.ofNullable(rooms.get(id));
        }

        @Override
        public RoomPage search(RoomSearchCondition condition) {
            lastCondition = condition;
            return page;
        }
    }
}
