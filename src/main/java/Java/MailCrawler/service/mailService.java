package Java.MailCrawler.service;

import Java.MailCrawler.model.mailModel;
import Java.MailCrawler.repository.mailRepository;
import Java.MailCrawler.util.SheetsServiceUtil;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.google.api.services.sheets.v4.model.SpreadsheetProperties;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.apache.commons.mail.util.MimeMessageParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@Service
public class mailService {
    @Autowired
    private mailRepository mailRepo;
    private Sheets sheetsService;

    public mailService() {
        try {
            this.sheetsService = SheetsServiceUtil.getSheetService();
        } catch (IOException | GeneralSecurityException e) {
            e.printStackTrace();
            // Handle exception accordingly
        }
    }
    public boolean storeMail(){
        Properties props = new Properties();
        props.put("mail.imap.host", "imap.gmail.com");
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.ssl.trust", "imap.gmail.com");
        try{
            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", "rivansh63@gmail.com", "fesk cphm wfdc fpsr");
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);

            int totalMessages = inbox.getMessageCount();
            int startMessage = Math.max(1, totalMessages - 10);

            Message[] messages = inbox.getMessages(startMessage,totalMessages);
            for(Message eachMessage : messages){
                if(eachMessage.getSubject()!=null && eachMessage.getSubject().startsWith("DSR")){
                    int mailNumber = eachMessage.getMessageNumber();
                    String date = eachMessage.getReceivedDate().toString();
                    String from = eachMessage.getFrom()[0].toString();
                    String content = getContent(eachMessage);
                    String[] trimContent = trimContent(content);
                    createDB(mailNumber,date,from,trimContent[0],trimContent[1],trimContent[2],trimContent[3]);
                }
            }
            inbox.close(false);
            store.close();
            return true;
        }
        catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        catch (Exception e){
            return false;
        }
    }
    public String generateSpreadSheetLink() throws IOException {
        List<mailModel> mailData = mailRepo.findAll();
        Spreadsheet spreadsheet = createSpreadsheet();
        spreadsheet.setSpreadsheetId("1Vy4rExSGZ8tyCGH6KqCvSuSBiybHI8NYhrkbKxvt_Go");
        addDataToSpreadSheet(spreadsheet.getSpreadsheetId(),mailData);
        String spreadsheetLink = "https://docs.google.com/spreadsheets/d/" + spreadsheet.getSpreadsheetId() + "/edit";
        return spreadsheetLink;
    }
    private Spreadsheet createSpreadsheet() throws IOException {
        Spreadsheet spreadsheet = new Spreadsheet().setProperties(
                new SpreadsheetProperties().setTitle("Mail Data")
        );
        spreadsheet = sheetsService.spreadsheets().create(spreadsheet).execute();
        return spreadsheet;
    }
    private void addDataToSpreadSheet(String spreadSheetID, List<mailModel> mailData) throws IOException {
        List<List<Object>> values = prepareDataForSpreadsheet(mailData);

        // Write data to the spreadsheet
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .update(spreadSheetID, "A1", body)
                .setValueInputOption("RAW")
                .execute();
    }
    private List<List<Object>> prepareDataForSpreadsheet(List<mailModel> mailData){
        List<List<Object>> values = new ArrayList<>();

        List<Object> columnNames = Arrays.asList("Mail Number", "Date", "From", "Project", "Tasks Done", "Tasks Todo", "Tasks onHold");
        values.add(columnNames);

        // Add mail data
        for(mailModel mail: mailData){
            List<Object> row = new ArrayList<>();
            row.add(mail.getMailNumber());
            row.add(mail.getDate());
            row.add(mail.getFrom());
            row.add(mail.getProject());
            row.add(mail.getTask_Done());
            row.add(mail.getTask_ToDo());
            row.add(mail.getTask_pending());
            values.add(row);
        }
        return values;
    }
    private String getContent(Message eachMessage) throws MessagingException, IOException {
        MimeMessageParser parser = new MimeMessageParser((MimeMessage) eachMessage);
        parser.parse();
        return parser.getPlainContent();
    }
    private String[] trimContent(String data){
        String[] lines = data.split("\n");
        String[] content = new String[4];

        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length == 2) {
                String key = parts[0].trim();
                String value = parts[1].trim();
                switch (key) {
                    case "Project":
                        content[0] = value;
                        break;
                    case "Tasks Done":
                        content[1] = value;
                        break;
                    case "Tasks Todo":
                        content[2] = value;
                        break;
                    case "Tasks onHold":
                        content[3] = value;
                        break;
                }
            }
        }
        return content;
    }
    private void createDB(int mailNumber, String date, String From, String Project, String Task_Done, String Task_ToDo, String Task_pending){
        mailModel mail = new mailModel(mailNumber,date,From,Project,Task_Done,Task_ToDo,Task_pending);
        mailRepo.save(mail);
    }

}
