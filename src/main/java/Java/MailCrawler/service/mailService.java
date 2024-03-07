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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                if(eachMessage.getSubject()!=null && eachMessage.getSubject().toLowerCase().contains("dsr")){
                    int mailNumber = eachMessage.getMessageNumber();
                    String date = eachMessage.getReceivedDate().toString();
                    String from = eachMessage.getFrom()[0].toString();
                    String content = getContent(eachMessage);
                    String[] trimContent = trimContent(content);
                    String project = getProjectName(eachMessage.getSubject());
                    createDB(mailNumber,date,from,project,trimContent[0],trimContent[1],trimContent[2]);
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
    public boolean userMail(String username, String password){
        Properties props = new Properties();
        props.put("mail.imap.host", "imap.gmail.com");
        props.put("mail.imap.port", "993");
        props.put("mail.imap.ssl.enable", "true");
        props.put("mail.imap.ssl.trust", "imap.gmail.com");
        try{
            Session session = Session.getDefaultInstance(props);
            Store store = session.getStore("imaps");
            store.connect("imap.gmail.com", username, password);
            Folder inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            int totalMessages = inbox.getMessageCount();
            int startMessage = Math.max(1, totalMessages - 10);

            Message[] messages = inbox.getMessages(startMessage,totalMessages);
            for(Message eachMessage : messages){
                if(eachMessage.getSubject()!=null && eachMessage.getSubject().toLowerCase().contains("dsr")){
                    int mailNumber = eachMessage.getMessageNumber();
                    String date = eachMessage.getReceivedDate().toString();
                    String from = eachMessage.getFrom()[0].toString();
                    String content = getContent(eachMessage);
                    String[] trimContent = trimContent(content);
                    String project = getProjectName(eachMessage.getSubject());
                    createDB(mailNumber,date,from,project,trimContent[0],trimContent[1],trimContent[2]);
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
//    Project-Test:DSR
    private String getProjectName(String str){
        str = str.toLowerCase();
        String patternString = "project-(.*)dsr";
        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        if(matcher.find()){
            String extractedText = matcher.group(1);
            return extractedText.toUpperCase();
        }
        else{
            return "NA";
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
    private String[] trimContent(String data) {
        String[] lines = data.split("\n");
        String[] content = new String[3];
        boolean isTasksDoneSection = false;
        boolean isTasksTodoSection = false;
        boolean isTasksOnHoldSection = false;

        for (String line : lines) {
            line = line.trim();
            if (line.toLowerCase().startsWith("tasks done")) {
                isTasksDoneSection = true;
                continue;
            } else if (line.toLowerCase().startsWith("tasks todo") || line.toLowerCase().startsWith("tasks to do")) {
                isTasksTodoSection = true;
                isTasksDoneSection = false;
                continue;
            } else if (line.toLowerCase().startsWith("tasks onhold") || line.toLowerCase().startsWith("tasks on hold")) {
                isTasksOnHoldSection = true;
                isTasksTodoSection = false;
                isTasksDoneSection = false;
                continue;
            }

            if (isTasksDoneSection) {
                if (content[0] == null) {
                    content[0] = line;
                } else {
                    content[0] += " " + line;
                }
            } else if (isTasksTodoSection) {
                if (content[1] == null) {
                    content[1] = line;
                } else {
                    content[1] += " " + line;
                }
            } else if (isTasksOnHoldSection) {
                if (content[2] == null) {
                    content[2] = line;
                } else {
                    content[2] += " " + line;
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
