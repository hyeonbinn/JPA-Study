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
3. 복잡한 실무환경에서 사용하기에는 한계가 명확하다.

 