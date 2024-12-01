## 프로젝션 
#### : 쿼리에서 필요한 특정 필드나 컬럼만 선택적으로 조회하는 것을 의미한다. (select 대상 지정)

- 프로젝션 대상이 하나면 >> 타입을 명확하게 지정할 수 있음
- 프로젝션 대상이 둘 이상이면 >> 튜플이나 DTO로 조회 

<br/>

#### 바깥 계층으로 내보낼 때는 tuple말고 DTO로 던져주자!!
- tuple은 com.querydsl 패키지에 속해있다.
- Repository 계층 내부에서 tuple을 사용하는 것은 문제가 없으나, 그 이상의 레벨 (controller, service 등)로 전달하면 구현 기술(예를 들어 QueryDSL을 사용한다..)이 노출되어 추상화 원칙을 위배하게 된다.
- 즉 tuple을 repository 계층 안에서만 사용하도록 하여 하부 기술을 다른 것으로 바꾸더라도 외부에 영향을 주지 않도록 하는 것이 좋다.
>즉, 외부 계층으로 데이터를 전달할 때는 DTO를 사용해 기술 독립성을 유지해야 한다.

<br/>

### 프로젝션 결과 반환을 DTO로 하는 방법

#### 순수 JPA에서 DTO 조회 코드
- 순수 JPA에서 DTO를 조회할 때는 new 명령어를 사용해야 한다.
- DTO의 package이름을 다 적어줘야해서 지저분하다.
- 생성자 방식만 지원한다. (세터,필드 주입 X ) <br/>
  <br/>

#### Querydsl DTO 조회 코드
> Querydsl DTO 조회 코드는 3가지 방법 모두를 지원한다.
1. 프로퍼티 접근
2. 필드 직접 접근
3. 생성자 사용 
   <br> <br>

#### Setter를 이용한 프로퍼티 접근
- Projections.bean을 사용해 DTO에 데이터를 매핑한다. (반환하고자 하는 타입, 프로젝션할 항목들 순서로 써주면 된다.)
- 조회해보면, 딱 필요한 member, age만 최적화해서 가져오는 것을 확인할 수 있다.
  <br> <br>

#### 필드 직접 접근
- Projections.fields를 사용해 필드에 직접 값을 할당한다. getter나 setter 없이 값을 넣을 수 있다.
- 필드 이름이 DTO와 일치해야 하며, 일치하지 않으면 null로 반환된다. 
- 이름이 다른 경우 .as("필드명") 을 작성해 매핑이 가능하다.
- 서브쿼리나 복잡한 표현식을 사용할 때는 ExpressionUtils.as()를 활용할 수 있다.
<br><br>

#### 생성자 사용
- Projections.constructor를 사용해 생성자를 통해 값을 주입한다.
- 필드 이름이 아닌 타입 기반으로 데이터를 매핑하므로 이름 매칭 오류를 방지할 수 있다.

<br/>

### 프로젝션과 결과 반환 - @QueryProjection
- MemberDto에 바로 @QueryProjection을 적어주고, Gradle > other > compileQuerydsl을 실행하면,
- DTO도 Q파일로 생성이 된다!! (DTO를 Q객체화 해서 사용 가능)
- 이를 사용할 때 생성자를 그대로 가져오기 때문에 타입 체크를 할 수 있어 안정적으로 코드를 작성할 수 있다. (cmd+p) <br>
<br>
- @QueryProjection과 constructor의 차이점은?
  - constructor는 컴파일 시점에 오류를 잡지 못하고, 런타임에 오류를 잡는다.
  - 똑같은 문제를 @QueryProjection를 사용해 해결하면, 컴파일 시점에 오류를 잡아준다. <br>
<br>
- 단점
  - DTO까지 Q파일을 생성해야 하는 점.
  - Querydsl에 대한 의존성을 가지게 된다는 점.

<br/>

### 동적 쿼리를 해결하는 방법
1. BooleanBuilder를 사용하는 방법
2. Where문 안에 다중 파라미터를 사용하는 방법

#### BooleanBuilder를 사용하는 방법
- 우선 BooleanBuilder를 만들어야 한다.(초기 조건을 넣어줄 수도 있다.)
- 동적 쿼리를 and, or를 통해 유연하게 처리할 수 있다.