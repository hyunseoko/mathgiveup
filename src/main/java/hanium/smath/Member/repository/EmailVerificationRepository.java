package hanium.smath.Member.repository;

import hanium.smath.Member.entity.EmailVerification;
import hanium.smath.Member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

//이메일 인증 코드를 데이터베이스에서 관리하는 리포지토리
@Repository
public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
    Optional<EmailVerification> findByMember_LoginIdAndVerifiedEmailFalse(String loginId);
    Optional<EmailVerification> findTopByMember_LoginIdAndVerifiedEmailFalseOrderByCreateTimeDesc(String loginId);
    Optional<EmailVerification> findTopByEmailAndVerifiedEmailFalseOrderByCreateTimeDesc(String email);
    EmailVerification findEmailVerificationByEmail(String email);
    Optional<EmailVerification> findTopByEmailOrderByCreateTimeDesc(String email);

    // 추가된 메서드: Member 엔티티를 사용하여 검색
    Optional<EmailVerification> findByMember(Member member);
    void deleteByEmail(String email);
}
