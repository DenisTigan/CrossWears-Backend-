package com.example.ecom.project;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@PostConstruct
	public void init() {
		// Setăm fusul orar default pe România (Bucharest)
		TimeZone.setDefault(TimeZone.getTimeZone("Europe/Bucharest"));
		System.out.println("Timezone setat la: " + TimeZone.getDefault().getID());
	}
}
