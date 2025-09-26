package com.ssdevcheckincheckout.ssdev.Backend.config;

//1️⃣ Global CORS Configuration
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {

 @Bean
 public WebMvcConfigurer corsConfigurer() {
     return new WebMvcConfigurer() {
         @Override
         public void addCorsMappings(CorsRegistry registry) {
             registry.addMapping("/**") // allow all endpoints
//                     .allowedOrigins("http://localhost:3000") // frontend origin
             		 .allowedOrigins("https://masterclassbookings-rt5n.vercel.app/")
                     .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                     .allowedHeaders("*")
                     .allowCredentials(true);
         }
     };
 }
}

