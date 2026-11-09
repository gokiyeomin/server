package kr.co.ratiko.weiter.room.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

class RoomContactTest {

    @Test
    void urlIsAllowed() {
        assertThatCode(() -> new RoomContact("https://open.kakao.com/o/abc")).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\n\t"})
    void blankUrlIsRejectedAsInvalidRequest(String url) {
        assertThatThrownBy(() -> new RoomContact(url))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.clientError()).isEqualTo(ClientError.INVALID_REQUEST));
    }
}
