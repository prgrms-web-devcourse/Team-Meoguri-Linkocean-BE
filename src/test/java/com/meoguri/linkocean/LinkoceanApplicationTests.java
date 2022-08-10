package com.meoguri.linkocean;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.test.context.ActiveProfiles;

@EnableAspectJAutoProxy
@SpringBootTest
@ActiveProfiles("local")
class LinkoceanApplicationTests {

	@Test
	void contextLoads() {
	}

}
