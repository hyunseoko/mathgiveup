package hanium.smath.Member.config;
import hanium.smath.Member.interceptor.LastLoginTimeInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LastLoginTimeInterceptor lastLoginTimeInterceptor;

    @Autowired
    public WebConfig(LastLoginTimeInterceptor lastLoginTimeInterceptor) {
        this.lastLoginTimeInterceptor = lastLoginTimeInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(lastLoginTimeInterceptor).addPathPatterns("/**");
    }
}