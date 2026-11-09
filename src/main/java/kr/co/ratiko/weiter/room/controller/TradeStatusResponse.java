package kr.co.ratiko.weiter.room.controller;

import kr.co.ratiko.weiter.room.domain.TradeStatus;

public record TradeStatusResponse(String code, String koreanName) {

    static TradeStatusResponse from(TradeStatus tradeStatus) {
        return new TradeStatusResponse(tradeStatus.name(), tradeStatus.koreanName());
    }
}
