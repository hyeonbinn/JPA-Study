package hellojpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.Persistence;

import java.util.List;
import java.util.Set;


public class JpaMain {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");

        EntityManager em = emf.createEntityManager(); //여기에서 EntityManager를 꺼내고, 아래부터 실제 동작하는 코드를 작성

        EntityTransaction tx = em.getTransaction();
        tx.begin(); // transaction 시작

        try {

            Member member = new Member();
            member.setUsername("member1");
            member.setHomeAddress(new Address("homeCity", "street", "10000"));

            member.getFavoriteFoods().add("육회");
            member.getFavoriteFoods().add("명란");
            member.getFavoriteFoods().add("초밥");

            member.getAddressHistory().add(new AddressEntity("old1", "street", "10000"));
            member.getAddressHistory().add(new AddressEntity("old2", "street", "10000"));

            em.persist(member);

            em.flush();
            em.clear();

            System.out.println("========= START =============");
            Member findMember = em.find(Member.class, member.getId());

            //homeCity -> newCity로 수정 시
            //findMember.getHomeAddress().setCity("newCity"); //이렇게 쓰면 안됨

            //이렇게 값 타입 자체를 통으로 새로 넣어야 함
//            Address a = findMember.getHomeAddress();
//            findMember.setHomeAddress(new Address("newCity", a.getStreet(), a.getZipcode()));
//
//            //치킨 -> 한식
//            findMember.getFavoriteFoods().remove("육회");
//            findMember.getFavoriteFoods().add("피자");

//            findMember.getAddressHistory().remove(new AddressEntity("old1", "street","10000"));
//            findMember.getAddressHistory().add(new AddressEntity("newCity1", "street","10000"));
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