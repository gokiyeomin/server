package kr.co.ratiko.weiter.room.domain;

import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;

public record RoomContact(String url) {

    public RoomContact {
        if (url == null || url.isBlank()) {
            throw new BusinessException(ClientError.INVALID_REQUEST, "방 연락처 주소는 비어 있을 수 없습니다.");
        }
    }
}
