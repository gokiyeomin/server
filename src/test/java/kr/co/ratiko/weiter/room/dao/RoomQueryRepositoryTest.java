package kr.co.ratiko.weiter.room.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
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
import kr.co.ratiko.weiter.room.domain.RoomSearchCondition;
import kr.co.ratiko.weiter.room.domain.RoomSort;
import kr.co.ratiko.weiter.room.domain.RoomTitle;
import kr.co.ratiko.weiter.room.domain.TradeStatus;
import kr.co.ratiko.weiter.room.domain.TransferBenefit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:room;MODE=MySQL;DB_CLOSE_DELAY=-1")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RoomQueryRepositoryTest {

    private static final int NO_LIMIT = Integer.MAX_VALUE;

    @Autowired
    private RoomJpaRepository jpaRepository;

    @Autowired
    private EntityManager entityManager;

    private final RoomMapper mapper = new RoomMapper();
    private RoomQueryRepository repository;

    @BeforeEach
    void setUp() {
        repository = new RoomQueryRepository(new JPAQueryFactory(entityManager));
    }

    @Test
    void gateFilterKeepsOnlyRoomsOfThatGate() {
        long north = save(Gate.NORTH_GATE, 0, 300_000, 0, false);
        save(Gate.MAIN_GATE, 0, 300_000, 0, false);

        assertThat(idsOf(search(Gate.NORTH_GATE, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.RECENT, null)))
                .containsExactly(north);
    }

    @Test
    void monthlyRangeCountsRentPlusMaintenanceFee() {
        long inRange = save(Gate.NORTH_GATE, 0, 300_000, 50_000, false);
        save(Gate.NORTH_GATE, 0, 300_000, 0, false);
        save(Gate.NORTH_GATE, 0, 340_000, 50_000, false);

        assertThat(idsOf(search(null, 340_000, 360_000, 0, NO_LIMIT, false, false, RoomSort.RECENT, null)))
                .containsExactly(inRange);
    }

    @Test
    void depositRangeKeepsRoomsWithinItInclusive() {
        save(Gate.NORTH_GATE, 1_000_000, 300_000, 0, false);
        long low = save(Gate.NORTH_GATE, 2_000_000, 300_000, 0, false);
        long high = save(Gate.NORTH_GATE, 3_000_000, 300_000, 0, false);
        save(Gate.NORTH_GATE, 4_000_000, 300_000, 0, false);

        assertThat(idsOf(search(null, 0, NO_LIMIT, 2_000_000, 3_000_000, false, false, RoomSort.DEPOSIT_ASC, null)))
                .containsExactly(low, high);
    }

    @Test
    void benefitOnlyKeepsRoomsWithATransferBenefit() {
        save(Gate.NORTH_GATE, 0, 300_000, 0, false);
        long withBenefit = save(Gate.NORTH_GATE, 0, 300_000, 0, true);

        assertThat(idsOf(search(null, 0, NO_LIMIT, 0, NO_LIMIT, true, false, RoomSort.RECENT, null)))
                .containsExactly(withBenefit);
    }

    @Test
    void photoOnlyKeepsRoomsWithAtLeastOneImageWithoutDuplicates() {
        save(Gate.NORTH_GATE, 0, 300_000, 0, false);
        long withPhotos = save(Gate.NORTH_GATE, 0, 300_000, 0, false, "a.jpg", "b.jpg", "c.jpg");

        RoomEntityPage page = search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, true, RoomSort.RECENT, null);

        assertThat(idsOf(page)).containsExactly(withPhotos);
        assertThat(page.total()).isEqualTo(1);
    }

    @Test
    void idsRestrictTheResultToThoseRooms() {
        save(Gate.NORTH_GATE, 0, 300_000, 0, false);
        long second = save(Gate.NORTH_GATE, 0, 300_000, 0, false);
        long third = save(Gate.NORTH_GATE, 0, 300_000, 0, false);

        assertThat(idsOf(search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.DEPOSIT_ASC, List.of(second, third))))
                .containsExactlyInAnyOrder(second, third);
    }

    @Test
    void emptyIdsMatchNothing() {
        save(Gate.NORTH_GATE, 0, 300_000, 0, false);

        RoomEntityPage page = search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.RECENT, List.of());

        assertThat(page.rooms()).isEmpty();
        assertThat(page.total()).isZero();
    }

    @Test
    void recentSortPutsTheLatestSavedRoomFirst() {
        long first = save(Gate.NORTH_GATE, 0, 300_000, 0, false);
        long second = save(Gate.NORTH_GATE, 0, 300_000, 0, false);
        long third = save(Gate.NORTH_GATE, 0, 300_000, 0, false);

        assertThat(idsOf(search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.RECENT, null)))
                .containsExactly(third, second, first);
    }

    @Test
    void monthlySortsOrderByRentPlusMaintenanceFee() {
        long cheap = save(Gate.NORTH_GATE, 0, 250_000, 0, false);
        long middle = save(Gate.NORTH_GATE, 0, 250_000, 60_000, false);
        long expensive = save(Gate.NORTH_GATE, 0, 300_000, 50_000, false);

        assertThat(idsOf(search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.MONTHLY_ASC, null)))
                .containsExactly(cheap, middle, expensive);
        assertThat(idsOf(search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.MONTHLY_DESC, null)))
                .containsExactly(expensive, middle, cheap);
    }

    @Test
    void depositSortsOrderByDeposit() {
        long low = save(Gate.NORTH_GATE, 1_000_000, 300_000, 0, false);
        long high = save(Gate.NORTH_GATE, 5_000_000, 300_000, 0, false);

        assertThat(idsOf(search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.DEPOSIT_ASC, null)))
                .containsExactly(low, high);
        assertThat(idsOf(search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.DEPOSIT_DESC, null)))
                .containsExactly(high, low);
    }

    @Test
    void pagingReturnsTheRequestedSliceAndTheTotalOfAllMatches() {
        long first = save(Gate.NORTH_GATE, 0, 100_000, 0, false, "a.jpg", "b.jpg");
        long second = save(Gate.NORTH_GATE, 0, 200_000, 0, false);
        long third = save(Gate.NORTH_GATE, 0, 300_000, 0, false, "c.jpg");
        long fourth = save(Gate.NORTH_GATE, 0, 400_000, 0, false);
        long fifth = save(Gate.NORTH_GATE, 0, 500_000, 0, false);

        RoomEntityPage secondPage = search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.MONTHLY_ASC, null, 2, 2);

        assertThat(idsOf(secondPage)).containsExactly(third, fourth);
        assertThat(secondPage.total()).isEqualTo(5);
        assertThat(idsOf(search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.MONTHLY_ASC, null, 1, 2)))
                .containsExactly(first, second);
        assertThat(idsOf(search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.MONTHLY_ASC, null, 3, 2)))
                .containsExactly(fifth);
    }

    @Test
    void imagesOfEachRoomComeBackInTheOrderTheyWereGiven() {
        save(Gate.NORTH_GATE, 0, 300_000, 0, false, "c.jpg", "a.jpg", "b.jpg");

        RoomEntityPage page = search(null, 0, NO_LIMIT, 0, NO_LIMIT, false, false, RoomSort.RECENT, null);

        assertThat(page.rooms().getFirst().imageUrls()).containsExactly("c.jpg", "a.jpg", "b.jpg");
    }

    private RoomEntityPage search(
            Gate gate, int monthlyMin, int monthlyMax, int depositMin, int depositMax,
            boolean benefitOnly, boolean photoOnly, RoomSort sort, List<Long> ids) {
        return search(gate, monthlyMin, monthlyMax, depositMin, depositMax, benefitOnly, photoOnly, sort, ids, 1, 50);
    }

    private RoomEntityPage search(
            Gate gate, int monthlyMin, int monthlyMax, int depositMin, int depositMax,
            boolean benefitOnly, boolean photoOnly, RoomSort sort, List<Long> ids, int page, int size) {
        return repository.search(new RoomSearchCondition(
                gate, monthlyMin, monthlyMax, depositMin, depositMax, benefitOnly, photoOnly, sort, ids, page, size));
    }

    private static List<Long> idsOf(RoomEntityPage page) {
        return page.rooms().stream().map(RoomEntity::id).toList();
    }

    private long save(Gate gate, int deposit, int rent, int maintenance, boolean transferBenefit, String... imageUrls) {
        Room room = new Room(
                null,
                new RoomTitle("북문 도보 3분 원룸"),
                new RoomPrice(deposit, rent, maintenance),
                new LeasePeriod(LocalDate.of(2027, 3, 1), LocalDate.of(2027, 8, 31)),
                gate,
                new TransferBenefit(transferBenefit, null),
                TradeStatus.TRADING,
                new RoomDescription("북문 도보 3분"),
                new RoomContact("https://open.kakao.com/o/abc"),
                new RoomDetails(Map.of()),
                new RoomImages(List.of(imageUrls)));
        Long id = jpaRepository.save(mapper.toEntity(room)).id();
        entityManager.flush();
        entityManager.clear();
        return id;
    }
}
