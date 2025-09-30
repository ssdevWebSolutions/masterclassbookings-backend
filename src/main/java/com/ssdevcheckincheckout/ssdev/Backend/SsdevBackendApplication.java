package com.ssdevcheckincheckout.ssdev.Backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SsdevBackendApplication {

	public static void main(String[] args) {
		 System.out.println("PORT env variable: " + System.getenv("PORT"));
		SpringApplication.run(SsdevBackendApplication.class, args);
	}

}
