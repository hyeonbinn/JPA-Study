package study.querydsl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import study.querydsl.entity.Hello;
import study.querydsl.entity.QHello;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Commit
class QuerydslApplicationTests {

	@Autowired
	EntityManager em;

	@Test
	void contextLoads() {
		Hello hello = new Hello();
		em.persist(hello);

		JPAQueryFactory query = new JPAQueryFactory(em);

		//QueryDS을 사용시 쿼리와 관련된 것을 사용할 때는 Q타입을 사용해야 함.
		QHello qHello = QHello.hello; // Q타입 동작 확인
		Hello result = query
				.selectFrom(qHello)
				.fetchOne();
		assertThat(result).isEqualTo(hello);

		//lombok 동작 확인
		assertThat(result.getId()).isEqualTo(hello.getId());
	}

}



