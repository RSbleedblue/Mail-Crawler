package Java.MailCrawler.util;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.Credentials;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.common.collect.Lists;

import javax.security.sasl.AuthorizeCallback;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SheetsServiceUtil {

    private static final String APPLICATION_NAME = "MailCrawler";
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";
    public static Credential getSheetsService() throws IOException, GeneralSecurityException {
        InputStream credentialsStream = SheetsServiceUtil.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                JacksonFactory.getDefaultInstance(),new InputStreamReader(credentialsStream)
        );
        List<String> scopes = Arrays.asList(SheetsScopes.SPREADSHEETS);

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance(),
                clientSecrets, Arrays.asList(
                SheetsScopes.SPREADSHEETS,
                "https://www.googleapis.com/auth/spreadsheets"
        )
        )
                .setDataStoreFactory(new FileDataStoreFactory(new File("tokens")))
                .setAccessType("offline")
                .build();

        return new AuthorizationCodeInstalledApp(
                flow,new LocalServerReceiver()).authorize("rivansh63@gmail.com");
    }
    public static Sheets getSheetService() throws GeneralSecurityException, IOException {
        Credential credential = getSheetsService();
        return new Sheets.Builder(GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance(),credential).setApplicationName(APPLICATION_NAME).build();
    }
}
