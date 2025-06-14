package hanium.smath.Member.service;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@Service("customMemberDetailsService")
public class CustomMemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Autowired
    public CustomMemberDetailsService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Member member = memberRepository.findByLoginId(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with loginId: " + username));

        List<GrantedAuthority> authorities = new ArrayList<>();

        // 아이디가 "admin"일 경우 관리자로 설정하고, USER 권한을 생략
        if ("admin".equals(username)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        } else {
            // 기본 권한을 USER로 설정
            authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        }

        System.out.println("User found: " + member.getLoginId() + ", with authorities: " + authorities);

        return new User(member.getLoginId(), member.getLoginPwd(), authorities);
    }
}
