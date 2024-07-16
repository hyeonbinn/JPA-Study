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

            em.flush();
            em.clear();

//            Member reality = em.find(Member.class, member1.getId());
//            System.out.println("reality = " + reality.getClass());

            Member reference = em.getReference(Member.class, member1.getId());
            System.out.println("reference = " + reference.getClass());

            //em.detach(reference); //reference를 영속성 컨텍스트에서 관리 안 해!
            em.close(); //영속성 컨텍스트를 끔

            reference.getName();

            tx.commit();
        } catch (Exception e) {
            tx.rollback(); // 문제가 생가면 롤백
            e.printStackTrace();
        } finally {
            em.close();
        }
        emf.close();
    }
}