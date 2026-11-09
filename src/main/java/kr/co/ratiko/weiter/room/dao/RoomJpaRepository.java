package kr.co.ratiko.weiter.room.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomJpaRepository extends JpaRepository<RoomEntity, Long> {

    @Override
    @EntityGraph(attributePaths = "images")
    Optional<RoomEntity> findById(Long id);
}
