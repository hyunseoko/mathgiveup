package hanium.smath.Member.config;

import hanium.smath.Member.security.JwtRequestFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
        System.out.println("JwtRequestFilter instance injected: " + jwtRequestFilter);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring SecurityFilterChain...");
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests
                        .requestMatchers("/api/members/**").permitAll()
                        .requestMatchers("/api/learning/**").hasRole("USER") // 여기를 확인
                        .requestMatchers("/api/achievements/**").hasRole("USER")
                        .requestMatchers("/api/community/posts/**").hasRole("USER")
                        .requestMatchers("/api/community/comments/**").hasRole("USER")

                        // 공지사항 관련 권한 설정
                        .requestMatchers("/notices/new", "/notices/edit/**", "/notices/delete/**").hasRole("ADMIN")  // 관리자만 접근 가능
                        .anyRequest().permitAll()
                )

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )

                // 서버 세션 유지
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                // 카카오 소셜 로그인 설정 추가
                .oauth2Login(oauth2 -> oauth2
                        .defaultSuccessUrl("/loginSuccess")  // 로그인 성공 후 리디렉션할 URL
                        .failureUrl("/loginFailure")         // 로그인 실패 후 리디렉션할 URL
                );


        // JwtRequestFilter를 UsernamePasswordAuthenticationFilter 전에 추가합니다.
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        System.out.println("SecurityFilterChain configured.");
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        System.out.println("Configuring PasswordEncoder...");
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("Access Denied!");
        };
    }

    @Bean
    public Http403ForbiddenEntryPoint authenticationEntryPoint() {
        return new Http403ForbiddenEntryPoint();
    }
}