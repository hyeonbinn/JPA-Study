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
