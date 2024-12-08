### QueryDSL의 장점
[ JPQL 대비 장점 ]
- 컴파일 시점 오류 검출: JPQL은 실행 시점에 오류를 발견하지만, QueryDSL은 컴파일 시점에 검출 가능하다. 
- 직관적이고 간결한 코드: 메서드 체인을 활용해 쿼리를 작성하므로 가독성이 높아진다.
- 파라미터 바인딩 처리 자동화: QueryDSL은 파라미터를 직접 전달받아 처리하므로 바인딩 코드가 줄어든다. <br/>
<br/>

#### 리포지토리에 QueryDSL 적용
- EntityManager: JPA의 핵심 인터페이스로 데이터베이스와 상호작용. 
- JPAQueryFactory: QueryDSL에서 쿼리를 작성하고 실행하는 중심 객체.
- JPAQueryFactory를 스프링 컨텍스트에 빈으로 등록하면 각 리포지토리에서 생성자 주입으로 사용 가능하다. <br/>
  <br/>

### 복잡한 조건 처리: BooleanBuilder 사용
[ BooleanBuilder를 활용한 동적 쿼리 ]

동적 조건 필요성
- 조건이 고정적이지 않고 입력 값에 따라 달라질 경우 BooleanBuilder를 사용해 유연하게 쿼리를 작성한다.

BooleanBuilder의 장점
- 동적 조건 추가: 조건이 null인지 확인 후 and 또는 or로 유연하게 추가 가능하다.
- 코드 재사용성: BooleanBuilder는 조건을 메서드로 분리해 다른 쿼리에서도 재사용 가능하다. <br/>
  <br/>

### QueryDSL과 동시성 문제
QueryDSL 동작 방식
- JPAQueryFactory는 EntityManager를 내부적으로 사용한다.
- 스프링에서 제공하는 EntityManager는 트랜잭션 단위로 분리되어 동작하므로 Thread-safe.
> QueryDSL은 멀티스레드 환경에서도 동시성 문제가 없음.
