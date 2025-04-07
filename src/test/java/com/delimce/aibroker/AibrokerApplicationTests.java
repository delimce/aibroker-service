package com.delimce.aibroker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;

@SpringBootTest
@ActiveProfiles("test")
@Import(AibrokerApplicationTestConfig.class)
class AibrokerApplicationTests {

	@Test
	void contextLoads() {
	}

}
