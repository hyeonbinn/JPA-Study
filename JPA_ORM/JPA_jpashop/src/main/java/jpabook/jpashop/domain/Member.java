package jpabook.jpashop.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "MEMBER_ID")
    private Long id;
    @Embedded
    private Address address;

    @OneToMany(mappedBy = "member") //mappedBy 속성으로 주인 지정. 즉 얘는 연관관계 주인이 아님. 읽기만 가능
    private List<Order> orders = new ArrayList<>(); //양방향 관계 설정을 위해 1:N에서 1쪽에 List로 설정

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }
}
