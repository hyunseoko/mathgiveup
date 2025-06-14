package hanium.smath.notice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class NoticeResponse {
    private Long id; // 게시글 고유 번호
    private String title;
    private String content;
    private String nickname;
    private LocalDateTime createTime;

    // public 생성자 추가
    public NoticeResponse(Long id, String title, String content, String nickname, LocalDateTime createTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.nickname = nickname;
        this.createTime = createTime;
    }
}
