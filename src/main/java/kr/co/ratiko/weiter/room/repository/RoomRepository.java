package kr.co.ratiko.weiter.room.repository;

import java.util.Optional;
import kr.co.ratiko.weiter.room.dao.RoomEntityPage;
import kr.co.ratiko.weiter.room.dao.RoomJpaRepository;
import kr.co.ratiko.weiter.room.dao.RoomMapper;
import kr.co.ratiko.weiter.room.dao.RoomQueryRepository;
import kr.co.ratiko.weiter.room.domain.Room;
import kr.co.ratiko.weiter.room.domain.RoomPage;
import kr.co.ratiko.weiter.room.domain.RoomSearchCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomRepository {

    private final RoomJpaRepository jpaRepository;
    private final RoomQueryRepository queryRepository;
    private final RoomMapper mapper = new RoomMapper();

    public Room save(Room room) {
        return mapper.toDomain(jpaRepository.save(mapper.toEntity(room)));
    }

    public Optional<Room> findById(Long id) {
        return jpaRepository.findById(id).map(mapper::toDomain);
    }

    public RoomPage search(RoomSearchCondition condition) {
        RoomEntityPage page = queryRepository.search(condition);
        return new RoomPage(page.rooms().stream().map(mapper::toDomain).toList(), page.total());
    }
}
