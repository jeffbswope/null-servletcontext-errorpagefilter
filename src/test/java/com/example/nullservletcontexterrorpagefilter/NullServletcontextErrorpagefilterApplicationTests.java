package com.example.nullservletcontexterrorpagefilter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class NullServletcontextErrorpagefilterApplicationTests {
	@Autowired
	private TestRestTemplate testRestTemplate;

	@Test
	public void anonIs401() {
		assertThat(testRestTemplate
				.exchange("/fobs", HttpMethod.GET, null, Object.class)).satisfies(entity -> {
			assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
		});
	}

	@Test
	public void noAuthIs403() {
		assertThat(testRestTemplate
				.withBasicAuth("other", "other")
				.exchange("/fobs", HttpMethod.GET, null, Object.class)).satisfies(entity -> {
			assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
		});
	}

	@Test
	public void authIs200() {
		assertThat(testRestTemplate
				.withBasicAuth("fobclient", "fobclient")
				.exchange("/fobs", HttpMethod.GET, null, Object.class)).satisfies(entity -> {
			assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
		});
	}
}
