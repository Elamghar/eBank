package org.sid.ebankingbackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "org.sid.ebankingbackend.repositories")
public class EbankingBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(EbankingBackendApplication.class, args);
    }
}