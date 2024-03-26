package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager(); //여기에서 EntityManager를 꺼내고, 아래부터 실제 동작하는 코드를 작성

        EntityTransaction tx = em.getTransaction();
        tx.begin(); // transaction 시작

        try {
           Member member = new Member();
           member.setName("member1");

           em.persist(member);

           Team team = new Team();
           team.setName("teamA");
           //외래키가 member 테이블에 있기에 update 쿼리가 member에 나감.
           team.getMembers().add(member);

           em.persist(team);

            tx.commit();
        } catch (Exception e) {
            tx.rollback(); // 문제가 생가면 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}