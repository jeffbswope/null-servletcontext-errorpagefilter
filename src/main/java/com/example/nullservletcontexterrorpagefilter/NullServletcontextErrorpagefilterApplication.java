package com.example.nullservletcontexterrorpagefilter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Stream;

@SpringBootApplication
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class NullServletcontextErrorpagefilterApplication {

	public static void main(String[] args) {
		SpringApplication.run(NullServletcontextErrorpagefilterApplication.class, args);
	}

	@RestController
	@RequestMapping("/fobs")
	@PreAuthorize("hasRole('FOB_MANAGER')")
	public static class FobController {
		private final List<Fob> fobs = Stream.of(new Fob("ALPHA", "Alpha"), new Fob("BETA", "beta"), new Fob("CHARLIE", "charlie")).toList();

		@GetMapping
		public List<Fob> getFobs() {
			return fobs;
		}
	}

	public record Fob(String key, String value) {

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}
	}

}
