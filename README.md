# Mail Crawler Project

## Overview
The Mail Crawler is a Java-based application designed to read emails from a specified mail server, extract relevant data, and store it in a MongoDB database. Additionally, it generates a Google Spreadsheet link containing the extracted data for easy access and sharing. The application is built using Spring Boot, with integrated Google Sheets API for data presentation.

## Features
- **Email Parsing**: Connects to an IMAP server and reads emails in a specified format.
- **Data Extraction**: Extracts relevant details such as mail number, date, sender, project name, tasks done, tasks to do, and tasks on hold.
- **Database Storage**: Stores the extracted data in a MongoDB database.
- **Google Sheets Integration**: Generates a Google Spreadsheet with the extracted data and provides a link for easy access.
- **User Authentication**: Supports user-specific email parsing through provided credentials.

## Tech Stack
- **Backend**: Spring Boot
- **Database**: MongoDB
- **Email Service**: JavaMail API
- **Google Sheets API**: For generating and updating spreadsheets
- **Frontend**: Not applicable (API-based interactions)
- **Others**: Lombok (for model classes), Maven (for dependency management)

## Prerequisites
- Java 8 or higher
- MongoDB instance
- Google Cloud project with Google Sheets API enabled
- Maven

## Setup Instructions
### 1. Clone the Repository
```sh
git clone https://github.com/yourusername/mail-crawler.git
cd mail-crawler
```

### 2. Configure Application Properties
Update the `application.properties` file with your MongoDB credentials and other configurations.

### 3. Google API Credentials
Place your `credentials.json` file (Google API credentials) in the `src/main/resources` directory. This file is necessary for accessing the Google Sheets API.

### 4. Build and Run the Application
Use Maven to build and run the application.
```sh
mvn clean install
mvn spring-boot:run
```

## API Endpoints
### 1. Store Mails
**Endpoint**: `/getMails`  
**Method**: GET  
**Description**: Connects to the email server using predefined credentials, extracts relevant data, and updates the MongoDB database.

### 2. Generate Spreadsheet Link
**Endpoint**: `/getSpreadSheet`  
**Method**: GET  
**Description**: Generates a Google Spreadsheet link containing the extracted email data.

### 3. User-Specific Mail Parsing
**Endpoint**: `/getMails`  
**Method**: POST  
**Description**: Connects to the email server using user-provided credentials, extracts relevant data, and updates the MongoDB database.  
**Request Body**:
```json
{
    "username": "user@example.com",
    "password": "userpassword"
}
```

## Project Structure
```
mail-crawler
├── src
│   ├── main
│   │   ├── java
│   │   │   └── Java
│   │   │       └── MailCrawler
│   │   │           ├── controller
│   │   │           │   └── mailController.java
│   │   │           ├── model
│   │   │           │   └── mailModel.java
│   │   │           ├── repository
│   │   │           │   └── mailRepository.java
│   │   │           └── service
│   │   │               └── mailService.java
│   │   └── resources
│   │       └── application.properties
├── pom.xml
└── README.md
```

## How It Works
1. **Credential Initialization**: Loads Google API credentials from the provided `credentials.json` file.
2. **Email Connection**: Connects to the IMAP server using JavaMail API with provided credentials.
3. **Email Parsing**: Reads emails, extracts relevant content based on specific keywords (like "DSR"), and formats the data.
4. **Database Storage**: Stores the extracted data into MongoDB.
5. **Google Sheets Update**: Creates/updates a Google Spreadsheet with the extracted data and generates a shareable link.

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments
- [Spring Boot](https://spring.io/projects/spring-boot)
- [MongoDB](https://www.mongodb.com/)
- [JavaMail API](https://javaee.github.io/javamail/)
- [Google Sheets API](https://developers.google.com/sheets/api)

Feel free to contribute to this project by submitting issues or pull requests.
