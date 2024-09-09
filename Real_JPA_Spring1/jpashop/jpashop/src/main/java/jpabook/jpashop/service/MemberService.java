package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {


//    @Autowired
//    private MemberRepository memberRepository; //필드 주입 : 테스트 및 유지보수가 어려워짐. 의존성을 주입할 수 없는 객체 생성 시, 주입이 불가능.

//    @Autowired
//    public void setMemberRepository(MemberRepository memberRepository) { //세터 주입 : 객체 생성 시, 의존성이 완전히 주입되지 않은 상태일 수 있어 불완전한 상태에서 사용될 가능성 존재.
//        this.memberRepository = memberRepository;
//    }

    //생성자 주입 : 권장 >> 객체 생성 시, 모든 의존성이 주입되므로 객체가 완전한 상태로 보장됨
    private final MemberRepository memberRepository; //final로 해줘야 좋음. (컴파일 시점에 체크가 가능)

    /** >> 이걸 만들어 주는 것이 @AllArgsConstructor
     >> final 붙은 필드만 가지고 생성자를 만들어 주는 것이 @RequiredArgsConstructor
     아래 코드를 위의 어노테이션으로 대체 가능 */
//    public MemberService(MemberRepository memberRepository) {
//        this.memberRepository = memberRepository;
//    }

    /**
     * 회원 가입
     */
    @Transactional
    public Long join(Member member) {

        validateDuplicateMember(member); //중복 회원 검증
        memberRepository.save(member);
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if (!findMembers.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }


    /**
     * 회원 전체 조회
     * */
    @Transactional(readOnly = true) //조회에서는 readOnly를 활성화하면 성능이 올라감
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 단건 조회
     * */
    @Transactional(readOnly = true) //조회에서는 readOnly를 활성화하면 성능이 올라감
    public Member findOne(Long memberId) {
        return memberRepository.findOne(memberId);
    }

    /**
     * 회원 수정
     */
    @Transactional
    public void update(Long id, String name) {
        Member member = memberRepository.findOne(id);
        member.setName(name);
    }

}