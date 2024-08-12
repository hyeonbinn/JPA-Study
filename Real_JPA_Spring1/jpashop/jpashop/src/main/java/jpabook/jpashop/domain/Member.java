package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Cleanup;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") //order 테이블에 있는 member 필드에 의해 매핑. (연관관계 주인이 아닌 곳에 mappedBy)
    private List<Order> orders = new ArrayList<>();
}
