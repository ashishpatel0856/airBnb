package com.ashish.projects.airBnb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class airBnbAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(airBnbAppApplication.class, args);
	}

}
