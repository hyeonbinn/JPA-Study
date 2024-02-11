package hellojpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
//@Table(name = "MBR")
public class Member {
    @Id
    private Long id;

    private String name;
    private int age;
    //private int haha; //validate에서 걸릴 필드

    public Member(long id, String name) {
        this.id = id;
        this.name = name;
    }
    public Member() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
