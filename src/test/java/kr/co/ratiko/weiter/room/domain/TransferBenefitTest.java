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

class TransferBenefitTest {

    @Test
    void benefitWithDetailIsAllowed() {
        assertThatCode(() -> new TransferBenefit(true, "월세 1개월 지원")).doesNotThrowAnyException();
    }

    @Test
    void benefitWithoutDetailIsAllowed() {
        assertThatCode(() -> new TransferBenefit(true, null)).doesNotThrowAnyException();
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = " ")
    void noBenefitWithNoOrBlankDetailIsAllowed(String detail) {
        assertThatCode(() -> new TransferBenefit(false, detail)).doesNotThrowAnyException();
    }

    @Test
    void detailWithoutBenefitIsRejectedAsInvalidRequest() {
        assertThatThrownBy(() -> new TransferBenefit(false, "월세 1개월 지원"))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.clientError()).isEqualTo(ClientError.INVALID_REQUEST));
    }
}
