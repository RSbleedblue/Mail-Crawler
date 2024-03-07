package Java.MailCrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MailCrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MailCrawlerApplication.class, args);
	}

}
