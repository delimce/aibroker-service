package com.delimce.aibroker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AibrokerApplication {

	public static void main(String[] args) {
		SpringApplication.run(AibrokerApplication.class, args);
	}

}
