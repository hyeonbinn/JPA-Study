### 스프링 데이터 JPA와 Querydsl

#### 스프링 데이터 JPA리포지토리로 변경
- 인터페이스로 리포지토리를 만들어 사용하고 JpaRepository

#### 사용자 정의 리포지토리
- 스프링 데이터 JPA와 Querydsl을 함께 사용하는 기본적인 방법은 사용자 정의 리포지토리를 상속하는 것이다.
- 하지만 쿼리가 조금 더 복잡해지거나 커스텀 해서 쓸 기능이 있다면, 사용자 정의 리포지토리를 만들어 사용하면 된다.
- 스프링 데이터 JPA 리포지토리는 인터페이스이기 때문에 Querydsl 기능으로 메서드를 정의할 수 없기에 -> 따로 사용자 정의 리포지토리를 상속해야 한다.
- 이 떄 사용자 정의 인터페이스 구현체 클래스명은 아래 중 하나로 정의해야 한다.
  - 스프링 데이터 JPA 인터페이스명 + "Impl" : MemberRepositoryImpl
  - 사용자 정의 인터페이스명 + "Impl" : MemberRepositoryCustomImpl
- MemberRepository에 JpaRepository 이외에 MemberRepositoryCustom을 상속 받아 사용하면, MemberRepository로 Spring data JPA와 Querydsl 기능을 함께 사용할 수 있게 된다.<br>
<br>

#### 스프링 데이터 페이징 활용1 - Querydsl 페이징 연동
- 페이징을 위해 스프링 데이터의 Page, Pageable을 Querydsl에서 활용할 수 있다.

- 1. 전체 카운트를 한 번에 조회하는 단순한 방법
  - pageable에서 offset, limit 정보를 추출해서 쿼리 조건에 추가한다.
  - fetchResults()를 사용하면 querydsl이 알아서 totalCountquery를 날려준다. (내용과 전체 카운트를 한 번에 조회할 수 있다. (실제 쿼리가 2번 호출 된다))
 
- 2. 데이터 내용과 전체 카운트를 별도로 조회하는 방법
  - 전체 카운트를 구할 때는 조인과 같은 성능에 영향을 주는 쿼리가 필요 없을 때가 있다. 
  - 그럴 때는 카운트 쿼리를 따로 분리하면 된다. 
    - 1번 방법은 querydsl이 알아서 totalCountquery를 날려줬다면, 이 방법에는 totalQuery를 직접 날리는 것.
    - 카운트 쿼리를 한 번에 조회하면, 최적화를 하지 못한다는 단점이 있다. 즉 카운트 쿼리를 최적화하고 싶다면, 별도로 날려주는 것이 좋다.
  - +) 코드를 리팩토링해서 내용 쿼리와 전체 카운트 쿼리를 읽기 좋게 분리하면 좋다.<br>
    <br>

#### 스프링 데이터 페이징 활용2 - CountQuery 최적화
- 때에 따라서는 countQuery를 생략할 수 있다.
- 스프링 데이터 라이브러리가 아래와 같이 count 쿼리가 생략 가능한 경우 생략해서 처리한다.
  1. 페이지 시작이면서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때
  2. 마지막 페이지 일 때 (offset + 컨텐츠 사이즈를 더해서 전체 사이즈 구함, 더 정확히는 마지막 페이지이면 서 컨텐츠 사이즈가 페이지 사이즈보다 작을 때

> return PageableExecutionUtils.getPage(content, pageable, () -> countQuery.fetchCount());
- countQuery에서는 fetchCount를 해줘야 실제 countQuery가 날라가는데,
- content, pageable를 보고 count 쿼리가 생략 가능한 경우에는 getPage안에서 마지막 파라미터인 () -> countQuery.fetchCount() 함수 호출을 하지 않는다.

 