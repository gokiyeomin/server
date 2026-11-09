package kr.co.ratiko.weiter.room.domain;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;

public record RoomSearchCondition(
        Gate gate,
        int monthlyMin,
        int monthlyMax,
        int depositMin,
        int depositMax,
        boolean benefitOnly,
        boolean photoOnly,
        RoomSort sort,
        List<Long> ids,
        int page,
        int size) {

    public static final int MAX_SIZE = 50;
    public static final int MAX_IDS = 200;

    public RoomSearchCondition {
        Objects.requireNonNull(sort, "정렬 기준은 필수입니다.");
        ids = ids == null ? null : List.copyOf(ids);
        rejectUnless(page >= 1, "페이지는 1 이상이어야 합니다: " + page);
        rejectUnless(size >= 1 && size <= MAX_SIZE, "페이지 크기는 1 이상 %d 이하여야 합니다: %d".formatted(MAX_SIZE, size));
        rejectUnless(0 <= monthlyMin && monthlyMin <= monthlyMax,
                "월 부담액 범위가 올바르지 않습니다: %d ~ %d".formatted(monthlyMin, monthlyMax));
        rejectUnless(0 <= depositMin && depositMin <= depositMax,
                "보증금 범위가 올바르지 않습니다: %d ~ %d".formatted(depositMin, depositMax));
        rejectUnless(ids == null || ids.size() <= MAX_IDS, "방 ID 목록은 %d개 이하여야 합니다.".formatted(MAX_IDS));
    }

    public Optional<Gate> gateFilter() {
        return Optional.ofNullable(gate);
    }

    public Optional<List<Long>> restrictedToIds() {
        return Optional.ofNullable(ids);
    }

    public long offset() {
        return (long) (page - 1) * size;
    }

    private static void rejectUnless(boolean valid, String logMessage) {
        if (!valid) {
            throw new BusinessException(ClientError.INVALID_REQUEST, logMessage);
        }
    }
}
