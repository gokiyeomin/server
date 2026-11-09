package kr.co.ratiko.weiter.room.domain;

import java.util.List;
import java.util.Objects;
import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;

public record RoomImages(List<String> urls) {

    public RoomImages {
        urls = List.copyOf(Objects.requireNonNull(urls, "방 이미지 주소 목록은 필수입니다. 없으면 빈 목록을 전달하세요."));
        if (urls.stream().anyMatch(String::isBlank)) {
            throw new BusinessException(ClientError.INVALID_REQUEST, "방 이미지 한 장의 주소는 비어 있을 수 없습니다.");
        }
    }
}
