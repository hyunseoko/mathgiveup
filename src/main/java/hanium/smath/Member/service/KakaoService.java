package hanium.smath.Member.service;

import hanium.smath.Member.dto.KakaoProfile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoService {

    private static final String KAKAO_USERINFO_URL = "https://kapi.kakao.com/v2/user/me";  // 카카오 사용자 정보 요청 URL

    public KakaoProfile getKakaoProfile(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);  // 액세스 토큰을 Authorization 헤더에 추가
        HttpEntity<String> entity = new HttpEntity<>("", headers);

        ResponseEntity<KakaoProfile> response = restTemplate.exchange(
                KAKAO_USERINFO_URL,
                HttpMethod.GET,
                entity,
                KakaoProfile.class
        );

        return response.getBody();
    }
}
