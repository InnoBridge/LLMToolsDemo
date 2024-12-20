package io.github.innobridge.llmtoolsdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackageClasses = {
    io.github.innobridge.llmtoolsdemo.configuration.ApplicationSpecificSpringComponentScanMarker.class,
    io.github.innobridge.llmtoolsdemo.controller.ApplicationSpecificSpringComponentScanMarker.class
})
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
