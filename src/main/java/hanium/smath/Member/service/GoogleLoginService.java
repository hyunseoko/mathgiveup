package hanium.smath.Member.service;
import hanium.smath.Member.dto.GoogleLoginRequest;
import hanium.smath.Member.service.MemberService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import hanium.smath.Member.entity.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class GoogleLoginService {

    @Autowired
    private MemberService memberService;

    @Autowired
    private GoogleIdTokenVerifierService googleIdTokenVerifier;

    public Member processGoogleLogin(GoogleLoginRequest googleLoginRequest) throws Exception {

        String idTokenString = googleLoginRequest.getIdToken();
        System.out.println("Processing Google login with token: " + idTokenString);

        GoogleIdToken.Payload payload = googleIdTokenVerifier.verifyToken(idTokenString);

        if (payload == null) {
            throw new Exception("Invalid ID token.");
        }
        System.out.println("Token payload: " + payload);

        String userId = payload.getSubject();
        String email = payload.getEmail();
        String name = (String) payload.get("name");

        System.out.println("Google ID: " + userId);
        System.out.println("Email: " + email);
        System.out.println("Name: " + name);

        Member member = memberService.findByGoogleId(userId);

        if(member == null) {
            System.out.println("No existing member found. Creating new member.");

            member = new Member();
            member.setGoogleId(userId);
            member.setEmail(email);
            member.setNickname(name);
            member.setLoginId(null); // loginId를 null로 설정
            member.setLoginPwd(null); // loginPwd를 null로 설정
            member.setEmailVerified(true);

            // 최초 로그인 시, 생년월일을 받는 코드
            LocalDate birthdate = LocalDate.parse(googleLoginRequest.getBirthDate());
            member.setBirthdate(birthdate);

            // 생년월일을 기반으로 학년 계산

            memberService.save(member);

            System.out.println("Member created.");
            System.out.println("grade : " + member.getGrade());
            System.out.println("birthDate : " + member.getBirthdate());
        } else {
            System.out.println("Existing member found: " + member.getNickname());
        }

        return member;
    }
}
