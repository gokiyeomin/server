package kr.co.ratiko.weiter.room.controller;

import java.util.List;
import kr.co.ratiko.weiter.room.domain.Gate;
import kr.co.ratiko.weiter.room.domain.RoomSearchCondition;
import kr.co.ratiko.weiter.room.domain.RoomSort;
import kr.co.ratiko.weiter.room.service.RoomQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomQueryService roomQueryService;

    @GetMapping
    public RoomSearchResponse search(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) Gate gate,
            @RequestParam(defaultValue = "0") int monthlyMin,
            @RequestParam(defaultValue = "2147483647") int monthlyMax,
            @RequestParam(defaultValue = "0") int depositMin,
            @RequestParam(defaultValue = "2147483647") int depositMax,
            @RequestParam(defaultValue = "false") boolean benefitOnly,
            @RequestParam(defaultValue = "false") boolean photoOnly,
            @RequestParam(defaultValue = "RECENT") RoomSort sort,
            @RequestParam(required = false) List<Long> ids) {
        RoomSearchCondition condition = new RoomSearchCondition(
                gate, monthlyMin, monthlyMax, depositMin, depositMax, benefitOnly, photoOnly, sort, ids, page, size);
        return RoomSearchResponse.from(roomQueryService.search(condition), page, size);
    }

    @GetMapping("/{id}")
    public DetailRoomResponse detail(@PathVariable Long id) {
        return DetailRoomResponse.from(roomQueryService.find(id));
    }
}
