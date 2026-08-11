package com.aubrian.bank_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BankApiApplication {

	public static void main(String[] args) {
		Dotenv.configure().systemProperties().load();
		SpringApplication.run(BankApiApplication.class, args);
	}

}
