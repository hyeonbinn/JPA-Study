package hellojpa;
import jakarta.persistence.*;

@Entity
@SequenceGenerator(
        name = "MEMBER_SEQ_GENERATOR",
        sequenceName = "MEMBER_SEQ",
        initialValue = 1, allocationSize = 50) //50개를 미리 DB에 올려놓고 사용
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                   generator = "MEMBER_SEQ_GENERATOR")
    private Long id;
    @Column(name = "name", nullable = false)
    private String username;

    public Member(){

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}