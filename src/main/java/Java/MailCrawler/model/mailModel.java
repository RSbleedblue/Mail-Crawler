package Java.MailCrawler.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "mails")

public class mailModel {
    @Id
    private int mailNumber;
    private String date;
    private String From;
    private String Project;
    private String Task_Done;
    private String Task_ToDo;
    private String Task_pending;
}
