package com.pageon.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PageonBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(PageonBackendApplication.class, args);
	}

}
