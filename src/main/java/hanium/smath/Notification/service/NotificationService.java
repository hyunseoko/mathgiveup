package hanium.smath.Notification.service;

import hanium.smath.Member.entity.Member;
import hanium.smath.Member.service.MemberService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final MemberService memberService;

    public NotificationService(MemberService memberService) {
        this.memberService = memberService;
    }

    // 사용자가 알림을 수신하도록 SSE 연결
    public SseEmitter subscribe(Long memberId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.put(memberId, emitter);

        emitter.onCompletion(() -> emitters.remove(memberId));
        emitter.onTimeout(() -> emitters.remove(memberId));

        return emitter;
    }

    // 24시간 동안 로그인하지 않은 사용자에게 알림 발송
    @Scheduled(fixedRate = 3600000)  // 1시간마다 실행
    public void sendNotifications() {
        List<Member> inactiveMembers = memberService.findMembersInactiveFor24Hours();
        for (Member member : inactiveMembers) {
            SseEmitter emitter = emitters.get(member.getIdMember());
            if (emitter != null) {
                try {
                    emitter.send(SseEmitter.event().name("notification").data("오늘 공부할 시간이 다가왔어요!"));
                } catch (IOException e) {
                    emitters.remove(member.getIdMember());
                }
            }
        }
    }
}
