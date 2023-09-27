package com.latti31.springeventserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringEventServerApplication {

	public static void main(String[] args) {
		System.out.println("Running main");
		SpringApplication.run(SpringEventServerApplication.class, args);
	}
}