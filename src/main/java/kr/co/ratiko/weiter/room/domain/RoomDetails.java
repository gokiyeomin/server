package kr.co.ratiko.weiter.room.domain;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public record RoomDetails(Map<String, Object> values) {

    public RoomDetails {
        values = Map.copyOf(Objects.requireNonNull(values, "상세 정보는 필수입니다. 없으면 빈 Map을 전달하세요."));
    }

    public Optional<String> text(String key) {
        return values.get(key) instanceof String text && !text.isBlank() ? Optional.of(text) : Optional.empty();
    }

    public List<String> texts(String key) {
        if (!(values.get(key) instanceof List<?> items)) {
            return List.of();
        }
        return items.stream().filter(String.class::isInstance).map(String.class::cast).toList();
    }

    public Optional<Integer> number(String key) {
        return values.get(key) instanceof Number number ? Optional.of(number.intValue()) : Optional.empty();
    }
}
