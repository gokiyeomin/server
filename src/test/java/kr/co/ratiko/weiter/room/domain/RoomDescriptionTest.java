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

class RoomDescriptionTest {

    @Test
    void textIsAllowed() {
        assertThatCode(() -> new RoomDescription("북문 도보 3분")).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\n\t"})
    void blankTextIsRejectedAsInvalidRequest(String text) {
        assertThatThrownBy(() -> new RoomDescription(text))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.clientError()).isEqualTo(ClientError.INVALID_REQUEST));
    }
}
