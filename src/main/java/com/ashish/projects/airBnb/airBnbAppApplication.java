package com.ashish.projects.airBnb;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true)

public class airBnbAppApplication {

	public static void main(String[] args) {

		SpringApplication.run(airBnbAppApplication.class, args);

	}

}
