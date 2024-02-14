package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager(); //여기에서 EntityManager를 꺼내고, 아래부터 실제 동작하는 코드를 작성

        EntityTransaction tx = em.getTransaction();
        tx.begin(); // transaction 시작

        try {

            Member member = new Member();

            //우리가 직접 Id에 값을 넣으면 안됨 (DB에서 Id를 null로 인식하면, 값을 세팅)

            member.setUsername("C");

            System.out.println("=========================");
            em.persist(member); //얘를 호출한 시점에 DB에 insert 쿼리를 날림
            System.out.println("=========================");

            tx.commit();

        } catch (Exception e) {
            tx.rollback(); // 문제가 생가면 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}