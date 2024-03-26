package hellojpa;

import jakarta.persistence.*;

@Entity
public class Locker {
    @Id @GeneratedValue
    private Long id;
    private String name;
    @OneToOne(mappedBy = "locker") //일대일에도 양방향 매핑시 mappedBy 원리는 똑같이 적용됨.
    private Member member;
}
