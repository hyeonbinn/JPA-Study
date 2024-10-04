package jpabook.jpashop.api;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;


import jpabook.jpashop.api.dto.OrderDto;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import jpabook.jpashop.service.query.OrderQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderApiController {

    private final OrderRepository orderRepository;
    private final OrderQueryRepository queryRepository;
    private final OrderQueryService queryService;

    /**
     * V1. 엔티티 직접 노출
     * 엔티티가 변하면 API 스펙도 변함
     * orderItem , item 관계를 직접 초기화하면 Hibernate5Module 설정에 의해 엔티티를 JSON으로 생성
     * 응답값이 재귀적으로 무한 반환
     * 양방향 관계 문제 발생 -> @JsonIgnore 추가
     */
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        return queryService.ordersV1();
    }

    /**
     * V1을 개선해 DTO로 변환하여 반환
     * */
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        return queryService.ordersV2();
    }

    /**
     * V2에서는 DTO에 데이터를 넣기 위해 LAZY 객체들을 모두 불러와야 하는데, 반복문으로 각 객체를 하나 하나 돌기에 N+1문제가 발생함
     * V3에서 페치조인으로 N+1문제를 해결!!
     */
    @GetMapping("/api/v3/orders")
    public List<OrderDto> ordersV3() {
        return queryService.ordersV3();
    }

    /**
     * V3.1 엔티티를 조회해서 DTO로 변환 페이징 고려
     * - ToOne 관계만 우선 모두 페치 조인으로 쿼리 수를 줄이자 (ToOne 관계는 페치 조인해도 페이징에 영향을 주지 않음)
     * - 나머지(컬렉션 관계_는 hibernate.default_batch_fetch_size, @BatchSize로 최적화
     */
    @GetMapping("/api/v3.1/orders")
    public List<OrderDto> ordersV3_page(
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "limit", defaultValue = "100") int limit
    ) {
        return queryService.ordersV3_page(offset, limit);
    }

    @GetMapping("/api/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return queryRepository.findOrderQueryDtos();
    }

    @GetMapping("/api/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return queryRepository.findAllByDto_optimization();
    }

    @GetMapping("/api/v6/orders")
    // 페이징 불가
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = queryRepository.findAllByDto_flat();
        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(),
                                o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(),
                                o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(),
                        e.getKey().getName(), e.getKey().getOrderDate(),
                        e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

}