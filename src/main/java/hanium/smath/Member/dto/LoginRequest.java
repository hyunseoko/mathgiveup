package hanium.smath.Member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LoginRequest {
    private String login_id;
    private String login_pwd;
    private boolean autoLogin;

    public LoginRequest() {}

    public LoginRequest(String login_id, String login_pwd, boolean autoLogin) {
        this.login_id = login_id;
        this.login_pwd = login_pwd;
        this.autoLogin = autoLogin;
    }

}
