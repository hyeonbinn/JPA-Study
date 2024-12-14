## 스프링 데이터 JPA가 제공하는 Querydsl 기능

> 제약이 커서 복잡한 실무 환경에서 사용하기에는 많이 부족한 기능들이나, 스프링 데이터에서 제공하기에 간단히 소개. <br>

### 인터페이스 지원 - QuerydslPredicateExecutor
<QuerydslPredicateExecutor 인터페이스>
- 스프링 데이터 JPA 메서드 Predicate. 파라미터에 Querydsl의 where 조건을 넣을 수 있다.
- QuerydslPredicateExecutor 또한 Pagable, Sort를 모두 지원하고 정상 동작한다.

<한계점>
1. 조인을 사용할 수 없다. (묵시적 조인은 가능하지만, left 조인이 불가능하다.)
2. 클라이언트가 Querydsl에 의존해야 한다. >> 즉 서비스 클래스가 Querydsl이라는 구현 기술에 의존해야 한다.
   - 리포지토리를 만드는 이유는 querydsl과 같은 구체화된 기능을 하부에 숨기기 위해. (기능이 바뀌도 그 부분만 바뀌면 되기에)
   - 하지만 이 상황에서는 서비스, 컨트롤러 로직이 querydsl에 의존하고 있기에 좋지 않다. (같은 순수한 자바 클래스를 넘기는 게 아니라 querydsl를 만들어 넘겨야 함)
3. 복잡한 실무환경에서 사용하기에는 한계가 명확하다.<br>
<br>

### Querydsl Web 지원
[공식 URL 참고](https://docs.spring.io/spring-data/jpa/docs/2.2.3.RELEASE/reference/html/#core.web.type-safe)

- @QuerydslPredicate(root= User.class)를 넘겨주면 요청 파라미터 정보를 predicate로 변환해서 받게 된다.
  - 예를 들어 ?firstname=Dave&lastname=Matthews >> 이런식으로 파라미터를 넘겨주면
  - QUser.user.firstname.eq("Dave").and(QUser.user.lastname.eq("Matthews")) >> 와 같이 predicate를 만들어서 파라미터 바인딩을 해준다.

<한계점>
1. 단순한 조건만 가능 (거의 eq만 가능하다고 보면 된다)
2. 조건을 커스텀하는 기능이 복잡하고 명시적이지 않다.
3. 컨트롤러가 Querydsl에 의존한다.
-> 복잡한 실무 환경에서 사용하기에는 한계가 명확. 그냥 쓰지 말자..!! <br>
<br>

### 리포지토리 지원 - QuerydslRepositorySupport

> public class MemberRepositoryImpl extends QuerydslRepositorySupport implements MemberRepositoryCustom {
<br>
> <br>public MemberRepositoryImpl(){ <br>
        super(Member.class);<br>
    }<br>
<br>

[장점]
1. getQuerydsl().applyPagination() 스프링 데이터가 제공하는 페이징을 Querydsl로 편리하게 변환 가능 (단! Sort는 오류발생)
2. from()으로 시작 가능(최근에는 QueryFactory를 사용해서 select() 로 시작하는 것이 더 명시적)
3. 페이징 처리가 편하다. 
   - getQuerydsl() 을 사용하여 기존에 페이지 처리에 쓰이던 offset, limit 등을 생략해 사용할 수 있다.
3. EntityManager 제공<br>
<br>

[한계점]
1. Querydsl 3.x 버전을 대상으로 만듬
2. Querydsl 4.x에 나온 JPAQueryFactory로 시작할 수 없음
3. select로 시작할 수 없음 (from으로 시작해야함) QueryFactory 를 제공하지 않음
4. 스프링 데이터 Sort 기능이 정상 동작하지 않음<br>
   <br>

