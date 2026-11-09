package kr.co.ratiko.weiter.room.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.List;
import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class RoomImagesTest {

    @Test
    void urlsKeepTheirOrder() {
        RoomImages images = new RoomImages(List.of("https://example.com/b.jpg", "https://example.com/a.jpg"));

        assertThat(images.urls()).containsExactly("https://example.com/b.jpg", "https://example.com/a.jpg");
    }

    @Test
    void roomWithNoImagesIsAnEmptyList() {
        assertThat(new RoomImages(List.of()).urls()).isEmpty();
    }

    @Test
    void urlsAreCopiedSoLaterChangesToTheSourceListDoNotLeakIn() {
        List<String> urls = new ArrayList<>(List.of("https://example.com/a.jpg"));
        RoomImages images = new RoomImages(urls);

        urls.add("https://example.com/b.jpg");

        assertThat(images.urls()).containsExactly("https://example.com/a.jpg");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " "})
    void blankUrlIsRejectedAsInvalidRequest(String blank) {
        assertThatThrownBy(() -> new RoomImages(List.of("https://example.com/a.jpg", blank)))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.clientError()).isEqualTo(ClientError.INVALID_REQUEST));
    }

    @Test
    void missingListIsRejected() {
        assertThatThrownBy(() -> new RoomImages(null)).isInstanceOf(NullPointerException.class);
    }
}
