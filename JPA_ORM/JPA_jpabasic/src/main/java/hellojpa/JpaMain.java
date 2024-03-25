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
            //팀 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);


            //회원 저장
            Member member = new Member();
            member.setName("member1");
            member.setTeam(team); //jpa가 team에서 pk를 꺼내서 fk로 사용함.
            em.persist(member);

            Member findMember = em.find(Member.class, member.getId());
            Team findTeam = findMember.getTeam(); //팀 아이디 찾아 쓰는 게 아니라 바로 사용할 수 있음.
            System.out.println("findTemd = " + findTeam.getName());

            /**다른 팀으로 변경하고 싶을 때
            Team newTeam = em.find(Team.class, 100L); //DB에 100이 있다고 가정했을 때.
            findMember.setTeam(newTeam); //db에서 fk가 업데이트 됨.**/

            tx.commit();
        } catch (Exception e) {
            tx.rollback(); // 문제가 생가면 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}