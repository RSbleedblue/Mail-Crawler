package Java.MailCrawler.controller;

import Java.MailCrawler.service.mailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mail")
public class mailController {
    @Autowired
    private mailService mailService;
    @GetMapping("/getMails")
    public ResponseEntity<Map<String,String >> getMails(){
        boolean result = mailService.storeMail();
        Map<String,String> map = new HashMap<>();
        if(result){
            map.put("success: ","True");
            map.put("message: ", "Database successfully updated");
            return ResponseEntity.ok(map);
        }
        else{
            map.put("success: ", "False");
            map.put("message: ", "Database failed to update");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }
    @GetMapping("/getSpreadSheet")
    public ResponseEntity<Map<String,String>> getLink(){
        String spreadSheetLink = mailService.generateSpreadSheetLink();
        Map<String,String> map = new HashMap<>();
        if(spreadSheetLink!=null && !spreadSheetLink.isEmpty()){
            map.put("success: ","True");
            map.put("link: ",spreadSheetLink);
            return ResponseEntity.ok(map);
        }
        else{
            map.put("success: ", "False");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);
        }
    }
}
