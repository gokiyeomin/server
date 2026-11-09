package kr.co.ratiko.weiter.room.domain;

import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;

public record TransferBenefit(boolean provided, String detail) {

    public TransferBenefit {
        if (!provided && detail != null && !detail.isBlank()) {
            throw new BusinessException(ClientError.INVALID_REQUEST,
                    "양도 혜택이 없는데 양도 혜택 상세가 입력되었습니다: " + detail);
        }
    }
}
