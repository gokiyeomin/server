package kr.co.ratiko.weiter.room.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class RoomDetailsTest {

    @Test
    void valuesAreKeptAsGiven() {
        Map<String, Object> values = Map.of("options", List.of("에어컨"), "area", 16.5);

        assertThat(new RoomDetails(values).values()).isEqualTo(values);
    }

    @Test
    void valuesAreCopiedSoLaterChangesToTheSourceMapDoNotLeakIn() {
        Map<String, Object> values = new HashMap<>(Map.of("options", "에어컨"));
        RoomDetails details = new RoomDetails(values);

        values.put("parking", true);

        assertThat(details.values()).containsOnlyKeys("options");
    }

    @Test
    void emptyDetailsAreAllowed() {
        assertThat(new RoomDetails(Map.of()).values()).isEmpty();
    }

    @Test
    void textIsReadWhenTheValueIsANonBlankString() {
        RoomDetails details = new RoomDetails(Map.of("roomType", "원룸", "blank", " ", "number", 3));

        assertThat(details.text("roomType")).contains("원룸");
        assertThat(details.text("blank")).isEmpty();
        assertThat(details.text("number")).isEmpty();
        assertThat(details.text("missing")).isEmpty();
    }

    @Test
    void textsKeepOnlyTheStringsOfAList() {
        RoomDetails details = new RoomDetails(Map.of("options", List.of("에어컨", 3, "세탁기"), "single", "에어컨"));

        assertThat(details.texts("options")).containsExactly("에어컨", "세탁기");
        assertThat(details.texts("single")).isEmpty();
        assertThat(details.texts("missing")).isEmpty();
    }

    @Test
    void numberIsReadWhenTheValueIsANumber() {
        RoomDetails details = new RoomDetails(Map.of("floorNumber", -1, "text", "3"));

        assertThat(details.number("floorNumber")).contains(-1);
        assertThat(details.number("text")).isEmpty();
        assertThat(details.number("missing")).isEmpty();
    }

    @Test
    void missingValuesAreRejected() {
        assertThatThrownBy(() -> new RoomDetails(null)).isInstanceOf(NullPointerException.class);
    }
}
