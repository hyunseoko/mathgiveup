package hanium.smath.Member.interceptor;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.repository.MemberRepository;
import hanium.smath.Member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;

@Component
public class LastLoginTimeInterceptor implements HandlerInterceptor {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    public LastLoginTimeInterceptor(MemberService memberService, MemberRepository memberRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        if (token != null && !token.isEmpty()) {
            String loginId = extractLoginIdFromToken(token);

            Optional<Member> memberOpt = memberRepository.findByLoginId(loginId);
            if (memberOpt.isPresent()) {
                Member member = memberOpt.get();
                memberService.updateLastLoginTime((long) member.getIdMember());
            }
        }

        return true;  // 요청을 계속 진행
    }

    private String extractLoginIdFromToken(String token) {
        return "parsedLoginId";  // JWT 파싱 로직 필요
    }
}