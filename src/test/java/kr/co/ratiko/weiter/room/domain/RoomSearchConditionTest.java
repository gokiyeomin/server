package kr.co.ratiko.weiter.room.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import java.util.stream.LongStream;
import kr.co.ratiko.weiter.common.exception.BusinessException;
import kr.co.ratiko.weiter.common.exception.ClientError;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RoomSearchConditionTest {

    @Test
    void defaultsAreValid() {
        assertThatCode(() -> condition(1, 12, 0, 100, 0, 100, null)).doesNotThrowAnyException();
    }

    @Test
    void offsetSkipsThePagesBeforeTheRequestedOne() {
        assertThat(condition(3, 12, 0, 100, 0, 100, null).offset()).isEqualTo(24);
    }

    @Test
    void offsetDoesNotOverflowForAHugePageNumber() {
        assertThat(condition(Integer.MAX_VALUE, 50, 0, 100, 0, 100, null).offset()).isEqualTo(2_147_483_646L * 50);
    }

    @Test
    void absentIdsMeanNoRestrictionWhileEmptyIdsRestrictToNothing() {
        assertThat(condition(1, 12, 0, 100, 0, 100, null).restrictedToIds()).isEmpty();
        assertThat(condition(1, 12, 0, 100, 0, 100, List.of()).restrictedToIds()).contains(List.of());
    }

    @Test
    void gateFilterIsAbsentWhenNoGateIsGiven() {
        assertThat(condition(1, 12, 0, 100, 0, 100, null).gateFilter()).isEmpty();
    }

    @ParameterizedTest
    @CsvSource({
            "0,12,0,100,0,100",
            "1,0,0,100,0,100",
            "1,51,0,100,0,100",
            "1,12,-1,100,0,100",
            "1,12,200,100,0,100",
            "1,12,0,100,-1,100",
            "1,12,0,100,200,100"})
    void invalidPagingOrRangeIsRejectedAsInvalidRequest(
            int page, int size, int monthlyMin, int monthlyMax, int depositMin, int depositMax) {
        assertThatThrownBy(() -> condition(page, size, monthlyMin, monthlyMax, depositMin, depositMax, null))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.clientError()).isEqualTo(ClientError.INVALID_REQUEST));
    }

    @Test
    void tooManyIdsAreRejectedAsInvalidRequest() {
        List<Long> ids = LongStream.rangeClosed(1, RoomSearchCondition.MAX_IDS + 1L).boxed().toList();

        assertThatThrownBy(() -> condition(1, 12, 0, 100, 0, 100, ids))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.clientError()).isEqualTo(ClientError.INVALID_REQUEST));
    }

    @Test
    void missingSortIsRejected() {
        assertThatThrownBy(() -> new RoomSearchCondition(null, 0, 100, 0, 100, false, false, null, null, 1, 12))
                .isInstanceOf(NullPointerException.class);
    }

    private static RoomSearchCondition condition(
            int page, int size, int monthlyMin, int monthlyMax, int depositMin, int depositMax, List<Long> ids) {
        return new RoomSearchCondition(
                null, monthlyMin, monthlyMax, depositMin, depositMax, false, false, RoomSort.RECENT, ids, page, size);
    }
}
