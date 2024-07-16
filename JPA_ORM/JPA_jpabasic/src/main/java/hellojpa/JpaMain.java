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

            Member member1 = new Member();
            member1.setName("member1");
            em.persist(member1);

            Member member2 = new Member();
            member2.setName("member2");
            em.persist(member2);

            em.flush();
            em.clear();

            Member m1 = em.find(Member.class, member1.getId());
            Member m2 = em.getReference(Member.class, member2.getId()); //getReference : 가짜 엔티티 조회


            System.out.println("m1 == m2 : " + (m1.getClass() == m2.getClass())); // false 출력 : 프록시인 객체와 프록시가 아닌 객체는 타입이 맞지 않음
            System.out.println("m1 instanceof m2 : " + (m1 instanceof Member)); // true 출력
            System.out.println("m1 instanceof m2 : " + (m2 instanceof Member)); // true 출력

            tx.commit();
        } catch (Exception e) {
            tx.rollback(); // 문제가 생가면 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}