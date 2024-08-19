package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ItemRepository {

    private final EntityManager em;

    public void save(Item item) {
        if(item.getId() == null) { //jpa에 저장하기 전까지는 id값이 없으므로 (즉 새로 생성한 객체)
            em.persist(item); //persist 해줌
        } else {
            em.merge(item); // update와 비슷한 개념
        }
    }

    public Item findOne(Long id) {
        return em.find(Item.class, id);
    }

    public List<Item> findAll() {
       return em.createQuery("select i from Item i", Item.class)
                .getResultList();
    }
}
