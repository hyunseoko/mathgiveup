package hanium.smath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "hanium.smath")
public class SmathApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmathApplication.class, args);
    }
}

