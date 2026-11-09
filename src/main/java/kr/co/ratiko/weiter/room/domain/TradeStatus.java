package kr.co.ratiko.weiter.room.domain;

public enum TradeStatus {
    TRADING("거래중"),
    RESERVED("예약중"),
    COMPLETED("거래완료");

    private final String koreanName;

    TradeStatus(String koreanName) {
        this.koreanName = koreanName;
    }

    public String koreanName() {
        return koreanName;
    }
}
