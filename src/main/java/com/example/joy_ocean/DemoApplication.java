package com.example.joy_ocean;

import java.util.TimeZone;

import javax.annotation.PostConstruct;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;


@SpringBootApplication
@EntityScan(basePackageClasses = {DemoApplication.class, Jsr310JpaConverters.class})
public class DemoApplication {

	@PostConstruct
	@JsonFormat(timezone = "Asia/Seoul")
	void init(){
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
