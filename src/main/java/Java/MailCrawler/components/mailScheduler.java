package Java.MailCrawler.components;

import Java.MailCrawler.service.mailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class mailScheduler {
    private mailService mailService;
    @Autowired
    private mailScheduler(mailService mailService){
        this.mailService = mailService;
    }
    @Scheduled(fixedDelay = 10000)
    public void checkMail() throws IOException {
        mailService.storeMail();
        String SpreadSheetLink = mailService.generateSpreadSheetLink();
        System.out.println("Database Updated!");
        System.out.println("SpreadSheet Link: "+ SpreadSheetLink);

    }
}
