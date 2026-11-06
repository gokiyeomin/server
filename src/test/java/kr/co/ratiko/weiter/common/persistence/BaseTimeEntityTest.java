package kr.co.ratiko.weiter.common.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class BaseTimeEntityTest {

    @Test
    void creationStampsBothTimesWithTheSameInstant() {
        Sample sample = new Sample();

        sample.onCreate();

        assertThat(sample.createdAt()).isNotNull().isEqualTo(sample.updatedAt());
    }

    @Test
    void updateKeepsCreationTimeAndMovesUpdateTimeForward() {
        Sample sample = new Sample();
        sample.onCreate();

        sample.onUpdate();

        assertThat(sample.updatedAt()).isAfterOrEqualTo(sample.createdAt());
    }

    @Test
    void updateDoesNotChangeCreationTime() {
        Sample sample = new Sample();
        sample.onCreate();
        var createdAt = sample.createdAt();

        sample.onUpdate();

        assertThat(sample.createdAt()).isEqualTo(createdAt);
    }

    static class Sample extends BaseTimeEntity {
    }
}
