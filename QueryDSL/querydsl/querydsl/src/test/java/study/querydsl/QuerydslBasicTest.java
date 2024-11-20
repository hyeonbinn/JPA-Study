package study.querydsl;

import static org.assertj.core.api.Assertions.assertThat;
import static study.querydsl.entity.QMember.*;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.Team;


@SpringBootTest
@Transactional
public class QuerydslBasicTest {
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;
    @BeforeEach
    public void before() {
        queryFactory = new JPAQueryFactory(em);
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");

        em.persist(teamA);
        em.persist(teamB);

        Member member1 = new Member("member1", 10, teamA);
        Member member2 = new Member("member2", 20, teamA);

        Member member3 = new Member("member3", 30, teamB);
        Member member4 = new Member("member4", 40, teamB);

        em.persist(member1);
        em.persist(member2);
        em.persist(member3);
        em.persist(member4);
    }
    @Test
    void startJPQL() {
        // JPQL로 member1 찾기
        String qlString = "select m from Member m "
                + "where m.username = :username";
        Member findMember = em.createQuery(qlString, Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }
    @Test
    void startQuerydsl() {
        /** 장점
         * 1. 컴파일시점 오류를 발견할 수 있다.
         * 2. 파라미터 바인딩을 잡아준다.
         * **/

        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(member.username.eq("member1")) //파라미터 바인딩 처리
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");

        /**
         * from member : member 테이블 전체 조회
         * where(member.username.eq("member1").and(member.age.eq(10))) : member의 username이 "member1"이면서 age가 10인 경우 조건 걸기
         * select member: 2까지 수행되었을 때 해당하는 member 엔티티 가져오기
         * fetchOne() : 결과 하나 가져오기
         * **/
    }


    @Test
    void search() {
        Member findMember = queryFactory
                .selectFrom(member) //select와 from을 합쳐서 쓸 수 있음.
                .where(member.username.eq("member1") //eq:같다
                        .and(member.age.eq(10))) // and를 쓰지 않고 쉼표로 이어도 같은 의미.
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

    /**
     * 웬만한 것은 모두 지원.
     * member.username.eq("member1") // username = 'member1'
     * member.username.ne("member1") //username != 'member1'
     * member.username.eq("member1").not() // username != 'member1'
     * member.username.isNotNull() //이름이 is not null
     * member.age.in(10, 20) // age in (10,20)
     * member.age.notIn(10, 20) // age not in (10, 20)
     * member.age.between(10,30) //between 10, 30
     * member.age.goe(30) // age >= 30
     * member.age.gt(30) // age > 30
     * member.age.loe(30) // age <= 30
     * member.age.lt(30) // age < 30
     * member.username.like("member%") //like 검색
     * member.username.contains("member") // like ‘%member%’ 검색
     * member.username.startsWith("member") //like ‘member%’ 검색
     * ...
     * **/
}
