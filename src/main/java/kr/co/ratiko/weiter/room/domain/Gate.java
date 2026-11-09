package kr.co.ratiko.weiter.room.domain;

public enum Gate {
    NORTH_GATE("북문"),
    FARM_GATE("농장문"),
    NURI_GATE("누리문"),
    TECHNO_GATE("테크노문"),
    HYANGTO_GATE("향토문"),
    NARI_GATE("나리문"),
    EAST_GATE("동문"),
    MAIN_GATE("정문"),
    VETERINARY_COLLEGE_GATE("수의대문"),
    SIDE_GATE("쪽문"),
    PROFESSOR_GATE("교수문"),
    JOEUN_GATE("조은문"),
    SOLO_GATE("솔로문"),
    WEST_GATE("서문"),
    SWIMMING_POOL_GATE("수영장문"),
    DAYCARE_GATE("어린이집문");

    private final String koreanName;

    Gate(String koreanName) {
        this.koreanName = koreanName;
    }

    public String koreanName() {
        return koreanName;
    }
}
