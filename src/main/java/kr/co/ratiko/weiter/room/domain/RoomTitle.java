package kr.co.ratiko.weiter.room.domain;

import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;

public record RoomTitle(String text) {

    public RoomTitle {
        if (text == null || text.isBlank()) {
            throw new BusinessException(ClientError.INVALID_REQUEST, "방 제목은 비어 있을 수 없습니다.");
        }
    }
}
