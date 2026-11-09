package kr.co.ratiko.weiter.room.controller;

import kr.co.ratiko.weiter.room.domain.Gate;

public record GateResponse(String code, String koreanName) {

    static GateResponse from(Gate gate) {
        return new GateResponse(gate.name(), gate.koreanName());
    }
}
