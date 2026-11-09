package kr.co.ratiko.weiter.room.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;
import org.junit.jupiter.api.Test;

class LeasePeriodTest {

    private static final LocalDate MOVE_IN = LocalDate.of(2027, 3, 1);

    @Test
    void expiryOnTheMoveInDayIsAllowed() {
        assertThatCode(() -> new LeasePeriod(MOVE_IN, MOVE_IN)).doesNotThrowAnyException();
    }

    @Test
    void expiryBeforeMoveInIsRejectedAsInvalidRequest() {
        assertThatThrownBy(() -> new LeasePeriod(MOVE_IN, MOVE_IN.minusDays(1)))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.clientError()).isEqualTo(ClientError.INVALID_REQUEST));
    }
}
