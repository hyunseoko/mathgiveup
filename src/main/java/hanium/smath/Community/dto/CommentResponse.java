package hanium.smath.Community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentResponse {
    private Long commentId;
    private String content;
    private Long postId;
    private String loginId;
    private String nickname;
    private String createTime;
    private String updateTime;
}
