package kr.co.ratiko.weiter.room.domain;

import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;

public record RoomPrice(int depositWon, int monthlyRentWon, int maintenanceFeeWon) {

    public RoomPrice {
        if (depositWon < 0 || monthlyRentWon < 0 || maintenanceFeeWon < 0) {
            throw new BusinessException(ClientError.INVALID_REQUEST,
                    "방 가격은 음수일 수 없습니다: 보증금=%d, 월세=%d, 관리비=%d"
                            .formatted(depositWon, monthlyRentWon, maintenanceFeeWon));
        }
    }
}
