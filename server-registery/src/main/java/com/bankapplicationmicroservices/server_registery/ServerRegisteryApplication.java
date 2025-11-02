package com.bankapplicationmicroservices.server_registery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@EnableEurekaServer
@SpringBootApplication
public class ServerRegisteryApplication {

	public static void main(String[] args) {
		SpringApplication.run(ServerRegisteryApplication.class, args);
	}

}
