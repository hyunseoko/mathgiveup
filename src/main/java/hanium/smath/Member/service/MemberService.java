package hanium.smath.Member.service;

import hanium.smath.Member.entity.EmailVerification;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.entity.Rank;
import hanium.smath.Member.repository.MemberRepository;
import hanium.smath.Member.repository.EmailVerificationRepository;
import hanium.smath.Member.dto.SignupRequest;
import hanium.smath.Member.dto.KakaoProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.*;


@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final EmailService emailService;

    //회원가입
    @Autowired
    public MemberService(MemberRepository memberRepository, EmailVerificationRepository emailVerificationRepository, EmailService emailService) {
        this.memberRepository = memberRepository;
        this.emailVerificationRepository = emailVerificationRepository;
        this.emailService = emailService;
    }

    public boolean checkLoginIdExists(String loginId) {
        return memberRepository.existsByLoginId(loginId);
    }
    public boolean checkEmailExists(String email) {
        return memberRepository.existsByEmail(email);
    }
    public void sendVerificationCodeToEmail(String email) {
        emailService.sendVerificationCodeToEmailOnly(email);
    }
    public boolean verifyEmailCode(String email, int code) {
        return emailService.verifyEmailCodeByEmail(email, code);
    }
    public void registerMember(SignupRequest signupRequest) {
        EmailVerification emailVerification = emailVerificationRepository.findEmailVerificationByEmail(signupRequest.getEmail());

        if (emailVerification == null) {
            throw new IllegalArgumentException("Email verification record not found for email: " + signupRequest.getEmail());
        }

        LocalDate birthdate = LocalDate.parse(signupRequest.getBirthdate());

        Member member = Member.builder()
                .email(emailVerification.getEmail())
                .loginId(signupRequest.getLoginId())
                .loginPwd(signupRequest.getLoginPwd())
                .name(signupRequest.getName())
                .nickname(signupRequest.getNickname())
                .birthdate(birthdate)
                .school(signupRequest.getSchool())
                .grade(signupRequest.getGrade())
                .isEmailVerified(true) // 이메일 인증 완료로 설정
                .icon("assets/images/icon1.png")
                .build();

        memberRepository.save(member);
    }

    // 회원 삭제
    @Transactional
    public void deleteCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserLoginId = authentication.getName(); // JWT에서 추출된 사용자 loginId

        // loginId로 사용자 조회
        Member member = memberRepository.findByLoginId(currentUserLoginId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with loginId: " + currentUserLoginId));
        memberRepository.delete(member);

        // 해당 사용자의 이메일로 이메일 인증 정보 삭제
        emailVerificationRepository.deleteByEmail(member.getEmail());
    }

    // 닉네임 변경
    @Transactional
    public void changeNickname(String newNickname) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserLoginId = authentication.getName(); // JWT에서 추출된 사용자 loginId

        Member member = memberRepository.findByLoginId(currentUserLoginId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with loginId: " + currentUserLoginId));
        member.setNickname(newNickname);
        memberRepository.save(member);
    }

    // 아이콘 변경
    @Transactional
    public void changeIcon(String newIcon) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserLoginId = authentication.getName(); // JWT에서 추출된 사용자 loginId

        Member member = memberRepository.findByLoginId(currentUserLoginId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with loginId: " + currentUserLoginId));

        member.setIcon(newIcon);
        memberRepository.save(member);
    }

    //로그인
    // 로그인 ID를 통해 사용자를 조회
    public Member getMemberById(String loginId) {
        System.out.println("Fetching member by loginId: " + loginId);
        return memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid login_id: " + loginId));
    }


    public Member findByGoogleId(String googleId) throws ExecutionException, InterruptedException, TimeoutException {
        // 이 부분은 Firebase에서 MySQL로 변경 필요
        System.out.println("Fetching member by googleId: " + googleId);
        return memberRepository.findByGoogleId(googleId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid google_id: " + googleId));
    }

    public void save(Member member) {
        System.out.println("Saving member with loginId: " + member.getLoginId());
        memberRepository.save(member);
    }

    public String findLoginId(String email, LocalDate birthdate) {
        System.out.println("Searching for loginId with email: " + email + " and birthdate: " + birthdate);
        return memberRepository.findByEmailAndBirthdate(email, birthdate)
                .orElseThrow(() -> new RuntimeException("No member found with provided email and birth date."))
                .getLoginId();
    }

    // 사용자가 존재하는지 확인
    public boolean checkUserExists(String loginId) {
        return memberRepository.findByLoginId(loginId).isPresent();
    }

    public boolean changeUserPassword(String email, String newPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return memberRepository.findByEmail(email)
                .map(member -> {
                    String encodedPassword = passwordEncoder.encode(newPassword);  // 비밀번호 암호화
                    member.setLoginPwd(encodedPassword);
                    memberRepository.save(member);
                    return true;
                })
                .orElse(false);
    }

    // 카카오 ID로 사용자를 조회하는 메서드 추가
    public Member findByKakaoId(String kakaoId) {
        System.out.println("Fetching member by kakaoId: " + kakaoId);
        return memberRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid kakao_id: " + kakaoId));
    }

    // 카카오 사용자 정보를 기반으로 회원을 생성하고 저장하는 메서드 추가
    public Member processKakaoLogin(KakaoProfile kakaoProfile) {
        String kakaoId = String.valueOf(kakaoProfile.getId());

        // 카카오 ID로 기존 사용자가 있는지 확인
        return memberRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> {
                    // 존재하지 않으면 새로운 회원 생성
                    Member newMember = new Member();
                    newMember.setKakaoId(kakaoId);
                    newMember.setLoginId(kakaoId); // 이 부분은 적절히 수정 필요
                    newMember.setEmail(kakaoProfile.getKakaoAccount().getEmail());
                    newMember.setNickname(kakaoProfile.getKakaoAccount().getProfile().getNickname());
                    // 필요한 추가 정보 설정
                    save(newMember);
                    return newMember;
                });
    }

    @Transactional
    public Member updateSkillScoreAndRank(String loginId, int newScore) {
        // 회원 조회
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found with loginId: " + loginId));

        // 새로운 스킬 점수 설정
        member.setSkillScore(newScore);

        // Rank 계산
        Rank newRank = calculateRank(newScore);
        member.setRank(newRank);

        // 변경 사항 저장
        memberRepository.save(member);

        return member;
    }

    // Rank 계산 로직
    private Rank calculateRank(int skillScore) {
        if (skillScore >= 10) {
            return Rank.GOLD;
        } else if (skillScore >= 7) {
            return Rank.SILVER;
        } else if (skillScore >= 3) {
            return Rank.BRONZE;
        } else {
            throw new IllegalArgumentException("Invalid skill score");
        }
    }

    public void updateSkillScore(String loginId, int newScore) {
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.setSkillScore(newScore);  // 새로운 점수 설정 (수정됨)
        memberRepository.save(member);
    }



    //알림
    public List<Member> findMembersInactiveFor24Hours() {
        LocalDateTime timeLimit = LocalDateTime.now().minusHours(24);
        Timestamp timestamp = Timestamp.valueOf(timeLimit);
        return memberRepository.findMembersInactiveFor24Hours(timestamp);
    }

    public void updateLastLoginTime(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        // 현재 시간을 LocalDateTime으로 가져온 후 Timestamp로 변환
        LocalDateTime currentTime = LocalDateTime.now();
        member.setLastLoginTime(Timestamp.valueOf(currentTime));

        memberRepository.save(member);
    }

    public LocalDateTime getLastLoginTime(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid member ID"));

        // Timestamp를 LocalDateTime으로 변환
        Timestamp lastLoginTime = member.getLastLoginTime();
        return (lastLoginTime != null) ? lastLoginTime.toLocalDateTime() : null;
    }
}
