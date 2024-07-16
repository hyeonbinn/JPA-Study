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

            Team team = new Team();
            team.setName("teamA");
            em.persist(team);

            Member member1 = new Member();
            member1.setName("member1");
            member1.setTeam(team);
            em.persist(member1);

            em.flush();
            em.clear();

            //Member m = em.find(Member.class, member1.getId());

            /** EAGER로 설정 시 JPQL에서 N+1 문제 발생
            MEMBER와 TEAM을 같이 조회해야 한다면, JPQL에서 fetch join을 사용해야 함 **/
            List<Member> members = em.createQuery("select m from Member m", Member.class).getResultList();

            //SQL : select * from Member
            //SQL : select * from TEAM where TEAM_ID = xxx

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