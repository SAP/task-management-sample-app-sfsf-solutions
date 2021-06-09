package com.sap.core.extensions.employeeonboarding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({ "com.sap.cloud.sdk", "com.sap.core.extensions.employeeonboarding" })
@ServletComponentScan({ "com.sap.cloud.sdk", "com.sap.core.extensions.employeeonboarding" }) // SDK Configuration
																								// Documentation
																								// https://sap.github.io/cloud-sdk/docs/java/guides/cap-sdk-integration/#enable-the-component-scan-for-sap-cloud-sdk-package

public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
