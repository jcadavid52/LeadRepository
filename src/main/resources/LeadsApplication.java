package desarrolloLocal.leads;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class LeadsApplication {

	public static void main(String[] args) {
		SpringApplication.run(LeadsApplication.class, args);
	}

}
