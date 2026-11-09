package kr.co.ratiko.weiter.room.domain;

import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;

public record RoomDescription(String text) {

    public RoomDescription {
        if (text == null || text.isBlank()) {
            throw new BusinessException(ClientError.INVALID_REQUEST, "방 본문은 비어 있을 수 없습니다.");
        }
    }
}
