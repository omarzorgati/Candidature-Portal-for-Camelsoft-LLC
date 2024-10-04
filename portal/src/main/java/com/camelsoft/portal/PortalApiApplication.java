package com.camelsoft.portal;

import com.camelsoft.portal.models.Role;
import com.camelsoft.portal.repositories.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableJpaAuditing
@EnableAsync
public class PortalApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(PortalApiApplication.class, args);
	}

}
