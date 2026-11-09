package kr.co.ratiko.weiter.room.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import kr.co.ratiko.weiter.room.domain.RoomSearchCondition;
import kr.co.ratiko.weiter.room.domain.RoomSort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class RoomQueryRepository {

    private static final QRoomEntity ROOM = QRoomEntity.roomEntity;
    private static final QRoomImageEntity IMAGE = QRoomImageEntity.roomImageEntity;
    private static final NumberExpression<Integer> MONTHLY_COST = ROOM.monthlyCostWon;

    private final JPAQueryFactory queryFactory;

    public RoomEntityPage search(RoomSearchCondition condition) {
        if (condition.restrictedToIds().filter(List::isEmpty).isPresent()) {
            return new RoomEntityPage(List.of(), 0);
        }
        BooleanBuilder where = where(condition);
        Long total = queryFactory.select(ROOM.count()).from(ROOM).where(where).fetchOne();
        List<Long> ids = queryFactory.select(ROOM.id).from(ROOM)
                .where(where)
                .orderBy(orderOf(condition.sort()))
                .offset(condition.offset())
                .limit(condition.size())
                .fetch();
        return new RoomEntityPage(fetchWithImagesInOrder(ids), total == null ? 0 : total);
    }

    private BooleanBuilder where(RoomSearchCondition condition) {
        BooleanBuilder where = new BooleanBuilder();
        condition.gateFilter().ifPresent(gate -> where.and(ROOM.gate.eq(gate)));
        where.and(MONTHLY_COST.between(condition.monthlyMin(), condition.monthlyMax()));
        where.and(ROOM.depositWon.between(condition.depositMin(), condition.depositMax()));
        if (condition.benefitOnly()) {
            where.and(ROOM.transferBenefit.isTrue());
        }
        if (condition.photoOnly()) {
            where.and(JPAExpressions.selectOne().from(IMAGE).where(IMAGE.room.id.eq(ROOM.id)).exists());
        }
        condition.restrictedToIds().ifPresent(ids -> where.and(ROOM.id.in(ids)));
        return where;
    }

    private OrderSpecifier<?>[] orderOf(RoomSort sort) {
        return switch (sort) {
            case RECENT -> new OrderSpecifier<?>[] {ROOM.createdAt.desc(), ROOM.id.desc()};
            case MONTHLY_ASC -> new OrderSpecifier<?>[] {MONTHLY_COST.asc(), ROOM.id.desc()};
            case MONTHLY_DESC -> new OrderSpecifier<?>[] {MONTHLY_COST.desc(), ROOM.id.desc()};
            case DEPOSIT_ASC -> new OrderSpecifier<?>[] {ROOM.depositWon.asc(), ROOM.id.desc()};
            case DEPOSIT_DESC -> new OrderSpecifier<?>[] {ROOM.depositWon.desc(), ROOM.id.desc()};
        };
    }

    private List<RoomEntity> fetchWithImagesInOrder(List<Long> ids) {
        if (ids.isEmpty()) {
            return List.of();
        }
        Map<Long, RoomEntity> byId = queryFactory.selectFrom(ROOM)
                .distinct()
                .leftJoin(ROOM.images, IMAGE).fetchJoin()
                .where(ROOM.id.in(ids))
                .fetch()
                .stream()
                .collect(Collectors.toMap(RoomEntity::id, Function.identity()));
        return ids.stream().map(byId::get).toList();
    }
}
