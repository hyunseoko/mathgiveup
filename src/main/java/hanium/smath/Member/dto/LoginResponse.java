package hanium.smath.Member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String message;
    private String nickname;
    private String token;
    private String icon;

    public LoginResponse(String message, String nickname, String token, String icon) {
        this.message = message;
        this.nickname = nickname;
        this.token = token;
        this.icon = icon;
    }
}
