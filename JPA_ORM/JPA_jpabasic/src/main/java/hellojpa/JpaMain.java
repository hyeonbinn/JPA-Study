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
            member.setName("hello");

            em.persist(member);

            em.flush();
            em.clear();

            //Member findMember = em.find(Member.class, member.getId());
            Member findMember = em.getReference(Member.class, member.getId()); //getReference : 가짜 엔티티 조회

            //System.out.println("findMember = " + findMember.getClass());
            //System.out.println("findMember.id = " + findMember.getId());
            System.out.println("findMember.username = " + findMember.getName()); //1번째 : DB에 없으므로 쿼리를 날려 객체를 가져옴
            System.out.println("findMember.username = " + findMember.getName()); //2번째 : 타겟이 이미 값이 있으므로 바로 출력

            tx.commit();
        } catch (Exception e) {
            tx.rollback(); // 문제가 생가면 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}