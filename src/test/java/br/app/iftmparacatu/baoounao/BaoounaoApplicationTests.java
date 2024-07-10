package br.app.iftmparacatu.baoounao;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;

@SpringBootTest
@PropertySource("classpath:env/local.properties")
class BaoounaoApplicationTests {

	@Test
	void contextLoads() {
	}

}
