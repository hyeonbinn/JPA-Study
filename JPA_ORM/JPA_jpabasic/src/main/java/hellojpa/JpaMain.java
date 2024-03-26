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
            //팀 저장
            Team team = new Team();
            team.setName("TeamA");
            em.persist(team);

            //회원 저장
            Member member = new Member();
            member.setName("member1");
            member.setTeam(team); //연관관계 주인에 값을 넣어야 함.
            em.persist(member);

            team.getMembers().add(member); //순수 객체 관계를 고려하면 사실 양쪽에 값을 모두 세팅하는 게 맞음

            /**
             * Member member = new Member();
* *            member.setName("member1");
* *            em.persist(member);
* *
             * Team team = new Team();
*             team.setName("TeamA");
              team.getMembers().add(member); //이렇게하면 안됨! => TEAM_ID가 null이 됨. mappedBy이므로 읽기만 가능. 변경할 때 아예 안 봄.
*             em.persist(team);
             **/

            em.flush();
            em.clear();

            Member findMember = em.find(Member.class, member.getId());

            List<Member> members = findMember.getTeam().getMembers();

            for (Member m : members) {
                System.out.println("m = " + m.getName());
            }

            tx.commit();
        } catch (Exception e) {
            tx.rollback(); // 문제가 생가면 롤백
        } finally {
            em.close();
        }
        emf.close();
    }
}