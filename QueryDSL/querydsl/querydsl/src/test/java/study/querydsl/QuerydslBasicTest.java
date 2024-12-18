package study.querydsl;

import static com.querydsl.jpa.JPAExpressions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.setAllowComparingPrivateFields;
import static study.querydsl.entity.QMember.*;
import static study.querydsl.entity.QTeam.team;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import jakarta.persistence.Query;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import study.querydsl.dto.MemberDto;
import study.querydsl.dto.QMemberDto;
import study.querydsl.dto.UserDto;
import study.querydsl.entity.Member;
import study.querydsl.entity.QMember;
import study.querydsl.entity.QTeam;
import study.querydsl.entity.Team;

import java.util.List;


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

    @Test
    /**
     * fetchResults() : 페이징 정보 포함, total count 쿼리 추가 실행
     * fetchCount() : count 쿼리로 변경해서 count 수 조회
     * **/

    void resultFetch() {
        List<Member> fetch = queryFactory //fetch() : 리스트 조회, 데이터 없으면 빈 리스트 반환
                .selectFrom(member)
                .fetch();
        Member fetchOne = queryFactory //fetchOne() : 단 건 조회 (결과가 없으면 null, 둘 이상이면 NonUniqueResultException)
                .selectFrom(member)
                .fetchOne();

        // fetchFirst은 limit(1).fetchOne과 같다.
        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        QueryResults<Member> results = queryFactory
                .selectFrom(member)
                .fetchResults(); // 실제로는 페이징쿼리가 복잡해지면 성능 때문에 결과가 달라질 수 있어 사용하지 않음 >> count 쿼리를 따로 날리는게 좋음!


        // count 쿼리 >> select절을 count 쿼리로 바꾼 것이라고 생각.
        Long total = queryFactory
                .selectFrom(member)
                .fetchCount();
    }

    /** 정렬 **/

    /**
     * 회원 정렬 순서
     * 1. 회원 나이 내림차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    public void sort() {
        em.persist(new Member(null, 100,null));
        em.persist(new Member("member5", 100,null));
        em.persist(new Member("member6", 100,null));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();
    }

    /** 페이징 **/
    // offset과 limit로 페이징 가능
    @Test
    public void paging1() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1) //0부터 시작(zero index)
                .limit(2) //최대 2건 조회
                .fetch();
        assertThat(result.size()).isEqualTo(2);
    }

    @Test
    public void paging2() { //전체 조회 수가 필요한 경우
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);
    }


    /** 집합 **/
    /**
     * JPQL
     * select
     * COUNT(m), //회원수
     * SUM(m.age), //나이 합
     * AVG(m.age), //평균 나이
     * MAX(m.age), //최대 나이
     * MIN(m.age) //최소 나이
     * from Member m
     */
    @Test
    public void aggregation() throws Exception {
        List<Tuple> result = queryFactory // Querydsl 에서 지원하는 Tuple, 여러 개의 타입이 있을 때 꺼내올 수 있음
                .select(member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);

        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);
    }

    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라.
     */
    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team) //member와 team 조인
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0); //0번 튜픟
        Tuple teamB = result.get(1); //1번 튜픟

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);
        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);
    }

    /** 조인 - 기본 조인 **/
    /**
     * 팀 A에 소속된 모든 회원
     */
    @Test
    public void join() {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team) //inner join과 동일. (left join, right join 등도 가능함)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }

    /**
     * 세타 조인(연관관계가 없어도 필드로 조인이 가능하다.)
     * 회원의 이름이 팀 이름과 같은 회원 조회
     */
    @Test
    public void theta_join()  {
        em.persist(new Member("teamA",0,null));
        em.persist(new Member("teamB",0,null));
        em.persist(new Member("teamC",0,null));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team) //우선 모두 조인을 하고
                .where(member.username.eq(team.name)) //where 절에서 필터링
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

    //세타 조인에서는 외부 조인이 불가능했지만, 이제는 on절을 사용해 가능


    /** ON절을 활용한 조인(JPA 2.1부터 지원)
        1. 조인 대상 필터링
        2. 연관관계 없는 엔티티 외부 조인
     **/

    /**
     * 1. 조인 대상 필터링
     *
     * 예) 회원과 팀을 조인하면서, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: SELECT m, t FROM Member m LEFT JOIN m.team t on t.name = 'teamA'
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.TEAM_ID=t.id and
     t.name='teamA'
     */
    @Test
    public void join_on_filtering() throws Exception {
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA")) //외부 조인이 아니라 내부 조인을 사용하면, where 절에서 필터링 하는 것과 기능이 동일하다!!
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple); }
    }

    /** 2. 연관관계 없는 엔티티 외부 조인 **/

    /**
     * 예) 회원의 이름과 팀의 이름이 같은 대상 외부 조인
     * JPQL: SELECT m, t FROM Member m LEFT JOIN Team t on m.username = t.name
     * SQL: SELECT m.*, t.* FROM Member m LEFT JOIN Team t ON m.username = t.name
     */
    @Test
    public void join_on_no_relation() throws Exception {
        em.persist(new Member("teamA",0,null));
        em.persist(new Member("teamB",0,null));
        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                //on 조인은 일반 조인과 다르게 엔티티가 하나만 들어간다!
                //조인 대상이 바로 들어가고, on절에서 원하는 조건이 들어간다!!
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();
        for (Tuple tuple : result) {
            System.out.println("t=" + tuple);
        }}


    /** 페치 조인 **/

    /** 페치 조인 미적용 **/
    @PersistenceUnit
    EntityManagerFactory emf;
    @Test
    public void fetchJoinNo() throws Exception {
        em.flush();
        em.clear();
        Member findMember = queryFactory
                .selectFrom(member)
                .where(member.username.eq("member1"))
                .fetchOne();

        //이미 로딩된(초기화) 엔티티인지 아닌지를 구분해줌
        boolean loaded =
                emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());

        assertThat(loaded).as("페치 조인 미적용").isFalse();}  //결과 isLoaded가 false

    /** 페치 조인 적용 **/
    @Test
    public void fetchJoinUse() throws Exception {
        em.flush();
        em.clear();
        Member findMember = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin() //뒤에 .fetchJoin()만 적어주면 된다. 한방쿼리로 가져온다.
                .where(member.username.eq("member1"))
                .fetchOne();

        //이미 로딩된(초기화) 엔티티인지 아닌지를 구분해줌
        boolean loaded =
                emf.getPersistenceUnitUtil().isLoaded(findMember.getTeam());
        assertThat(loaded).as("페치 조인 적용").isTrue(); //결과 isLoaded가 true
    }

    /** 서브 쿼리 **/

    /**
     * 나이가 가장 많은 회원 조회
     */
    @Test
    public void subQuery()  {
        QMember memberSub = new QMember("memberSub"); //서브쿼리 내에서 쓰이는 member는 바깥에 선언한 member와 겹치면 안 되므로 따로 선언

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq( //eq 대신 goe, in 등 다양한 조건 사용 가능.
                        //서브 쿼리에서는 com.querydsl.jpa.JPAExpressions 을 사용한다.
                        select(memberSub.age.max())
                                .from(memberSub)
                ))
                .fetch();
        assertThat(result).extracting("age")
                .containsExactly(40);

    }

    /**
     * select 절에 subquery
     */
    @Test
    public void selectSubQuery()  {
        QMember memberSub = new QMember("memberSub");

        List<Tuple> fetch = queryFactory
                .select(member.username,
                        //select 절에 subquery 사용할 수 있다.
                        select(memberSub.age.avg())
                                .from(memberSub)
                ).from(member)
                .fetch();

        for (Tuple tuple : fetch) {
            System.out.println("username = " + tuple.get(member.username));
            System.out.println("age = " +
                    tuple.get(select(memberSub.age.avg())
                            .from(memberSub)));
        }
    }

    /** JPA JPQL,Querydsl는 from 절의 서브쿼리(인라인 뷰)는 지원하지 않는다.
     *
     *  <from 절의 서브쿼리 해결방안>
     * 1. 서브쿼리를 join으로 변경한다. (가능한 상황도 있고, 불가능한 상황도 있다.)
     * 2. 애플리케이션에서 쿼리를 2번 분리해서 실행한다.
     * 3. nativeSQL을 사용한다.
     * **/

    /** 쿼리는 데이터를 가져오는 것에만 집중하는 게 좋음..!!
     *  쿼리에서 모든 걸 다 해결하려고 하면 from 안에 from안에,,이렇게 됨 **/



    /** Case 뮨 **/

    // 이런 case 마다의 로직은 애플리케이션에서 하는 게 좋음.
    // DB에서는 값을 가져오는 용도로만 사용하는 것이 좋음
    @Test
    public void baseCase() { //단순 조건
        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s= " + s);
        }
    }

    @Test
    public void complexCase() { //복잡한 조건
        List<String> result = queryFactory
                .select(new CaseBuilder()    // CaseBuilder를 사용할 수 있다.
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s= " + s);
        }
    }

    /** 상수, 문자 더하기 **/
    @Test
    public void constance() {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A")) //Expressions를 사용해 문자를 가져올 수 있다.
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);

        }
    }

    @Test
    public void concat() {
        //{username}_{age}
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue())) //stringValue를 사용해 age의 타입을 String으로 바꾸고 concat한다. 보통 enum에 많이 쓰임
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }


    /** 프로젝션 **/
    @Test
    public void simpleProjection() { // 프로젝션 결과가 하나인 것.
        List<String> result = queryFactory
                .select(member.username) //타입이 하나
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void tupleProjection() { // 프로젝션 결과가 여러 개인 것.
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username); //tuple.get으로 각각 꺼내올 수 있음. 반환타입도 자동으로 맞춰줌.
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }


    /** 순수 JPA에서 DTO 조회 **/
    @Test
    public void findDtoByJPQL() {
        List<MemberDto> result = em.createQuery(
                        "select new study.querydsl.dto.MemberDto(m.username, m.age) " +
                                "from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }

    }

    /** Querydsl 빈 생성(Bean population) **/

    /** 프로퍼티 접근  - setter **/
    @Test
    public void findDtoBySetter() {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }

    }

    /** 필드 직접 접근 **/
    @Test
    public void findDtoByField() {
        List<MemberDto> result = queryFactory
                .select(Projections.fields(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }

    }

    /** 생성자 사용 **/
    @Test
    public void findUserDto() {
        QMember memberSub = new QMember("memberSub");
        List<UserDto> result = queryFactory
                .select(Projections.fields(UserDto.class,
                                member.username.as("name"),

                                // 프로퍼티나, 필드 접근 생성 방식에서 이름이 다를 때 서브쿼리 내에서 ExpressionUtils를 사용.
                                ExpressionUtils.as(
                                        JPAExpressions
                                                .select(memberSub.age.max())
                                                .from(memberSub), "age")
                        )
                ).from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("memberDto = " + userDto);
        }

    }

    @Test
    public void findDtoByConstructor() {
        List<UserDto> result = queryFactory
                .select(Projections.constructor(UserDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (UserDto userDto : result) {
            System.out.println("userDto = " + userDto);
        }
    }

    @Test
    public void findDtoQueryProjection() {
        List<MemberDto> result = queryFactory
                .select(new QMemberDto(member.username, member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

    @Test
    public void dynamicQuery_BooleanBuilder() {
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember1(usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);

    }

    private List<Member> searchMember1(String usernameParam, Integer ageParam) {
        //파라미터의 값이 null인지 아닌지에 따라 쿼리가 동적으로 바뀌도록 해볼 것.

        BooleanBuilder builder = new BooleanBuilder();
        if(usernameParam != null) {
            builder.and(member.username.eq(usernameParam)); //usernameParam이 null이 아니면, BooleanBuilder에 and 조건을 넣어준다.
        }

        if(ageParam != null) {
            builder.and(member.age.eq(ageParam)); //ageParam이 null이 아니면, BooleanBuilder에 and 조건을 넣어준다.
        }

        return queryFactory
                .selectFrom(member)
                .where(builder) //builder에 나온 결과를 where절에 넣어준면 된다!
                .fetch();
    }


    @Test
    public void dynamicQuery_WhereParam() {
        String usernameParam = "member1";
        Integer ageParam = 10;

        List<Member> result = searchMember2 (usernameParam, ageParam);
        assertThat(result.size()).isEqualTo(1);


     }

     private  List<Member> searchMember2(String usernameParam, Integer ageParam) {
        return queryFactory
                .selectFrom(member)
     //           .where(usernameEq(usernameParam), ageEq(ageParam))
                .where(allEq(usernameParam,ageParam)) // 조립된 조건을 where 절에 전달해 사용할 수 있다.
                .fetch();
     }

    private BooleanExpression usernameEq(String usernameParam) { // 각각의 조건을 생성하는 메서드
        return usernameParam != null ? member.username.eq(usernameParam) : null;
    }

    private BooleanExpression ageEq(Integer ageParam) { // 각각의 조건을 생성하는 메서드
        return  ageParam != null ? member.age.eq(ageParam) : null;
    }

    private BooleanExpression allEq(String usernameParam, Integer ageParam) {
        // 개별 조건들을 조립해 한 번에 적용할 수 있다.
        return usernameEq(usernameParam).and(ageEq(ageParam));
    }

    @Test
    @Commit
    public void bulkUpdate() {
        /**
         * member1 = 10 >> member1
         * member2 = 20 >> member2
         * member3 = 30 >> member3
         * member4 = 40 >> member4
         * **/

        //update와 set을 사용하여 특정 조건에 해당하는 데이터를 수정
        long count = queryFactory
                .update(member)
                .set(member.username, "비회원") // '비회원'으로 username 변경
                .where(member.age.lt(28))  // 나이가 28 미만인 회원만!
                .execute();

        // 수정 이후 영속성 컨텍스트와 DB 간 데이터 불일치가 발생할 수 있으니, 컨텍스트 초기화가 필요하다.
        em.flush();
        em.clear();

        /** update 쿼리 실행 이후
         * member1 = 10 >> 비회원
         * member2 = 20 >> 비회원
         * member3 = 30 >> member3
         * member4 = 40 >> member4
         * **/

        List<Member> result = queryFactory
                .selectFrom(member)
                .fetch();

        for (Member member1 : result) {
            System.out.println("member1 = " + member1);
        }
    }

    @Test
    public void bulkAdd() { // 기존 값에 숫자 더하기
        long count = queryFactory
                .update(member)
                .set(member.age, member.age.add(1)) //모든 회원의 나이에 1씩 더하기.
                .execute();
    }

    @Test
    public void bulkDelete() {
        long count = queryFactory
                .delete(member)
                .where(member.age.gt(18))
                .execute();
    }

    @Test
    public void sqlFunction() {
        List<String> result = queryFactory
                .select(
                        Expressions.stringTemplate(
                                "function('replace', {0}, {1}, {2})",
                                member.username, "member", "M")) //member라는 단어를 M이라고 바꿔서 조회한다. (replace 함수 사용)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }

    }

    @Test
    public void sqlFunction2() throws Exception {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
//          .where(member.username.eq(
//                    Expressions.stringTemplate("function('lower', {0})", member.username))) // 이 기능을
                .where(member.username.eq(member.username.lower())) // 표준에 내장되어 있기 때문에, 이렇게 작성해 사용할 수 있다.
                .fetch();

        for (String s : result) {
            System.out.println("s : "+s);
        }
    }
}



