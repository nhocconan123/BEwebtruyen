package com.truyen.webtruyen;

import com.truyen.webtruyen.config.OtpProperties;
import com.truyen.webtruyen.config.SpringMailProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({OtpProperties.class, SpringMailProperties.class})
public class WebtruyenApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebtruyenApplication.class, args);
	}

}
