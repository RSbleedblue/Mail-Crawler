package Java.MailCrawler.repository;

import Java.MailCrawler.model.mailModel;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface mailRepository extends MongoRepository<mailModel, String> {
}
