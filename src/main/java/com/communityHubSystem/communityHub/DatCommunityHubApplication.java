package com.communityHubSystem.communityHub;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DatCommunityHubApplication {

	public static void main(String[] args) {
		SpringApplication.run(DatCommunityHubApplication.class, args);
	}

}
