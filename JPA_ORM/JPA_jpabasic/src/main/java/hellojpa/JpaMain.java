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

            Member reality = em.find(Member.class, member1.getId());
            System.out.println("reality = " + reality.getClass());

            Member reference = em.getReference(Member.class, member1.getId());
            //위에서 이미 em.find를 했기에 엔티티가 이미 있으므로 em.getReference()를 호출해도 실제 엔티티가 반환됨.
            System.out.println("reference = " + reference.getClass());

            System.out.println("reality == reference : " + (reality == reference)); //jpa에서는 이를 항상 true가 되도록 보장함.



            tx.commit();
        } catch (Exception e) {
            tx.rollback(); // 문제가 생가면 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}