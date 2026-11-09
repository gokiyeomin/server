package kr.co.ratiko.weiter.room.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.stream.Stream;
import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class RoomPriceTest {

    @Test
    void zeroAmountsAreAllowed() {
        assertThatCode(() -> new RoomPrice(0, 0, 0)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @MethodSource("pricesWithOneNegativeAmount")
    void anyNegativeAmountIsRejectedAsInvalidRequest(int deposit, int monthlyRent, int maintenanceFee) {
        assertThatThrownBy(() -> new RoomPrice(deposit, monthlyRent, maintenanceFee))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.clientError()).isEqualTo(ClientError.INVALID_REQUEST));
    }

    static Stream<Arguments> pricesWithOneNegativeAmount() {
        return Stream.of(
                Arguments.of(-1, 300_000, 0),
                Arguments.of(1_000_000, -1, 0),
                Arguments.of(1_000_000, 300_000, -1));
    }
}
