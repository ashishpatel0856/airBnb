package com.ashish.projects.VrboApp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class VrboAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(VrboAppApplication.class, args);
	}

}
