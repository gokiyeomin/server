package kr.co.ratiko.weiter.room.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import kr.co.ratiko.weiter.room.dao.RoomJpaRepository;
import kr.co.ratiko.weiter.room.dao.RoomQueryRepository;
import kr.co.ratiko.weiter.room.domain.Gate;
import kr.co.ratiko.weiter.room.domain.LeasePeriod;
import kr.co.ratiko.weiter.room.domain.Room;
import kr.co.ratiko.weiter.room.domain.RoomContact;
import kr.co.ratiko.weiter.room.domain.RoomDescription;
import kr.co.ratiko.weiter.room.domain.RoomDetails;
import kr.co.ratiko.weiter.room.domain.RoomImages;
import kr.co.ratiko.weiter.room.domain.RoomPage;
import kr.co.ratiko.weiter.room.domain.RoomSearchCondition;
import kr.co.ratiko.weiter.room.domain.RoomSort;
import kr.co.ratiko.weiter.room.domain.RoomPrice;
import kr.co.ratiko.weiter.room.domain.RoomTitle;
import kr.co.ratiko.weiter.room.domain.TradeStatus;
import kr.co.ratiko.weiter.room.domain.TransferBenefit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase.Replace;

@DataJpaTest(properties = "spring.datasource.url=jdbc:h2:mem:room;MODE=MySQL;DB_CLOSE_DELAY=-1")
@AutoConfigureTestDatabase(replace = Replace.NONE)
class RoomRepositoryTest {

    @Autowired
    private RoomJpaRepository jpaRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void savedRoomGetsAnIdAndComesBackWithEveryValue() {
        RoomRepository repository = repository();
        Room saved = repository.save(room(new TransferBenefit(true, "월세 1개월 지원"),
                new RoomDetails(Map.of("roomType", "ONE_ROOM", "options", List.of("에어컨", "세탁기"))),
                new RoomImages(List.of("https://example.com/a.jpg"))));
        reloadFromDatabase();

        Room found = repository.findById(saved.id()).orElseThrow();

        assertThat(saved.id()).isNotNull();
        assertThat(found).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void roomWithoutTransferBenefitDetailOrImagesComesBackEmpty() {
        RoomRepository repository = repository();
        Room saved = repository.save(room(new TransferBenefit(false, null), new RoomDetails(Map.of()),
                new RoomImages(List.of())));
        reloadFromDatabase();

        Room found = repository.findById(saved.id()).orElseThrow();

        assertThat(found).usingRecursiveComparison().isEqualTo(saved);
    }

    @Test
    void imagesComeBackInTheOrderTheyWereGiven() {
        RoomRepository repository = repository();
        Room saved = repository.save(room(new TransferBenefit(false, null), new RoomDetails(Map.of()),
                new RoomImages(List.of("https://example.com/c.jpg", "https://example.com/a.jpg",
                        "https://example.com/b.jpg"))));
        reloadFromDatabase();

        Room found = repository.findById(saved.id()).orElseThrow();

        assertThat(found.images().urls()).containsExactly(
                "https://example.com/c.jpg", "https://example.com/a.jpg", "https://example.com/b.jpg");
    }

    @Test
    void missingRoomIsEmpty() {
        RoomRepository repository = repository();

        assertThat(repository.findById(Long.MAX_VALUE)).isEmpty();
    }

    @Test
    void searchReturnsDomainRoomsWithTheTotalOfAllMatches() {
        RoomRepository repository = repository();
        Room first = repository.save(room(new TransferBenefit(false, null), new RoomDetails(Map.of()),
                new RoomImages(List.of("https://example.com/a.jpg"))));
        repository.save(room(new TransferBenefit(false, null), new RoomDetails(Map.of()), new RoomImages(List.of())));
        reloadFromDatabase();

        RoomPage page = repository.search(new RoomSearchCondition(
                null, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, false, true, RoomSort.RECENT, null, 1, 12));

        assertThat(page.total()).isEqualTo(1);
        assertThat(page.rooms()).hasSize(1);
        assertThat(page.rooms().getFirst()).usingRecursiveComparison().isEqualTo(first);
    }

    private RoomRepository repository() {
        return new RoomRepository(jpaRepository, new RoomQueryRepository(new JPAQueryFactory(entityManager)));
    }

    private void reloadFromDatabase() {
        entityManager.flush();
        entityManager.clear();
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
