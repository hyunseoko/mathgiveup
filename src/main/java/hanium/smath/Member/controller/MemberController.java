package hanium.smath.Member.controller;

import hanium.smath.Member.dto.*;
import hanium.smath.Member.repository.EmailVerificationRepository;
import hanium.smath.Member.entity.EmailVerification;
import hanium.smath.Member.entity.Member;
import hanium.smath.Member.security.JwtUtil;
import hanium.smath.Member.service.EmailService;
import hanium.smath.Member.service.GoogleLoginService;
import hanium.smath.Member.service.KakaoService;
import hanium.smath.Member.service.MemberService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.service.annotation.GetExchange;

@RestController
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final GoogleLoginService googleLoginService;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final KakaoService kakaoService;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public MemberController(MemberService memberService, GoogleLoginService googleLoginService, JwtUtil jwtUtil, EmailService emailService, KakaoService kakaoService, EmailVerificationRepository emailVerificationRepository, PasswordEncoder passwordEncoder) {
        this.memberService = memberService; // controller가 생성될 때 service 주입하기
        System.out.println("MemberController instantiated with MemberService");
        this.googleLoginService = googleLoginService;
        System.out.println("MemberController instantiated with googleLoginService");
        this.jwtUtil = jwtUtil;
        System.out.println("MemberController instantiated with jwtUtil");
        this.emailService = emailService;
        System.out.println("MemberController instantiated with email");
        this.kakaoService = kakaoService;
        System.out.println("MemberController instantiated with kakaoService");
        this.emailVerificationRepository = emailVerificationRepository;
        System.out.println("MemberController instantiated with emailVerificationRepository");
        this.passwordEncoder = passwordEncoder;
    }

    //회원가입
    //아이디 중복 확인
    @PostMapping("/check-loginId")
    public ResponseEntity<String> checkLoginId(@RequestParam String loginId) {
        if (memberService.checkLoginIdExists(loginId)) {
            return ResponseEntity.badRequest().body("Login ID already exists");
        } else {
            return ResponseEntity.ok("Login ID is available");
        }
    }

    //이메일 중복 확인 후 인증 메일 전송
    @PostMapping("/check-email")
    public ResponseEntity<String> checkEmail(@RequestParam String email) {
        if (memberService.checkEmailExists(email)) {
            return ResponseEntity.badRequest().body("Email already exists");
        } else {
            // 새로운 인증 코드 생성
            int newCode = emailService.generateVerificationCode();

            // 이메일로 기존의 인증 레코드를 가져옴
            Optional<EmailVerification> optionalVerification = emailVerificationRepository.findTopByEmailOrderByCreateTimeDesc(email);

            if (optionalVerification.isPresent()) {
                // 기존 레코드가 있으면 인증번호만 업데이트
                EmailVerification existingVerification = optionalVerification.get();
                existingVerification.setVerificationCode(newCode);
                existingVerification.setCreateTime(LocalDateTime.now()); // 생성 시간 갱신
                emailVerificationRepository.save(existingVerification);
                System.out.println("EmailController: Updated verification code for email: " + email);

                // 인증 코드 이메일로 전송
                emailService.sendVerificationEmail(email, newCode);

                return ResponseEntity.ok("Verification code updated and sent to: " + email);
            } else {
                memberService.sendVerificationCodeToEmail(email);
                return ResponseEntity.ok("Email is available. Verification code sent to email.");
            }
        }
    }

    //이메일 검증
    @PatchMapping("/verify-email")
    public ResponseEntity<String> verifyEmail(@RequestParam String email, @RequestParam String code) {
        int codeInt = Integer.parseInt(code);
        boolean isVerified = memberService.verifyEmailCode(email, codeInt);

        if (isVerified) {
            return ResponseEntity.ok("Email verified successfully");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification code");
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> registerMember(@RequestBody SignupRequest signupRequest) {
        // 이메일 인증 정보 가져오기
        EmailVerification optionalEmailVerification = emailVerificationRepository.findEmailVerificationByEmail(signupRequest.getEmail());

        if (optionalEmailVerification == null || !optionalEmailVerification.isVerifiedEmail()) {
            return ResponseEntity.badRequest().body(null);
        }

        if (memberService.checkLoginIdExists(signupRequest.getLoginId())) {
            return ResponseEntity.badRequest().body(null);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupRequest.getLoginPwd());
        signupRequest.setLoginPwd(encodedPassword);

        // 회원 등록
        memberService.registerMember(signupRequest);

        // 회원 정보 가져오기 - 회원가입 후 자동 로그인
        Member member = memberService.getMemberById(signupRequest.getLoginId());
        if (member == null) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        // JWT 토큰 생성
        String token = jwtUtil.generateToken(member.getLoginId());

        // 응답 반환 - 토큰만 반환
        SignupResponse response = new SignupResponse(token);
        return ResponseEntity.ok(response);
    }

    //회원 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteMember() {
        memberService.deleteCurrentUser();
        return ResponseEntity.ok("Your account has been deleted.");
    }

    //닉네임 변경
    @PatchMapping("/change_nickname")
    public ResponseEntity<String> changeNickname(@RequestBody NicknameChangeRequest request) {
        memberService.changeNickname(request.getNewNickname());
        return ResponseEntity.ok("Nickname has been changed successfully.");
    }

    //아이콘 변경
    @PatchMapping("/change_icon")
    public ResponseEntity<String> changeIcon(@RequestBody IconChangeRequest request) {
        memberService.changeIcon(request.getNewIcon());
        return ResponseEntity.ok("Icon has been changed successfully.");
    }



    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        System.out.println("RequestBody received: " + loginRequest);

        try {
            System.out.println("Login request received for ID: " + loginRequest.getLogin_id());
            Member member = memberService.getMemberById(loginRequest.getLogin_id());

            if (member != null) {
                System.out.println("Member found : " + loginRequest.getLogin_id());
                if (passwordEncoder.matches(loginRequest.getLogin_pwd(), member.getLoginPwd())) {
                    String token;
                    if (loginRequest.isAutoLogin()) {
                        System.out.println("Generating token with extended expiry for ID: " + loginRequest.getLogin_id());
                        token = jwtUtil.generateTokenWithExtendedExpiry(member.getLoginId());
                    } else {
                        System.out.println("Generating token for ID: " + loginRequest.getLogin_id());
                        token = jwtUtil.generateToken(member.getLoginId());
                    }
                    LoginResponse response = new LoginResponse("Login successful", member.getNickname(), token, member.getIcon());

                    System.out.println("Login successful for ID: " + loginRequest.getLogin_id());
                    return ResponseEntity.ok(response);
                } else {
                    System.out.println("Invalid login_pwd for ID: " + loginRequest.getLogin_id());
                    return ResponseEntity.status(401).body(new LoginResponse("Invalid login_pwd.", null, null, null));
                }
            } else {
                System.out.println("Invalid login_id: " + loginRequest.getLogin_id());
                return ResponseEntity.status(401).body(new LoginResponse("Invalid login_id.", null, null, null));
            }
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(500).body(new LoginResponse("Error during login: " + e.getMessage(), null, null, null));
        }
    }

    @PostMapping("/google-login")
    public ResponseEntity<LoginResponse> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            System.out.println("ID Token received: " + request.getIdToken());
            Member member = googleLoginService.processGoogleLogin(request);

            if (member != null) {
                System.out.println("Member found : " + member.getNickname());
                String token = jwtUtil.generateToken(member.getLoginId());
                LoginResponse response = new LoginResponse("Login successful", member.getNickname(), token, member.getIcon());
                return ResponseEntity.ok(response);
            } else {
                System.out.println("Failed to process Google login request");
                return ResponseEntity.status(401).body(new LoginResponse("Login failed for Google ID", null, null, null));
            }

        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return ResponseEntity.status(500).body(new LoginResponse("Error during login: " + e.getMessage(), null, null, null));
        }
    }


    @PostMapping("/kakao-login")
    public ResponseEntity<LoginResponse> kakaoLogin(@RequestBody KakaoLoginRequest request) {
        try {
            System.out.println("Kakao access token received: " + request.getAccessToken());

            // 카카오 사용자 정보 가져오기
            KakaoProfile kakaoProfile = kakaoService.getKakaoProfile(request.getAccessToken());

            // 사용자 정보로 로그인 처리
            Member member = memberService.processKakaoLogin(kakaoProfile);

            if (member != null) {
                // JWT 토큰 생성
                String token = jwtUtil.generateToken(member.getLoginId());
                System.out.println("Member found : " + member.getNickname());
                LoginResponse response = new LoginResponse("Login successful", member.getNickname(), token, member.getIcon());
                return ResponseEntity.ok(response);
            } else {
                System.out.println("Failed to process Kakao login request");
                return ResponseEntity.status(401).body(new LoginResponse("Login failed for Kakao ID", null, null, null));
            }

        } catch (Exception e) {
            System.err.println("Error during Kakao login: " + e.getMessage());
            return ResponseEntity.status(500).body(new LoginResponse("Error during Kakao login: " + e.getMessage(), null, null, null));
        }
    }


    // 아이디 찾기
    @GetMapping("/find/loginId")
    public ResponseEntity<String> findLoginId(@RequestParam String email, @RequestParam String birthdate) {
        System.out.println("Received request to find login ID");
        try {
            LocalDate birthDateLocal = LocalDate.parse(birthdate);
            String loginId = memberService.findLoginId(email, birthDateLocal);
            System.out.println("Found ID: " + loginId);
            return ResponseEntity.ok(loginId);  // 로그인 ID를 직접 반환
        } catch (RuntimeException e) {
            System.err.println("Error finding login ID: " + e.getMessage());
            return ResponseEntity.badRequest().body("Login ID not found.");
        }
    }

    // 아이디찾기 - 이메일 보내기
    @PatchMapping("/find/send/email")
    public ResponseEntity<String> updateVerificationCode(@RequestParam String email) {
        System.out.println("EmailController: Updating verification code for email: " + email);
        try {
            // 새로운 인증 코드 생성
            int newCode = emailService.generateVerificationCode();

            // 이메일로 기존의 인증 레코드를 가져옴
            Optional<EmailVerification> optionalVerification = emailVerificationRepository.findTopByEmailOrderByCreateTimeDesc(email);


            if (optionalVerification.isPresent()) {
                // 기존 레코드가 있으면 인증번호만 업데이트
                EmailVerification existingVerification = optionalVerification.get();
                existingVerification.setVerificationCode(newCode);
                existingVerification.setCreateTime(LocalDateTime.now()); // 생성 시간 갱신
                emailVerificationRepository.save(existingVerification);
                System.out.println("EmailController: Updated verification code for email: " + email);

                // 인증 코드 이메일로 전송
                emailService.sendVerificationEmail(email, newCode);

                return ResponseEntity.ok("Verification code updated and sent to: " + email);
            } else {
                return ResponseEntity.status(404).body("No verification record found for email: " + email);
            }
        } catch (Exception e) {
            System.err.println("Error updating verification code: " + e.getMessage());
            return ResponseEntity.status(500).body("Error updating verification code: " + e.getMessage());
        }
    }

    // 이메일로 보낸 인증 코드를 검증하는 API
    @GetMapping("/find/email/verify")
    public ResponseEntity<String> verifyCodeFindId(@RequestParam String email, @RequestParam String code) {
        System.out.println("EmailVerificationController: Verifying code for email: " + email + ", code: " + code);
        int codeInt = Integer.parseInt(code);
        boolean codeValid = emailService.verifyEmailCodeByEmail(email, codeInt);
        if (codeValid) {
            return ResponseEntity.ok("Verification code valid.");
        } else {
            System.out.println("EmailVerificationController: Invalid verification code for email: " + email);
            return ResponseEntity.status(400).body("Invalid verification code.");
        }
    }

    // 비번 재설정 - 아이디 찾기를 통해 회원의 존재 여부 확인 및 이메일 반환
    @PostMapping("/reset/password/loginId")
    public ResponseEntity<String> initiatePasswordReset(@RequestParam String login_id) {
        System.out.println("LoginController: Initiating password reset for loginId: " + login_id);
        try {
            Member member = memberService.getMemberById(login_id);
            if (member != null) {
                // 회원이 존재하면 이메일을 반환
                String email = member.getEmail();
                System.out.println("LoginController: Member found. Email: " + email);
                return ResponseEntity.ok(email); // 이메일을 직접 반환
            } else {
                System.out.println("LoginController: User not found for loginId: " + login_id);
                return ResponseEntity.status(404).body("User not found.");
            }
        } catch (Exception e) {
            System.err.println("Error during password reset initiation: " + e.getMessage());
            return ResponseEntity.status(500).body("Error during password reset initiation.");
        }
    }

    @PatchMapping("/reset/password/change")
    public ResponseEntity<String> changePassword(@RequestParam String email, @RequestParam String new_password, @RequestParam String code) {
        System.out.println("LoginController: Changing password for email: " + email + ", code: " + code);
        int codeInt = Integer.parseInt(code);
        // 인증 코드 검증 (verifiedEmail 필드 상태를 무시하고 검증)
        boolean codeValid = emailService.verifyEmailCodeByEmail(email, codeInt);
        if (!codeValid) {
            System.out.println("LoginController: Invalid verification code for email: " + email);
            return ResponseEntity.status(400).body("Invalid verification code.");
        }

        // 비밀번호 변경
        boolean passwordChanged = memberService.changeUserPassword(email, new_password);
        if (passwordChanged) {
            // 인증 코드 무효화 (실제로는 verifiedEmail을 true로 설정)
            emailService.invalidateVerificationCode(email, code);
            System.out.println("LoginController: Password changed successfully for email: " + email);
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            System.out.println("LoginController: Failed to change password for email: " + email);
            return ResponseEntity.status(400).body("Failed to change password.");
        }
    }

    @PatchMapping("/update-skill-score")
    public ResponseEntity<UpdateSkillScoreResponse> updateSkillScore(@RequestBody UpdateSkillScoreRequest request) {
        try {
            // JWT에서 로그인 ID 추출
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String loginId = authentication.getName();  // 'getName()' 사용

            // 점수 및 Rank 업데이트
            Member updatedMember = memberService.updateSkillScoreAndRank(loginId, request.getNewScore());

            // 응답 생성
            UpdateSkillScoreResponse response = new UpdateSkillScoreResponse();
            response.setRankLevel(updatedMember.getRank().name());  // Rank enum 값을 문자열로 반환
            response.setSkillScore(updatedMember.getSkillScore());
            response.setNickname(updatedMember.getNickname());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

}
