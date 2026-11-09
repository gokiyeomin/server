package kr.co.ratiko.weiter.room.domain;

import java.time.LocalDate;
import java.util.Objects;
import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;

public record LeasePeriod(LocalDate moveInDate, LocalDate expiryDate) {

    public LeasePeriod {
        Objects.requireNonNull(moveInDate, "입주 가능일은 필수입니다.");
        Objects.requireNonNull(expiryDate, "계약 만료일은 필수입니다.");
        if (expiryDate.isBefore(moveInDate)) {
            throw new BusinessException(ClientError.INVALID_REQUEST,
                    "계약 만료일(%s)이 입주 가능일(%s)보다 앞설 수 없습니다.".formatted(expiryDate, moveInDate));
        }
    }
}
