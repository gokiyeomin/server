package kr.co.ratiko.weiter.room.service;

import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;
import kr.co.ratiko.weiter.room.domain.Room;
import kr.co.ratiko.weiter.room.domain.RoomPage;
import kr.co.ratiko.weiter.room.domain.RoomSearchCondition;
import kr.co.ratiko.weiter.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoomQueryService {

    private final RoomRepository roomRepository;

    @Transactional(readOnly = true)
    public RoomPage search(RoomSearchCondition condition) {
        return roomRepository.search(condition);
    }

    @Transactional(readOnly = true)
    public Room find(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ClientError.ROOM_NOT_FOUND, "room %d does not exist".formatted(id)));
    }
}
