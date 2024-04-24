package com.example.testBackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class TestBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(TestBackendApplication.class, args);
	}
}
