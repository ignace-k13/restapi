package be.abis.exercise;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class Exercise54apiApplication {

    public static void main(String[] args) {
        SpringApplication.run(Exercise54apiApplication.class, args);
    }

}
